package com.xie.spot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.xie.spot.entity.City;
import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.User;
import com.xie.spot.pojo.PjInputResult;
import com.xie.spot.repository.CityRepository;
import com.xie.spot.repository.SpotBasicRepository;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.ChinesePinyin;
import com.xie.spot.sys.utils.ExcelHelper;
import com.xie.spot.sys.utils.ExcelRawData;
import com.xie.spot.sys.utils.JsonResult;

/**
 * 负责excel导入的
 * @author IcekingT420
 *
 */
@Controller
public class ExcelInputController {
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private SpotBasicRepository spotBasicRepository;
	
	/**
	 * 仅仅是文件的上传，返回文件保存之后的相对路径
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/justFileUpload",produces="application/json;charset=utf-8")
	@ResponseBody
	public String justFileUpload(HttpServletRequest request) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		// 上传文件临时保存的目录
		if (Utils.getSpotPicsDir() == null) {
			return new JsonResult(false, "上传文件保存的目标文件夹 不存在").toString();
		}
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile fileToUpload = multipartRequest.getFile("fileToUpload");
		if (fileToUpload == null || fileToUpload.isEmpty()) {
			return new JsonResult(false, "要上传的文件内容不存在. fileToUpload")
					.toString();
		}
		String fileName = fileToUpload.getOriginalFilename();
		String fileExt = "";
		if (fileName.indexOf('.') != -1) {
			fileExt = fileName.substring(fileName.lastIndexOf('.'));
		}
		String newFileName = "uploadfile_" + System.currentTimeMillis()
				+ fileExt;
		File aFile = new File(Utils.getSpotPicsDir(), newFileName);
		// 开始保存文件
		try {
			fileToUpload.transferTo(aFile);
		} catch (Exception e) {
			return new JsonResult(false, "文件保存异常：" + e.getMessage()).toString();
		}

		return new JsonResult(true, "uploadfiles/spotPics/" + newFileName)
				.toString();
	}
	
	/**
	 * 主要的导入方法，excel进来以后，跳转到其它方法处理
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/excelInput",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String excelInput(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		List<PjInputResult> resultList = new ArrayList<PjInputResult>();
		if(loginUser == null || !loginUser.isSuperAdmin()){
			resultList.add(new PjInputResult(false,"缺少当前登入帐号信息"));
			JsonResult jsonResult = new JsonResult(false);
			jsonResult.putRows(resultList);
			return jsonResult.toString();
		}
		//上传文件临时保存的目录
		if(Utils.getTmpDir() == null){
			resultList.add(new PjInputResult(false,"文件存放目录不存在"));
			JsonResult jsonResult = new JsonResult(false);
			jsonResult.putRows(resultList);
			return jsonResult.toString();
		}
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
		MultipartFile fileToUpload = multipartRequest.getFile("fileToUpload");
		if(fileToUpload == null || fileToUpload.isEmpty()){
			resultList.add(new PjInputResult(false,"要上传的文件不存在"));
			JsonResult jsonResult = new JsonResult(false);
			jsonResult.putRows(resultList);
			return jsonResult.toString();
		}
		String fileName = fileToUpload.getOriginalFilename();
		String fileExt = "";
		if(fileName.indexOf('.') != -1){
			fileExt = fileName.substring(fileName.lastIndexOf('.'));
		}
		if(!fileExt.equals(".xls")){
			resultList.add(new PjInputResult(false,"用户资料必须保存在Excel (*.xls) 文件中，请根据模板填写"));
			JsonResult jsonResult = new JsonResult(false);
			jsonResult.putRows(resultList);
			return jsonResult.toString();
		}
		String newFileName = "TmpUploadExcelFile_"+System.currentTimeMillis()+fileExt;
		File aFile = new File(Utils.getTmpDir(),newFileName);
		//开始保存文件
		try {
			fileToUpload.transferTo(aFile);
		} catch (Exception e) {
			resultList.add(new PjInputResult(false,"文件保存异常："+e.getMessage()));
			JsonResult jsonResult = new JsonResult(false);
			jsonResult.putRows(resultList);
			return jsonResult.toString();
		}
		
		//不同的文件类型，需要根据excel文件区分出来的
		//文件保存成功后，开始解析
		boolean resultFlag = inputExcelOperate(loginUser,resultList,aFile);
		
		JsonResult jsonResult = new JsonResult(resultFlag);
		jsonResult.putRows(resultList);
		return jsonResult.toString();
	}
	
	/**
	 * excel文件保存之后，解析文件内容
	 * @return
	 */
	private boolean inputExcelOperate(User loginUser,List<PjInputResult> resultList,File excelFile){
		try {
			InputStream is = new FileInputStream(excelFile);
			HSSFWorkbook wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			//A1单元格用于区分模版类型
			HSSFRow row0 = sheet.getRow(0);
			if(row0==null){
				resultList.add(new PjInputResult(false,"Excel模版不正确"));
				is.close();
				return false;
			}
			//获得A1单元格的值
			HSSFCell cellA1 = row0.getCell(0);
			if(cellA1 == null){
				resultList.add(new PjInputResult(false,"Excel模版不正确"));
				is.close();
				return false;
			}
			String cellA1Value = ExcelHelper.getCellStringValue(cellA1);
			if(Utils.isEmpty(cellA1Value)){
				resultList.add(new PjInputResult(false,"Excel模版不正确"));
				is.close();
				return false;
			}
			boolean resultFlag = false;
			if(cellA1Value.equals("Templet_1")){
				//景点基本资料导入
				resultFlag = inputExcel_SpotBasic(loginUser, resultList, sheet);
			}else {
				resultList.add(new PjInputResult(false,"Excel模版不正确"));
			}
			is.close();
			return resultFlag;
		} catch (Exception e) {
			resultList.add(new PjInputResult(false,"Excel数据解析并导入异常："+e.getMessage()));
			return false;
		}
	}
	
