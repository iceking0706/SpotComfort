package com.xie.spot.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ssin.security.MD5;
import ssin.util.MyStringUtil;

import com.xie.spot.entity.AdvtPicture;
import com.xie.spot.entity.City;
import com.xie.spot.entity.CodeWeather;
import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotComfortCorrect;
import com.xie.spot.entity.SpotPassengerFlow;
import com.xie.spot.entity.SpotPicture;
import com.xie.spot.entity.User;
import com.xie.spot.pojo.ComparatorPjFileInfo;
import com.xie.spot.pojo.FileExtFilter;
import com.xie.spot.pojo.PjFileInfo;
import com.xie.spot.pojo.PjSpotComfortCalcuResult;
import com.xie.spot.pojo.PjValueText;
import com.xie.spot.repository.AdvtPictureRepository;
import com.xie.spot.repository.CityRepository;
import com.xie.spot.repository.CodeWeatherRepository;
import com.xie.spot.repository.SpotBasicRepository;
import com.xie.spot.repository.SpotComfortCorrectRepository;
import com.xie.spot.repository.SpotPassengerFlowRepository;
import com.xie.spot.repository.SpotPictureRepository;
import com.xie.spot.repository.UserRepository;
import com.xie.spot.service.FetchWeatherService;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.ChinesePinyin;
import com.xie.spot.sys.utils.JsonResult;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

/**
 * 主要的Controller
 * @author IcekingT420
 *
 */
