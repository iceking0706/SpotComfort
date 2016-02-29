package com.xie.spot.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ssin.util.DateProcess;

import com.eg.intf3.security.ByteUtils;
import com.suyou.singnalway.RecvInOutData;
import com.suyou.singnalway.SnIpPort;
import com.xie.spot.entity.CameraAlert;
import com.xie.spot.entity.CameraCfg;
import com.xie.spot.entity.CameraData;
import com.xie.spot.mail.SendMail;
import com.xie.spot.pojo.ComparatorRecvInOutData;
import com.xie.spot.pojo.PjOnedayRawIOLostInfo;
import com.xie.spot.repository.CameraAlertRepository;
import com.xie.spot.repository.CameraCfgRepository;
import com.xie.spot.repository.CameraDataRepository;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JDBC;
import com.xie.spot.sys.utils.JsonResult;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

@Service
public class CameraOperService {
	private static final Logger logger = LoggerFactory.getLogger(CameraOperService.class);
	
	@Autowired
	private CameraCfgRepository cameraCfgRepository;
	@Autowired
	private CameraDataRepository cameraDataRepository;
	@Autowired
	private CameraAlertRepository cameraAlertRepository;
	
	/**
	 * 连接不成功时候，设置某个摄像机脱机
	 * @param cmr
	 */
	@Transactional
	public void offlineOneCmr(CameraCfg cmr){
		cmr.setOnline(0);
		cmr.setIp("");
		cmr.setPort(0);
		cameraCfgRepository.save(cmr);
	}
	
	@Transactional
	public CameraCfg findCameraBySN(String sn){
		return cameraCfgRepository.findBySn(sn);
	}
	
	/**
	 * 得到某个摄像机最新的上传数据
	 * @param sn
	 * @return
	 */
	@Transactional
	public CameraData getLatestCameraData(String sn){
		Map<String, Object> mapCon = new HashMap<String, Object>();
		mapCon.put("snfull", sn);
		PageData<CameraData> pageData = cameraDataRepository.searchBy(mapCon, new PageParam(0, 1));
		if(Utils.isEmpty(pageData.getContent()))
			return null;
		return pageData.getContent().get(0);
	}
	
	/**
	 * 查询距离某个时间最近的一条数据
	 * @param sn
	 * @param time
	 * @return
	 */
	@Transactional
	public CameraData getNearestByCameraData(String sn,long time){
		Map<String, Object> mapCon = new HashMap<String, Object>();
		mapCon.put("snfull", sn);
		mapCon.put("startTime", time);
		PageData<CameraData> pageData = cameraDataRepository.searchBy(mapCon, new PageParam(0, 1));
		//比time后面的哪个
		CameraData data1 = Utils.isEmpty(pageData.getContent())?null:pageData.getContent().get(0);
		
		mapCon.clear();
		mapCon.put("snfull", sn);
		mapCon.put("endTime", time);
		pageData = cameraDataRepository.searchBy(mapCon, new PageParam(0, 1));
		//比time小的那个
		CameraData data2 = Utils.isEmpty(pageData.getContent())?null:pageData.getContent().get(0);
		
		if(data1 == null && data2 == null){
			return null;
		}else {
			if(data1 != null && data2 != null){
				//前后两个都有，看看时间谁最接近
				long sub1 = Math.abs(data1.getRecvIOTime().longValue()-time);
				long sub2 = Math.abs(data2.getRecvIOTime().longValue()-time);
				return sub1<sub2?data1:data2;
			}else{
				if(data1 != null)
					return data1;
				else 
					return data2;
			}
		}
	}
	