	/**
	 * 导入景点基本信息
	 * @param loginUser
	 * @param resultList
	 * @param sheet
	 * @return
	 */
	private boolean inputExcel_SpotBasic(User loginUser,List<PjInputResult> resultList,HSSFSheet sheet){
		try {
			//总数量，和导入成功的数量
			int succCount = 0;
			//最后一行行号
			int totalCount = sheet.getPhysicalNumberOfRows();
			if(totalCount < 3){
				resultList.add(new PjInputResult(false,"没有数据1"));
				return false;
			}
			int endRowIndex = totalCount;
			//从第3行开始导入数据
			List<ExcelRawData> listRawDatas = new ArrayList<ExcelRawData>();
			for(int i=2;i<=endRowIndex;i++){
				HSSFRow row = sheet.getRow(i);
				if(row == null)
					continue;
				//根据列循环
				List<String> tmpList = new ArrayList<String>();
				for(int j=0;j<=9;j++){
					HSSFCell cell = row.getCell(j);
					tmpList.add(ExcelHelper.getCellStringValue(cell));
				}
				String[] tmpData = new String[tmpList.size()];
				tmpData = tmpList.toArray(tmpData);
				tmpList.clear();
				ExcelRawData excelRawData = new ExcelRawData(i+1, tmpData);
				if(excelRawData.isAllBlank())
					continue;
				listRawDatas.add(excelRawData);
			}
			if(listRawDatas.size() == 0){
				resultList.add(new PjInputResult(false,"没有数据2"));
				return false;
			}
			
			//原始数据获得之后，进行解析
			//并非每行都需要填写省份和城市，因此需要保存当前的信息
			String cur_provice = null;
			City cur_city = null;
			for(ExcelRawData rawData: listRawDatas){
				//省份名称
				String provinceName = rawData.getData()[0];
				if(provinceName.equals("") && cur_provice==null){
					//省份列为空，但是前面没有指定过省份，无效
					resultList.add(new PjInputResult(false,"行："+rawData.getRowNo()+", 缺少 省份 信息"));
					continue;
				}
				if(!provinceName.equals("")){
					//设置当前省份
					cur_provice = provinceName;
				}
				//城市名称
				String cityName = rawData.getData()[1];
				if(cityName.equals("") && cur_city==null){
					//城市列为空，但是前面没有指定过城市，无效
					resultList.add(new PjInputResult(false,"行："+rawData.getRowNo()+", 缺少 城市 信息"));
					continue;
				}
				if(!cityName.equals("")){
					//如果城市不为空，则要判断当前省份下的该城市是否存在，不存在的话，则新建
					cur_city = cityRepository.findByNameAndProvince(cityName, cur_provice);
					if(cur_city == null){
						cur_city = new City();
						cur_city.setName(cityName);
						cur_city.setProvince(cur_provice);
						//设置天气代码
						cur_city.setWcode(rawData.getData()[2]);
						//设置城市的拼音
						String py1 = ChinesePinyin.getPinyin1(cur_city.getName());
						cur_city.setPinyin(py1);
						cur_city.setPinyszm(ChinesePinyin.getSzm(py1));
						cur_city = cityRepository.save(cur_city);
						resultList.add(new PjInputResult(true,"行："+rawData.getRowNo()+", 新增城市："+cur_city.showFullName()));
					}else {
						boolean needModify = false;
						if(!cur_city.getWcode().equals(rawData.getData()[2])){
							//城市的天气代码发生了编号，修改天气代码
							cur_city.setWcode(rawData.getData()[2]);
							resultList.add(new PjInputResult(true,"行："+rawData.getRowNo()+", 修改城市天气代码："+cur_city.showFullName()));
							needModify = true;
						}
						if(Utils.isEmpty(cur_city.getPinyin())){
							String py1 = ChinesePinyin.getPinyin1(cur_city.getName());
							cur_city.setPinyin(py1);
							cur_city.setPinyszm(ChinesePinyin.getSzm(py1));
							resultList.add(new PjInputResult(true,"行："+rawData.getRowNo()+", 修改城市拼音排序："+cur_city.showFullName()));
							needModify = true;
						}
						
						if(needModify)
							cur_city = cityRepository.save(cur_city);
					}
				}
				//景点名称
				String spotName = rawData.getData()[3];
				if(spotName.equals("")){
					resultList.add(new PjInputResult(false,"行："+rawData.getRowNo()+", 缺少 景点名称 信息"));
					continue;
				}
				
				//以下就表示数据有效的了
				succCount++;
				//根据名字和城市找到景点是否存在
				SpotBasic spot = spotBasicRepository.findByNameAndCity(spotName, cur_city);
				boolean exist = true;
				if(spot == null){
					//不存在，则新建
					spot = new SpotBasic();
					//设置当前城市
					spot.setCity(cur_city);
					spot.setName(spotName);
					exist = false;
				}
				if(!rawData.getData()[4].equals("")){
					//景观等级
					int viewLevel = Utils.parseInt(rawData.getData()[4]);
					//目前景观等级只有1，2，3
					if(viewLevel>=1 && viewLevel<=3)
						spot.setViewLevel(viewLevel);
				}
				if(!rawData.getData()[5].equals("")){
					//景点评级
					spot.setGrade(rawData.getData()[5]);
				}
				if(!rawData.getData()[6].equals("")){
					//景点编号
					spot.setCode(rawData.getData()[6]);
				}
				if(!rawData.getData()[7].equals("")){
					//最大承载量
					int maxCapacity = Utils.parseInt(rawData.getData()[7]);
					if(maxCapacity > 0)
						spot.setMaxCapacity(maxCapacity);
				}
				if(!rawData.getData()[8].equals("") && !rawData.getData()[9].equals("")){
					//坐标X,Y
					spot.setLonX(Utils.parseDouble(rawData.getData()[8]));
					spot.setLatY(Utils.parseDouble(rawData.getData()[9]));
				}
				spot = spotBasicRepository.save(spot);
				
				resultList.add(new PjInputResult(true,"行："+rawData.getRowNo()+", "+(exist?"修改":"新增")+"景点："+spot.showFullName()));
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			resultList.add(new PjInputResult(false,"Excel数据解析并导入异常："+e.getMessage()));
			return false;
		}
	}
}