@Controller
public class SpotController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private SpotBasicRepository spotBasicRepository;
	@Autowired
	private SpotPictureRepository spotPictureRepository;
	@Autowired
	private CodeWeatherRepository codeWeatherRepository;
	@Autowired
	private SpotComfortCorrectRepository spotComfortCorrectRepository;
	@Autowired
	private SpotPassengerFlowRepository spotPassengerFlowRepository;
	@Autowired
	private FetchWeatherService fetchWeatherService;
	@Autowired
	private AdvtPictureRepository advtPictureRepository;
	
	/**
	 * 登入
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/login",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String login(HttpServletRequest request){
		String username = Utils.getParamValue(request, "username");
		String password = Utils.getParamValue(request, "password");
		if(Utils.isEmpty(username))
			return new JsonResult(false,"缺少必要参数：username").toString();
		if(Utils.isEmpty(password))
			return new JsonResult(false,"缺少必要参数：password").toString();
		User loginUser = userRepository.findByUsernameAndPassword(username, MD5.encode(password));
		if(loginUser == null)
			return new JsonResult(false,"用户名不存在或密码错误").toString();
		request.getSession().setAttribute("loginUser", loginUser);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 退出登入
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/logout",produces="application/json;charset=utf-8")
	@ResponseBody
	public String logout(HttpServletRequest request){
		if(request.getSession().getAttribute("loginUser") != null){
			request.getSession().removeAttribute("loginUser");
		}
		return new JsonResult(true).toString();
	}
	
	/**
	 * 修改密码
	 * @param request
	 * @param oldPass 原密码
	 * @param newPass 新密码
	 * @return
	 */
	@RequestMapping(value="/modifypass",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String modifyPass(HttpServletRequest request,String oldPass,String newPass){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(!loginUser.getPassword().equals(MD5.encode(oldPass))){
			return new JsonResult(false,"原密码不正确").toString();
		}
		//开始修改
		loginUser.setPassword(MD5.encode(newPass));
		userRepository.save(loginUser);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 后台管理端定期去查询，保持连接
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/keepOnline",produces="application/json;charset=utf-8")
	@ResponseBody
	public String keepOnline(HttpServletRequest request){
		/*User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}*/
		return new JsonResult(true).toString();
	}
	
	/**
	 * 城市列表
	 * @param request
	 * @param pageParam
	 * @param province
	 * @param city
	 * @return
	 */
	@RequestMapping(value="/listCity",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listCity(HttpServletRequest request,PageParam pageParam,String province,String city){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		if(!Utils.isEmpty(province))
			mapCon.put("province", province);
		if(!Utils.isEmpty(city))
			mapCon.put("city", city);
		PageData<City> pageData = cityRepository.searchBy(mapCon, pageParam);
		JsonResult jsonResult = new JsonResult(true,pageData.getTotal(),pageData.getContent());
		return jsonResult.toString();
	}
	
	@RequestMapping(value="/addCity",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String addCity(HttpServletRequest request,String name,String wcode,String province,String picUrl,String pinyin,Integer rcmd){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(name)){
			return new JsonResult(false,"缺少必要参数 name").toString();
		}
		if(Utils.isEmpty(province)){
			return new JsonResult(false,"缺少必要参数 province").toString();
		}
		//判断城市是否存在了
		City city = cityRepository.findByNameAndProvince(name, province);
		if(city != null){
			return new JsonResult(false,"城市："+city.showFullName()+" 已经存在").toString();
		}
		city = new City();
		city.setName(name);
		city.setProvince(province);
		city.setWcode(wcode);
		city.setPicUrl(picUrl);
		if(!Utils.isEmpty(pinyin)){
			city.setPinyin(pinyin);
			city.setPinyszm(ChinesePinyin.getSzm(pinyin));
		}else{
			//拼音为空，重新生成一下
			String py1 = ChinesePinyin.getPinyin1(city.getName());
			city.setPinyin(py1);
			city.setPinyszm(ChinesePinyin.getSzm(py1));
		}
		city.setRcmd(!Utils.isEmpty(rcmd)?rcmd:0);
		city = cityRepository.save(city);
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/modifyCity",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String modifyCity(HttpServletRequest request,String name,String wcode,String province,Long id,String picUrl,String pinyin,Integer rcmd){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		if(Utils.isEmpty(name)){
			return new JsonResult(false,"缺少必要参数 name").toString();
		}
		if(Utils.isEmpty(province)){
			return new JsonResult(false,"缺少必要参数 province").toString();
		}
		City city = cityRepository.findOne(id);
		if(city == null){
			return new JsonResult(false,"数据库检索失败 City.id="+id).toString();
		}
		//判断名称和省份是否存在的
		City other = cityRepository.findByNameAndProvince(name, province);
		if(other!=null && !other.getId().equals(city.getId())){
			return new JsonResult(false,"城市："+other.showFullName()+" 已经存在").toString();
		}
		
		city.setName(name);
		city.setProvince(province);
		city.setWcode(wcode);
		city.setPicUrl(picUrl);
		if(!Utils.isEmpty(pinyin)){
			//不为空，要判断是否变化了
			if(!pinyin.equals(city.getPinyin())){
				city.setPinyin(pinyin);
				city.setPinyszm(ChinesePinyin.getSzm(pinyin));
			}
		}else{
			//拼音为空，重新生成一下
			String py1 = ChinesePinyin.getPinyin1(city.getName());
			city.setPinyin(py1);
			city.setPinyszm(ChinesePinyin.getSzm(py1));
		}
		city.setRcmd(!Utils.isEmpty(rcmd)?rcmd:0);
		city = cityRepository.save(city);
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/deleteCity",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteCity(HttpServletRequest request,Long id){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		City city = cityRepository.findOne(id);
		if(city == null){
			return new JsonResult(false,"数据库检索失败 City.id="+id).toString();
		}
		if(!cityRepository.canDelete(city)){
			return new JsonResult(false,"城市："+city.showFullName()+" 被引用，无法删除").toString();
		}
		cityRepository.delete(city);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 景点列表
	 * @param request
	 * @param pageParam
	 * @param province
	 * @param city
	 * @return
	 */
	@RequestMapping(value="/listSpot",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listSpot(HttpServletRequest request,PageParam pageParam,String province,String city,String name,Integer viewLevel){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		if(!Utils.isEmpty(province))
			mapCon.put("province", province);
		if(!Utils.isEmpty(city))
			mapCon.put("city", city);
		if(!Utils.isEmpty(name))
			mapCon.put("name", name);
		if(viewLevel!=null && viewLevel>0)
			mapCon.put("viewLevel", viewLevel);
		PageData<SpotBasic> pageData = spotBasicRepository.searchBy(mapCon, pageParam);
		if(!Utils.isEmpty(pageData.getContent())){
			for(SpotBasic spot: pageData.getContent()){
				spot.setPicCount(spotPictureRepository.totalPicCountOfSpot(spot));
				spot.setCcCount(spotComfortCorrectRepository.totalCcCountOfSpot(spot));
			}
		}
		JsonResult jsonResult = new JsonResult(true,pageData.getTotal(),pageData.getContent());
		return jsonResult.toString();
	}
	
	/**
	 * 省份的下拉列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/comboProvince",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String comboProvince(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return "{}";
		}
		List<String> list = cityRepository.listAllProvince();
		if(Utils.isEmpty(list))
			return "{}";
		List<PjValueText> pjList = new ArrayList<PjValueText>();
		while(!list.isEmpty()){
			String str = list.remove(0);
			pjList.add(new PjValueText(str, str));
		}
		JSONArray array = JSONArray.fromObject(pjList);
		String json = array.toString();
		return json;
	}
	
	/**
	 * 根据省份得到城市
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/comboCity",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String comboCity(HttpServletRequest request,String province){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return "{}";
		}
		if(Utils.isEmpty(province))
			return "{}";
		List<City> list = cityRepository.findByProvinceOrderByNameAsc(province);
		if(Utils.isEmpty(list))
			return "{}";
		List<PjValueText> pjList = new ArrayList<PjValueText>();
		while(!list.isEmpty()){
			City city = list.remove(0);
			pjList.add(new PjValueText(String.valueOf(city.getId()), city.getName()));
		}
		JSONArray array = JSONArray.fromObject(pjList);
		String json = array.toString();
		return json;
	}
	
	@RequestMapping(value="/addSpotBasic",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String addSpotBasic(HttpServletRequest request,Long cityId,String name,String code,String wcode,String grade,Integer viewLevel,Integer maxCapacity,Double lonX,Double latY,Integer mainRcmd){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(name)){
			return new JsonResult(false,"缺少必要参数 name").toString();
		}
		if(Utils.isEmptyId(cityId)){
			return new JsonResult(false,"缺少必要参数 cityId").toString();
		}
		if(Utils.isEmpty(viewLevel)){
			return new JsonResult(false,"缺少必要参数 viewLevel").toString();
		}
		//判断城市记录是否存在
		City city = cityRepository.findOne(cityId);
		if(city == null){
			return new JsonResult(false,"数据库检索失败 City.id="+cityId).toString();
		}
		//判断景点的名称是否已经存在，同一个城市中的景点不能同名
		SpotBasic spot = spotBasicRepository.findByNameAndCity(name, city);
		if(spot != null){
			return new JsonResult(false,city.showFullName()+" 中名称为 "+name+" 的景点已经存在").toString();
		}
		spot = new SpotBasic();
		spot.setCity(city);
		spot.setName(name);
		spot.setCode(code);
		spot.setWcode(wcode);
		spot.setGrade(grade);
		spot.setMaxCapacity(maxCapacity);
		spot.setViewLevel(viewLevel);
		spot.setLonX(lonX);
		spot.setLatY(latY);
		spot.setMainRcmd(mainRcmd);
		
		spot = spotBasicRepository.save(spot);
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/modifySpotBasic",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String modifySpotBasic(HttpServletRequest request,Long id,Long cityId,String name,String code,String wcode,String grade,Integer viewLevel,Integer maxCapacity,Double lonX,Double latY,Integer mainRcmd){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		if(Utils.isEmpty(name)){
			return new JsonResult(false,"缺少必要参数 name").toString();
		}
		if(Utils.isEmptyId(cityId)){
			return new JsonResult(false,"缺少必要参数 cityId").toString();
		}
		if(Utils.isEmpty(viewLevel)){
			return new JsonResult(false,"缺少必要参数 viewLevel").toString();
		}
		//判断原先的景点记录是否存在
		SpotBasic spot = spotBasicRepository.findOne(id);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败 SpotBasic.id="+id).toString();
		}
		//如果景点的cityId发生了变化，判断一下新的城市是否存在
		if(!spot.getCity().getId().equals(cityId)){
			City city = cityRepository.findOne(cityId);
			if(city == null){
				return new JsonResult(false,"数据库检索失败 City.id="+cityId).toString();
			}
			spot.setCity(city);
		}
		//找到城市下面的景点名称，判断新的名字是否存在重复的
		SpotBasic other = spotBasicRepository.findByNameAndCity(name, spot.getCity());
		if(other != null && !other.getId().equals(spot.getId())){
			return new JsonResult(false,spot.getCity().showFullName()+" 中名称为 "+name+" 的景点已经存在").toString();
		}
		spot.setName(name);
		spot.setCode(code);
		spot.setWcode(wcode);
		spot.setGrade(grade);
		spot.setMaxCapacity(maxCapacity);
		spot.setViewLevel(viewLevel);
		spot.setLonX(lonX);
		spot.setLatY(latY);
		spot.setMainRcmd(mainRcmd);
		
		spot = spotBasicRepository.save(spot);
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/deleteSpotBasic",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteSpotBasic(HttpServletRequest request,Long id){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		SpotBasic spot = spotBasicRepository.findOne(id);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败 SpotBasic.id="+id).toString();
		}
		if(!spotBasicRepository.canDelete(spot)){
			return new JsonResult(false,spot.getCity().showFullName()+" 中的景点："+spot.getName()+" 被引用，无法删除").toString();
		}
		spotBasicRepository.delete(spot);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 切换到某个景点 图片界面
	 * @param request
	 * @param spotId
	 * @return
	 */
	@RequestMapping(value="/toPageSpotPic")
	@Transactional
	public String toPageSpotPic(HttpServletRequest request,Long spotId){
		long curSpotId = 0l;
		String curSpotFullName = "";
		if(!Utils.isEmptyId(spotId)){
			curSpotId = spotId.longValue();
			SpotBasic spot = spotBasicRepository.findOne(spotId);
			if(spot != null){
				curSpotFullName = spot.showFullName();
			}
		}
		request.setAttribute("curSpotId", curSpotId);
		request.setAttribute("curSpotFullName", curSpotFullName);
		return "mng_spot_pic";
	}
	
	/**
	 * 切换到某个景点 修正因子界面
	 * @param request
	 * @param spotId
	 * @return
	 */
	@RequestMapping(value="/toPageSpotCC")
	@Transactional
	public String toPageSpotCC(HttpServletRequest request,Long spotId){
		long curSpotId = 0l;
		String curSpotFullName = "";
		if(!Utils.isEmptyId(spotId)){
			curSpotId = spotId.longValue();
			SpotBasic spot = spotBasicRepository.findOne(spotId);
			if(spot != null){
				curSpotFullName = spot.showFullName();
			}
		}
		request.setAttribute("curSpotId", curSpotId);
		request.setAttribute("curSpotFullName", curSpotFullName);
		return "mng_spot_comfort_c";
	}
	
	/**
	 * 某个景点的图片
	 * @param request
	 * @param pageParam
	 * @param spotId
	 * @return
	 */
	@RequestMapping(value="/listSpotPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listSpotPicture(HttpServletRequest request,PageParam pageParam,Long spotId){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(spotId)){
			return new JsonResult(false,"缺少必要参数 spotId").toString();
		}
		SpotBasic spot = spotBasicRepository.findOne(spotId);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败. SpotBasic.id="+spotId).toString();
		}
		
		Page<SpotPicture> page = spotPictureRepository.findBySpotOrderByMainRcmdDescAndTimeDesc(spot, new PageRequest(pageParam.getPage(), pageParam.getSize()));
		
		JsonResult jsonResult = new JsonResult(true,page.getTotalElements(),page.getContent());
		return jsonResult.toString();
	}
	
	/**
	 * 后台管理增加景点图片
	 * @param request
	 * @param spotId
	 * @param url
	 * @return
	 */
	@RequestMapping(value="/addSpotPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String addSpotPicture(HttpServletRequest request,Long spotId,String url,Integer mainRcmd){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(spotId)){
			return new JsonResult(false,"缺少必要参数 spotId").toString();
		}
		if(Utils.isEmpty(url)){
			return new JsonResult(false,"缺少必要参数 url").toString();
		}
		SpotBasic spot = spotBasicRepository.findOne(spotId);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败. SpotBasic.id="+spotId).toString();
		}
		SpotPicture picture = new SpotPicture();
		//此处增加的图片，统一默认系统增加
		picture.setComefrom("SystemAdd");
		picture.setUrl(url);
		picture.setSpot(spot);
		picture.setMainRcmd(mainRcmd);
		picture = spotPictureRepository.save(picture);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 修改景区的图片，只能修改其推荐值
	 * @param request
	 * @param id
	 * @param mainRcmd
	 * @return
	 */
	@RequestMapping(value="/modifySpotPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String modifySpotPicture(HttpServletRequest request,Long id,Integer mainRcmd){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		SpotPicture po = spotPictureRepository.findOne(id);
		if(po == null){
			return new JsonResult(false,"数据检索失败. SpotPicture.id="+id).toString();
		}
		if(mainRcmd == null || mainRcmd<0){
			return new JsonResult(false,"缺少必要参数 mainRcmd").toString();
		}
		po.setMainRcmd(mainRcmd);
		po = spotPictureRepository.save(po);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 删除，可以多个删除
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/deleteSpotPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteSpotPicture(HttpServletRequest request,String ids){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(ids)){
			return new JsonResult(false,"缺少必要参数 ids").toString();
		}
		String[] idArray = MyStringUtil.getArrayFromStrByChar(ids, "_");
		if(idArray==null || idArray.length == 0){
			return new JsonResult(false,"缺少必要参数 ids (2)").toString();
		}
		List<SpotPicture> delList = new ArrayList<SpotPicture>();
		for(String idStr: idArray){
			long idL = Utils.parseLong(idStr);
			if(idL<=0)
				continue;
			SpotPicture picture = spotPictureRepository.findOne(idL);
			if(picture == null)
				continue;
			delList.add(picture);
		}
		if(delList.size() == 0){
			return new JsonResult(false,"没有要删除的数据").toString();
		}
		
		spotPictureRepository.delete(delList);
		
		delList.clear();
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/listCodeWeather",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listCodeWeather(HttpServletRequest request,PageParam pageParam,String wcode){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return "{}";
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		if(!Utils.isEmpty(wcode))
			mapCon.put("wcode", wcode);
		PageData<CodeWeather> pageData = codeWeatherRepository.searchBy(mapCon, pageParam);
		if(!Utils.isEmpty(pageData.getContent())){
			for(CodeWeather po: pageData.getContent()){
				String cityOrSpot = "";
				//先根据wcode找城市，
				Page<City> tmpPage = cityRepository.findByWcode(po.getWcode(), new PageRequest(0, 1));
				if(!Utils.isEmpty(tmpPage.getContent())){
					cityOrSpot = tmpPage.getContent().get(0).showFullName();
				}else{
					//找不到城市的话，再找景点
					Page<SpotBasic> tmpPage2 = spotBasicRepository.findByWcode(wcode, new PageRequest(0, 1));
					if(!Utils.isEmpty(tmpPage2.getContent())){
						cityOrSpot = tmpPage2.getContent().get(0).showFullName();
					}else {
						cityOrSpot = "unKnow";
					}
				}
				po.setCityOrSpot(cityOrSpot);
			}
		}
		JsonResult jsonResult = new JsonResult(true,pageData.getTotal(),pageData.getContent());
		return jsonResult.toString();
	}
	
	@RequestMapping(value="/deleteCodeWeather",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteCodeWeather(HttpServletRequest request,String ids){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(ids)){
			return new JsonResult(false,"缺少必要参数 ids").toString();
		}
		String[] idArray = MyStringUtil.getArrayFromStrByChar(ids, "_");
		if(idArray==null || idArray.length == 0){
			return new JsonResult(false,"缺少必要参数 ids (2)").toString();
		}
		List<CodeWeather> delList = new ArrayList<CodeWeather>();
		for(String idStr: idArray){
			long idL = Utils.parseLong(idStr);
			if(idL<=0)
				continue;
			CodeWeather po = codeWeatherRepository.findOne(idL);
			if(po == null)
				continue;
			delList.add(po);
		}
		if(delList.size() == 0){
			return new JsonResult(false,"没有要删除的数据").toString();
		}
		
		codeWeatherRepository.delete(delList);
		
		delList.clear();
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 某个景点的舒适度修正因子
	 * @param request
	 * @param pageParam
	 * @param spotId
	 * @return
	 */
	@RequestMapping(value="/listSpotCC",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listSpotCC(HttpServletRequest request,PageParam pageParam,Long spotId){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return "{}";
		}
		if(Utils.isEmptyId(spotId)){
			return "{}";
		}
		SpotBasic spot = spotBasicRepository.findOne(spotId);
		if(spot == null){
			return "{}";
		}
		
		Page<SpotComfortCorrect> page = spotComfortCorrectRepository.findBySpot(spot, new PageRequest(pageParam.getPage(), pageParam.getSize()));
		
		JsonResult jsonResult = new JsonResult(true,page.getTotalElements(),page.getContent());
		return jsonResult.toString();
	}
	
	/**
	 * 景点修正因子的新增和修改，根据id是否存在来判断
	 * @param request
	 * @param spotId
	 * @param vo
	 * @return
	 */
	@RequestMapping(value="/addModifySpotCC",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String addModifySpotCC(HttpServletRequest request,Long spotId,SpotComfortCorrect vo){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(spotId)){
			return new JsonResult(false,"缺少必要参数 spotId").toString();
		}
		SpotBasic spot = spotBasicRepository.findOne(spotId);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败. SpotBasic.id="+spotId).toString();
		}
		if(!vo.isValid()){
			return new JsonResult(false,"数据无效，至少存在一个修正因子"+spotId).toString();
		}
		SpotComfortCorrect entity = null;
		if(!Utils.isEmptyId(vo.getId())){
			//修改的情况
			entity = spotComfortCorrectRepository.findOne(vo.getId());
			if(entity == null){
				return new JsonResult(false,"数据库检索失败. SpotComfortCorrect.id="+vo.getId()).toString();
			}
			if(!entity.getSpot().getId().equals(spot.getId())){
				return new JsonResult(false,"景点信息不一致. SpotComfortCorrect.spot.id="+entity.getId()+", SpotBasic.id="+spot.getId()).toString();
			}
		}else {
			//新增
			entity = new SpotComfortCorrect();
			entity.setSpot(spot);
		}
		
		//无论新增或修改，数据更新
		entity.setSeasonFactor(vo.getSeasonFactor());
		entity.setSeasonScore(vo.getSeasonScore());
		entity.setWeatherFactor(vo.getWeatherFactor());
		entity.setWeatherScore(vo.getWeatherScore());
		entity.setTempFactor(vo.getTempFactor());
		entity.setTempScore(vo.getTempScore());
		entity.setPassengerFactor(vo.getPassengerFactor());
		entity.setPassengerScore(vo.getPassengerScore());
		entity.setMark(vo.getMark());
		
		entity = spotComfortCorrectRepository.save(entity);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 删除景点的修正因子
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/deleteSpotCC",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteSpotCC(HttpServletRequest request,String ids){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(ids)){
			return new JsonResult(false,"缺少必要参数 ids").toString();
		}
		String[] idArray = MyStringUtil.getArrayFromStrByChar(ids, "_");
		if(idArray==null || idArray.length == 0){
			return new JsonResult(false,"缺少必要参数 ids (2)").toString();
		}
		List<SpotComfortCorrect> delList = new ArrayList<SpotComfortCorrect>();
		for(String idStr: idArray){
			long idL = Utils.parseLong(idStr);
			if(idL<=0)
				continue;
			SpotComfortCorrect cc = spotComfortCorrectRepository.findOne(idL);
			if(cc == null)
				continue;
			delList.add(cc);
		}
		if(delList.size() == 0){
			return new JsonResult(false,"没有要删除的数据").toString();
		}
		
		spotComfortCorrectRepository.delete(delList);
		
		delList.clear();
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 模拟景点的客流量，当前时刻
	 * @param request
	 * @param spotId
	 * @param simulateFlow
	 * @return
	 */
	@RequestMapping(value="/simulateSpotFlow",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String simulateSpotFlow(HttpServletRequest request,Long spotId,Integer simulateFlow){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(spotId)){
			return new JsonResult(false,"缺少必要参数 spotId").toString();
		}
		SpotBasic spot = spotBasicRepository.findOne(spotId);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败. SpotBasic.id="+spotId).toString();
		}
		if(Utils.isEmpty(simulateFlow)){
			return new JsonResult(false,"缺少必要参数 simulateFlow").toString();
		}
		if(Utils.isEmpty(spot.getMaxCapacity())){
			return new JsonResult(false,"景点的最大承载量未设置").toString();
		}
		SpotPassengerFlow entity = new SpotPassengerFlow();
		entity.setFlow(simulateFlow);
		entity.setSpot(spot);
		entity.setComefrom("SystemAdd");
		entity.setCrowdDegree(Utils.calculateCrowdDegreeByFlow(simulateFlow.intValue(), spot.getMaxCapacity().intValue()));
		
		entity = spotPassengerFlowRepository.save(entity);
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 景点客流数据列表
	 * @param request
	 * @param pageParam
	 * @param province
	 * @param city
	 * @return
	 */
	@RequestMapping(value="/listPassengerFlow",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listPassengerFlow(HttpServletRequest request,PageParam pageParam,String spotCity,String spotName){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		if(!Utils.isEmpty(spotCity))
			mapCon.put("spotCity", spotCity);
		if(!Utils.isEmpty(spotName))
			mapCon.put("spotName", spotName);
		PageData<SpotPassengerFlow> pageData = spotPassengerFlowRepository.searchBy(mapCon, pageParam);
		JsonResult jsonResult = new JsonResult(true,pageData.getTotal(),pageData.getContent());
		return jsonResult.toString();
	}
	
	/**
	 * 删除景点客流数据
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/deleteSpotPassengerFlow",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteSpotPassengerFlow(HttpServletRequest request,String ids){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(ids)){
			return new JsonResult(false,"缺少必要参数 ids").toString();
		}
		String[] idArray = MyStringUtil.getArrayFromStrByChar(ids, "_");
		if(idArray==null || idArray.length == 0){
			return new JsonResult(false,"缺少必要参数 ids (2)").toString();
		}
		List<SpotPassengerFlow> delList = new ArrayList<SpotPassengerFlow>();
		for(String idStr: idArray){
			long idL = Utils.parseLong(idStr);
			if(idL<=0)
				continue;
			SpotPassengerFlow entity = spotPassengerFlowRepository.findOne(idL);
			if(entity == null)
				continue;
			delList.add(entity);
		}
		if(delList.size() == 0){
			return new JsonResult(false,"没有要删除的数据").toString();
		}
		
		spotPassengerFlowRepository.delete(delList);
		
		delList.clear();
		
		return new JsonResult(true).toString();
	}
	
	/**
	 * 计算景观的舒适度指数
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/calcuSpotComfort",produces="application/json;charset=utf-8")
	@ResponseBody
	public String calcuSpotComfort(HttpServletRequest request,Long spotId){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(spotId)){
			return new JsonResult(false,"缺少必要参数 spotId").toString();
		}
		SpotBasic spot = spotBasicRepository.findOne(spotId);
		if(spot == null){
			return new JsonResult(false,"数据库检索失败. SpotBasic.id="+spotId).toString();
		}
		PjSpotComfortCalcuResult result = fetchWeatherService.calculateSpotComfort(spot);
		
		JsonResult jsonResult = new JsonResult(true);
		jsonResult.put("result", result);
		
		return jsonResult.toString();
	}
	
	@RequestMapping(value="/listAdvtPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listAdvtPicture(HttpServletRequest request,PageParam pageParam){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		
		Page<AdvtPicture> pageData = advtPictureRepository.searchAll(new PageRequest(pageParam.getPage(), pageParam.getSize()));
		
		JsonResult jsonResult = new JsonResult(true,pageData.getTotalElements(),pageData.getContent());
		return jsonResult.toString();
	}
	
	/**
	 * 新增广告位的一张图片
	 * @param request
	 * @param spotId
	 * @param url
	 * @param mainRcmd
	 * @return
	 */
	@RequestMapping(value="/addAdvtPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String addAdvtPicture(HttpServletRequest request,String mark,String url,Integer mainRcmd,String linkUrl){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(mark)){
			return new JsonResult(false,"缺少必要参数 mark").toString();
		}
		if(Utils.isEmpty(url)){
			return new JsonResult(false,"缺少必要参数 url").toString();
		}
		AdvtPicture picture = new AdvtPicture();
		picture.setUrl(url);
		picture.setMark(mark);
		picture.setMainRcmd(mainRcmd);
		picture.setLinkUrl(Utils.isEmpty(linkUrl)?"####":linkUrl);
		picture = advtPictureRepository.save(picture);
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/modifyAdvtPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String modifyAdvtPicture(HttpServletRequest request,Long id,String mark,String url,Integer mainRcmd,String linkUrl){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		AdvtPicture picture = advtPictureRepository.findOne(id);
		if(picture == null){
			return new JsonResult(false,"数据检索失败. AdvtPicture.id="+id).toString();
		}
		if(Utils.isEmpty(mark)){
			return new JsonResult(false,"缺少必要参数 mark").toString();
		}
		if(Utils.isEmpty(url)){
			return new JsonResult(false,"缺少必要参数 url").toString();
		}
		if(mainRcmd == null || mainRcmd<0){
			return new JsonResult(false,"缺少必要参数 mainRcmd").toString();
		}
		picture.setUrl(url);
		picture.setMark(mark);
		picture.setMainRcmd(mainRcmd);
		picture.setLinkUrl(Utils.isEmpty(linkUrl)?"####":linkUrl);
		picture = advtPictureRepository.save(picture);
		
		return new JsonResult(true).toString();
	}
	
	@RequestMapping(value="/deleteAdvtPicture",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteAdvtPicture(HttpServletRequest request,Long id){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmptyId(id)){
			return new JsonResult(false,"缺少必要参数 id").toString();
		}
		AdvtPicture po = advtPictureRepository.findOne(id);
		if(po == null){
			return new JsonResult(false,"数据检索失败. AdvtPicture.id="+id).toString();
		}
		advtPictureRepository.delete(po);
		
		return new JsonResult(true).toString();
	}
	
	
}