	/**
	 * 当设备的ip地址发生变化时候的触发
	 * @param snIpMap
	 */
	@Transactional
	public void theSnIpChange(ConcurrentMap<String, SnIpPort> map){
		try {
			//第一步，把现有数据库中的设备都要变成脱机
			cameraCfgRepository.meSetAllCameraOffline();
			if(map.size() == 0){
				return;
			}
			//第二步，根据sn，设置ip和联机
			Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				SnIpPort sipObj = map.get(iterator.next());
				if(sipObj == null)
					continue;
				//根据sn找到对象
				CameraCfg po = cameraCfgRepository.findBySn(sipObj.getSn());
				if(po == null)
					continue;
				po.setIp(sipObj.getIp());
				po.setPort(sipObj.getPort());
				po.setOnline(1);
				po.setTime(sipObj.getTime());
				cameraCfgRepository.save(po);
			}
		} catch (Exception e) {
			logger.error("theSnIpChange error: "+e.getMessage(), e);
		}
	}
	
	/**
	 * 找出全部联机的相机
	 * @return
	 */
	@Transactional
	public List<CameraCfg> findOnlineCmrs(){
		return cameraCfgRepository.meFindOnlineCameras();
	}
	
	/**
	 * 找出全部启用的相机
	 * @return
	 */
	@Transactional
	public List<CameraCfg> findInUseCmrs(){
		return cameraCfgRepository.meFindInUseCameras();
	}
	
	@Transactional
	public void saveCmrData(CameraData cmrData){
		cameraDataRepository.save(cmrData);
	}
	
	@Transactional
	public void saveCmrAlert(CameraAlert cmrAlert){
		cameraAlertRepository.save(cmrAlert);
	}
	
	@Transactional
	public boolean meHasUnProcessedCmrAlert(String sn,Integer type){
		return cameraAlertRepository.meHasUnProcessedCmrAlert(sn, type);
	}
	
	/**
	 * 统计一天之内某个点的进出总量
	 * @param sn
	 * @param date
	 * @return
	 */
	@Transactional
	public JsonResult calcuOnedayInOut(String sn,String date){
		if(Utils.isEmpty(sn)){
			return new JsonResult(false, "SN is not valid: sn="+sn);
		}
//		String startStr = date+" 00:00:00";
//		String endStr = date+" 23:59:59";
//		Date startTime = DateProcess.toDate(startStr, DateProcess.format_yyyy_MM_dd_HH_mm_ss);
//		Date endTime = DateProcess.toDate(endStr, DateProcess.format_yyyy_MM_dd_HH_mm_ss);
//		if(startTime == null || endTime == null){
//			return new JsonResult(false, "Start or end time is not valid: data="+date);
//		}
		CameraCfg camera = cameraCfgRepository.findBySn(sn);
		if(camera == null)
			return new JsonResult(false, "SN is not valid, data is not exist: sn="+sn);
		
		Map<String, Object> mapCon = new HashMap<String, Object>();
		mapCon.put("snfull", sn);
		mapCon.put("startTime", camera.oneDayFirstRecordTime(date));
		mapCon.put("endTime", camera.oneDayLastRecordTime(date));
		
		PageData<CameraData> pageData = cameraDataRepository.searchBy(mapCon, null);
		if(Utils.isEmpty(pageData.getContent())){
			return new JsonResult(false, "No data found: sn="+sn+", data="+date);
		}
		int sumIn = 0;
		int sumOut = 0;
		for(CameraData po: pageData.getContent()){
			sumIn += po.getDin().intValue();
			sumOut += po.getDout().intValue();
		}
		JsonResult jsonResult = new JsonResult(true);
		jsonResult.put("sumIn", sumIn);
		jsonResult.put("sumOut", sumOut);
		jsonResult.put("date", date);
		jsonResult.put("sn", sn);
		
		return jsonResult;
	}
	
	/**
	 * 找出需要发送邮件的那些报警
	 */
	@Transactional
	public void checkCmrAlertAndSendMail(int maxCount){
		List<CameraAlert> poList = cameraAlertRepository.meNeedToSendMail(maxCount);
		if(Utils.isEmpty(poList))
			return;
		//组装需要发送的html格式
		String html = "设备报警信息列表：";
		//找到这些记录的相机备注信息
		Map<String, String> sncmrMap = new HashMap<String, String>();
		for(CameraAlert po: poList){
			String cmrMark = sncmrMap.get(po.getSn());
			if(cmrMark == null){
				//数据库获取，如果没有备注，则为空
				CameraCfg cfg = cameraCfgRepository.findBySn(po.getSn());
				cmrMark = (cfg!=null && !Utils.isEmpty(cfg.getMark()))?cfg.getMark():"";
				sncmrMap.put(po.getSn(), cmrMark);
			}
			po.setCmrMark(cmrMark);
			html += "<br/>"+po.forMailStr();
		}
		sncmrMap.clear();
		
		//发送邮件
		boolean result = SendMail.send126Mail("系统通知: 相机报警记录, 数量: "+poList.size(), html);
		logger.info("Send mail to 126. "+result);
		if(result){
			//如果成功了，则把这些都改成已经发送邮件了
			long nowTime = System.currentTimeMillis();
			for(CameraAlert po: poList){
				po.setMailTime(nowTime);
			}
			cameraAlertRepository.save(poList);
		}
		
	}
	
	
	
	/**
	 * 通过直接jdbc方式，统计一下，一天内相机获得的原始数据记录条数
	 * @param sn
	 * @param oneDayStr
	 * @return
	 */
	public int getCmrOnedayRawIOCountJDBC(CameraCfg camera,String oneDayStr){
		if(camera == null)
			return -1;
		try {
			int total = 0;
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			String sql = "select ioRawData from tcameradata where sn='"+camera.getSn()+"' and ioRawData is not null and time between "+camera.oneDayFirstRecordTime(oneDayStr)+" and "+camera.oneDayLastRecordTime(oneDayStr);
			ResultSet rs = jdbc.executeQuery(sql);
			while(rs.next()){
				String ioRawData = rs.getString(1);
				if(Utils.isEmpty(ioRawData))
					continue;
				List<RecvInOutData> rawDataList = RecvInOutData.parsePCSFlowInfo(new String(ByteUtils.hexToByteArray(ioRawData)));
				if(Utils.isEmpty(rawDataList))
					continue;
				total += rawDataList.size();
				rawDataList.clear();
			}
			rs.close();
			
			jdbc.stopConnection();
			
			return total;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
	}
	
	/**
	 * 以jdbc方式统计一天某个相机的in out 总和
	 * @param sn
	 * @param oneDayStr
	 * @return
	 */
	public String calcuOnedayInOutJDBC(CameraCfg camera,String oneDayStr){
		if(camera == null)
			return "-1,-1";
		try {
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			String sql = "select sum(din),sum(dout) from tcameradata where sn='"+camera.getSn()+"' and time between "+camera.oneDayFirstRecordTime(oneDayStr)+" and "+camera.oneDayLastRecordTime(oneDayStr);
			ResultSet rs = jdbc.executeQuery(sql);
			int sumIn = 0;
			int sumOut = 0;
			if(rs.next()){
				sumIn = rs.getInt(1);
				sumOut = rs.getInt(2);
			}
			rs.close();
			jdbc.stopConnection();
			
			return sumIn+","+sumOut;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "-1,-1";
		}
	}
	
	/**
	 * 使用jdbc方式找出某一天的原始数据集合
	 * @param camera
	 * @param oneDayStr
	 * @return
	 */
	public List<RecvInOutData> getCmrOnedayRawInOutDatasJDBC(CameraCfg camera,String oneDayStr){
		List<RecvInOutData> list = new ArrayList<RecvInOutData>();
		if(camera == null)
			return list;
		try {
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			String sql = "select ioRawData from tcameradata where sn='"+camera.getSn()+"' and ioRawData is not null and time between "+camera.oneDayFirstRecordTime(oneDayStr)+" and "+camera.oneDayLastRecordTime(oneDayStr);
			ResultSet rs = jdbc.executeQuery(sql);
			
			while(rs.next()){
				String ioRawData = rs.getString(1);
				if(Utils.isEmpty(ioRawData))
					continue;
				List<RecvInOutData> rawDataList = RecvInOutData.parsePCSFlowInfo(new String(ByteUtils.hexToByteArray(ioRawData)));
				if(Utils.isEmpty(rawDataList))
					continue;
				list.addAll(rawDataList);
				
				rawDataList.clear();
			}
			rs.close();
			
			jdbc.stopConnection();
			
			//排序
			if(!Utils.isEmpty(list)){
				Collections.sort(list, new ComparatorRecvInOutData());
			}
			
			return list;
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return list;
		}
	}
	
	/**
	 * 获得某个相机一天之内得到的全部原始记录数据，数据库中存在的
	 * 并根据从小到大的顺序排列
	 * @param sn
	 * @param onedayStr yyyy-MM-dd格式
	 * @return
	 */
	@Transactional
	public List<RecvInOutData> getCameraOnedayRawInOutDatas(CameraCfg camera,String oneDayStr){
		List<RecvInOutData> list = new ArrayList<RecvInOutData>();
		CameraCfg cmrPo = camera;
		if(cmrPo == null)
			return list;
		Map<String, Object> mapCon = new HashMap<String, Object>();
		mapCon.put("snfull", cmrPo.getSn());
		mapCon.put("startTime", cmrPo.oneDayFirstRecordTime(oneDayStr));
		mapCon.put("endTime", cmrPo.oneDayLastRecordTime(oneDayStr));
		PageData<CameraData> pageData = cameraDataRepository.searchBy(mapCon, null);
		if(pageData.getTotal()==0l || Utils.isEmpty(pageData.getContent()))
			return list;
		
		//从每条 CameraData 中获得原始数据
		for(CameraData po: pageData.getContent()){
			String ioRawData = po.getIoRawData();
			if(Utils.isEmpty(ioRawData))
				continue;
			List<RecvInOutData> rawDataList = RecvInOutData.parsePCSFlowInfo(new String(ByteUtils.hexToByteArray(ioRawData)));
			if(Utils.isEmpty(rawDataList))
				continue;
			list.addAll(rawDataList);
			
			rawDataList.clear();
		}
		pageData.getContent().clear();
		
		
		if(Utils.isEmpty(list))
			return list;
		
		//System.out.println(oneDayStr+"->"+list.size());
		
		//对全部的原始数据进行排序
		Collections.sort(list, new ComparatorRecvInOutData());
		
		return list;
	}
	
	public List<RecvInOutData> getCameraOnedayRawInOutDatas(String sn,String oneDayStr){
		return getCameraOnedayRawInOutDatas(cameraCfgRepository.findBySn(sn),oneDayStr);
	}
	
	/**
	 * 获得某个相机，一段时间内的丢包率统计数据
	 * @param camera
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Transactional
	@Deprecated
	public List<PjOnedayRawIOLostInfo> getCameraRawInLostInfos(CameraCfg camera,String startDateStr,String endDateStr){
		List<PjOnedayRawIOLostInfo> list = new ArrayList<PjOnedayRawIOLostInfo>();
		Date startDate = DateProcess.toDate(startDateStr, "yyyy-MM-dd");
		Date endDate = DateProcess.toDate(endDateStr, "yyyy-MM-dd");
		if(startDate == null || endDate == null)
			return list;
		long startTime = startDate.getTime();
		int onedayShould = camera.oneDayRawTotalMinutes();
		while(startTime<=endDate.getTime()){
			Date curDate = new Date(startTime);
			String oneDayStr = DateProcess.toString(curDate, "yyyy-MM-dd");
			List<RecvInOutData> rawDatas = getCameraOnedayRawInOutDatas(camera, oneDayStr);
			PjOnedayRawIOLostInfo lostInfo = new PjOnedayRawIOLostInfo(oneDayStr, rawDatas, onedayShould);
			list.add(lostInfo);
			//System.out.println("日期="+lostInfo.getDayShort()+", onedayShould="+lostInfo.getOnedayShould()+", onedayActual="+lostInfo.getOnedayActual()+", completePercent="+lostInfo.getCompletePercent()+", lostPercent="+lostInfo.getLostPercent());
			//变成下一天
			startTime += DateProcess.onedaymm;
		}
		
		return list;
	}
	
	public List<PjOnedayRawIOLostInfo> getCameraRawInLostInfos(String sn,String startDateStr,String endDateStr){
		return getCameraRawInLostInfos(cameraCfgRepository.findBySn(sn), startDateStr, endDateStr);
	}
}
