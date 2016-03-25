package com.xie.spot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ssin.util.DateProcess;
import ssin.util.MyStringUtil;

import com.eg.intf3.security.ByteUtils;
import com.suyou.singnalway.RecvInOutData;
import com.xie.spot.entity.CameraAlert;
import com.xie.spot.entity.CameraCfg;
import com.xie.spot.entity.CameraData;
import com.xie.spot.entity.User;
import com.xie.spot.pojo.ComparatorRecvInOutData;
import com.xie.spot.pojo.PjCameraData;
import com.xie.spot.pojo.PjCmrCfg;
import com.xie.spot.pojo.PjCmrData;
import com.xie.spot.pojo.PjCmrDataInner;
import com.xie.spot.pojo.PjOnedayLostMinute;
import com.xie.spot.pojo.PjOnedayRawIOLostInfo;
import com.xie.spot.pojo.spotshow.OneSpotCfg;
import com.xie.spot.pojo.spotshow.SpotsShowCfgByJson;
import com.xie.spot.repository.CameraAlertRepository;
import com.xie.spot.repository.CameraCfgRepository;
import com.xie.spot.repository.CameraDataRepository;
import com.xie.spot.service.CameraOperService;
import com.xie.spot.sys.CameraManager;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JsonResult;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;
import com.xie.spot.sys.utils.ffmpeg.LiveStreamUser;
import com.xie.spot.sys.utils.ffmpeg.RtmpManager;
import com.xie.spot.sys.utils.ffmpeg.UserSN;

/**
 * 和摄像头设备相关的操作
 * 
 * @author IcekingT420
 * 
 */
@Controller
public class CameraController {
	@Autowired
	private CameraCfgRepository cameraCfgRepository;
	@Autowired
	private CameraDataRepository cameraDataRepository;
	@Autowired
	private CameraAlertRepository cameraAlertRepository;
	@Autowired
	private CameraManager cameraManager;
	@Autowired
	private CameraOperService cameraOperService;

	/**
	 * 查询设备列表
	 * 
	 * @param request
	 * @param pageParam
	 * @return
	 */
	@RequestMapping(value = "/listCameraCfg", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listCameraCfg(HttpServletRequest request, PageParam pageParam) {
		if (request.getParameter("fistTime") != null
				&& request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return "{}";
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		String sn = Utils.getParamValue(request, "sn");
		if (!Utils.isEmpty(sn))
			mapCon.put("sn", sn);
		String mark = Utils.getParamValue(request, "mark");
		if (!Utils.isEmpty(mark))
			mapCon.put("mark", mark);
		Integer online = Utils.getParamValueInt(request, "online");
		if (online != null && online >= 0) {
			mapCon.put("online", online);
		}
		Integer inUse = Utils.getParamValueInt(request, "inUse");
		if(inUse!=null && inUse.intValue()>=0){
			mapCon.put("inUse", inUse);
		}
		String scene = Utils.getParamValue(request, "scene");
		if (!Utils.isEmpty(scene))
			mapCon.put("scene", scene);
		
		PageData<CameraCfg> pageData = cameraCfgRepository.searchBy(mapCon,
				pageParam);
		JsonResult jsonResult = new JsonResult(true, pageData.getTotal(),
				pageData.getContent());
		return jsonResult.toString();
	}

	/**
	 * 新增摄像机设备
	 * 
	 * @param request
	 * @param sn
	 * @param sim
	 * @param telNo
	 * @param mark
	 * @return
	 */
	@RequestMapping(value = "/addCameraCfg", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String addCameraCfg(HttpServletRequest request, String sn,
			String sim, String telNo, String mark, String provider, String puk,
			int inoutInterval, int tkpInterval, int tkpHourSt, int tkpHourEd,byte inUse,int offlineTimeout,String scene) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if (Utils.isEmpty(sn)) {
			return new JsonResult(false, "缺少必要参数 sn").toString();
		}
		// 判断这个sn是否已经存在了
		if (cameraCfgRepository.meIsSnExist(sn, null)) {
			return new JsonResult(false, "摄像机已经存在. sn=" + sn).toString();
		}
		CameraCfg po = new CameraCfg();
		po.setSn(sn);
		po.setSim(sim);
		po.setTelNo(telNo);
		po.setMark(mark);
		po.setProvider(provider);
		po.setPuk(puk);
		po.setInoutInterval(inoutInterval);
		po.setTkpInterval(tkpInterval);
		po.setTkpHourSt(tkpHourSt);
		po.setTkpHourEd(tkpHourEd);
		po.setInUse(inUse);
		po.setOfflineTimeout(offlineTimeout);
		po.setScene(scene);

		po = cameraCfgRepository.save(po);

		return new JsonResult(true).toString();
	}
	
	/**
	 * 将相机的几个参数应用到全部的相机去
	 * @param request
	 * @param inoutInterval
	 * @param tkpInterval
	 * @param tkpHourSt
	 * @param tkpHourEd
	 * @return
	 */
	@RequestMapping(value = "/setCameraParamsToAll", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String setCameraParamsToAll(HttpServletRequest request,int inoutInterval, int tkpInterval, int tkpHourSt, int tkpHourEd,int offlineTimeout){
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		cameraCfgRepository.updateParamsToAll(inoutInterval, tkpInterval, tkpHourSt, tkpHourEd,offlineTimeout);
		
		return new JsonResult(true).toString();
	}

	/**
	 * 修改摄像机配置
	 * 
	 * @param request
	 * @param sn
	 * @param sim
	 * @param telNo
	 * @param mark
	 * @return
	 */
	@RequestMapping(value = "/modifyCameraCfg", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String modifyCameraCfg(HttpServletRequest request, Long id,
			String sn, String sim, String telNo, String mark, String provider, String puk,
			int inoutInterval, int tkpInterval, int tkpHourSt, int tkpHourEd,byte inUse,int offlineTimeout,String scene) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if (Utils.isEmptyId(id)) {
			return new JsonResult(false, "缺少必要参数 id").toString();
		}
		if (Utils.isEmpty(sn)) {
			return new JsonResult(false, "缺少必要参数 sn").toString();
		}
		CameraCfg po = cameraCfgRepository.findOne(id);
		if (po == null) {
			return new JsonResult(false, "数据库检索失败 CameraCfg.id=" + id)
					.toString();
		}
		// 如果sn发生了编号，判断新的sn是否已经存在了
		if (!sn.equals(po.getSn())) {
			if (cameraCfgRepository.meIsSnExist(sn, po.getId())) {
				return new JsonResult(false, "摄像机已经存在. sn=" + sn).toString();
			}
		}
		po.setSn(sn);
		po.setSim(sim);
		po.setTelNo(telNo);
		po.setMark(mark);
		po.setProvider(provider);
		po.setPuk(puk);
		po.setInoutInterval(inoutInterval);
		po.setTkpInterval(tkpInterval);
		po.setTkpHourSt(tkpHourSt);
		po.setTkpHourEd(tkpHourEd);
		po.setInUse(inUse);
		po.setOfflineTimeout(offlineTimeout);
		po.setScene(scene);

		po = cameraCfgRepository.save(po);

		return new JsonResult(true).toString();
	}

	/**
	 * 删除摄像机配置
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteCameraCfg", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteCameraCfg(HttpServletRequest request, Long id) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if (Utils.isEmptyId(id)) {
			return new JsonResult(false, "缺少必要参数 id").toString();
		}
		CameraCfg po = cameraCfgRepository.findOne(id);
		if (po == null) {
			return new JsonResult(false, "数据库检索失败 CameraCfg.id=" + id)
					.toString();
		}
		// 判断是否可以删除
		if (!cameraCfgRepository.canDelete(po)) {
			return new JsonResult(false, "数据被引用，无法删除. sn=" + po.getSn())
					.toString();
		}

		cameraCfgRepository.delete(po);

		return new JsonResult(true).toString();
	}

	/**
	 * 查询摄像机的数据
	 * 
	 * @param request
	 * @param pageParam
	 * @return
	 */
	@RequestMapping(value = "/listCameraData", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listCameraData(HttpServletRequest request, PageParam pageParam) {
		if (request.getParameter("fistTime") != null
				&& request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return "{}";
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		String sn = Utils.getParamValue(request, "sn");
		if (!Utils.isEmpty(sn))
			mapCon.put("sn", sn);
		String startTime = Utils.getParamValue(request, "startTime");
		if (!Utils.isEmpty(startTime)) {
			Date dateS = DateProcess.toDate(startTime, "yyyy-MM-dd");
			if (dateS != null)
				mapCon.put("startTime", dateS.getTime());
		}
		String endTime = Utils.getParamValue(request, "endTime");
		if (!Utils.isEmpty(endTime)) {
			Date dateS = DateProcess.toDate(endTime, "yyyy-MM-dd");
			if (dateS != null)
				mapCon.put("endTime", dateS.getTime());
		}
		PageData<CameraData> pageData = cameraDataRepository.searchBy(mapCon,
				pageParam);
		List<PjCameraData> listData = new ArrayList<PjCameraData>();
		if(!Utils.isEmpty(pageData.getContent())){
			//通过sn得到相机配置，主要是为了获得备注信息使用
			Map<String, String> sncmrMap = new HashMap<String, String>();
			for(CameraData po: pageData.getContent()){
				PjCameraData pjData = new PjCameraData(po);
				String cmrMark = sncmrMap.get(po.getSn());
				if(cmrMark == null){
					//数据库获取，如果没有备注，则为空
					CameraCfg cfg = cameraCfgRepository.findBySn(po.getSn());
					cmrMark = (cfg!=null && !Utils.isEmpty(cfg.getMark()))?cfg.getMark():"";
					sncmrMap.put(po.getSn(), cmrMark);
				}
				pjData.setCmrMark(cmrMark);
				listData.add(pjData);
			}
			sncmrMap.clear();
		}
		JsonResult jsonResult = new JsonResult(true, pageData.getTotal(),
				listData);
		return jsonResult.toString();
	}

	@RequestMapping(value = "/deleteCameraData", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteCameraData(HttpServletRequest request, Long id) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if (Utils.isEmptyId(id)) {
			return new JsonResult(false, "缺少必要参数 id").toString();
		}
		CameraData po = cameraDataRepository.findOne(id);
		if (po == null) {
			return new JsonResult(false, "数据库检索失败 CameraData.id=" + id)
					.toString();
		}

		cameraDataRepository.delete(po);

		return new JsonResult(true).toString();
	}
	
	/**
	 * 某个相机一段时间的丢包情况列表
	 * @param request
	 * @param sn
	 * @param startDateStr
	 * @param endDateStr
	 * @return
	 */
	@RequestMapping(value = "/listCameraLostRawDataInfo", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String listCameraLostRawDataInfo(HttpServletRequest request, String sn,String startDateStr,String endDateStr) {
		if (request.getParameter("fistTime") != null
				&& request.getParameter("fistTime").equals("true"))
			return new JsonResult(false, "fistTime").toString();
		
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) 
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		
		if(Utils.isEmpty(sn) || Utils.isEmpty(startDateStr) || Utils.isEmpty(endDateStr))
			return new JsonResult(false, "缺少必要参数").toString();
		
		CameraCfg camera = cameraOperService.findCameraBySN(sn);
		if(camera == null)
			return new JsonResult(false, "相机配置检索失败. SN="+sn).toString();
		
		List<PjOnedayRawIOLostInfo> list = new ArrayList<PjOnedayRawIOLostInfo>();
		Date startDate = DateProcess.toDate(startDateStr, "yyyy-MM-dd");
		Date endDate = DateProcess.toDate(endDateStr, "yyyy-MM-dd");
		
		if(startDate == null || endDate == null)
			return new JsonResult(false, "日期格式不正确").toString();
		
		int onedayShould = camera.oneDayRawTotalMinutes();
		
		long startTime = startDate.getTime();
		while(startTime<=endDate.getTime()){
			Date curDate = new Date(startTime);
			String oneDayStr = DateProcess.toString(curDate, "yyyy-MM-dd");
			int onedayActual = cameraOperService.getCmrOnedayRawIOCountJDBC(camera, oneDayStr);
			startTime += DateProcess.onedaymm;
			if(onedayActual == 0){
				continue;
			}
			PjOnedayRawIOLostInfo lostInfo = new PjOnedayRawIOLostInfo(oneDayStr, onedayActual, onedayShould);
			lostInfo.setSn(sn);
			//统计这一天的in和out求和
			String sumio = cameraOperService.calcuOnedayInOutJDBC(camera, oneDayStr);
			String[] arraysumio = MyStringUtil.getArrayFromStrByChar(sumio, ",");
			if(arraysumio != null && arraysumio.length==2){
				lostInfo.setSumIn(Integer.parseInt(arraysumio[0]));
				lostInfo.setSumOut(Integer.parseInt(arraysumio[1]));
			}
			list.add(lostInfo);
		}
		
		if(Utils.isEmpty(list))
			return new JsonResult(false, "数据检索失败. sn="+sn+", startDateStr="+startDateStr+", endDateStr="+endDateStr).toString();
		
		JsonResult jsonResult = new JsonResult(true, list.size(),list);
		jsonResult.put("sn", sn);
		jsonResult.put("cmrMark", camera.getMark());
		jsonResult.put("startDateStr", startDateStr);
		jsonResult.put("endDateStr", endDateStr);
		
		return jsonResult.toString();
	}
	
	/**
	 * 查看某个相机，一天之内，丢失掉的分钟数，阶段性的显示
	 * @param request
	 * @param sn
	 * @param oneDayStr
	 * @return
	 */
	@RequestMapping(value = "/detailCmrOnedayLostIOMinutes", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String detailCmrOnedayLostIOMinutes(HttpServletRequest request, String sn,String oneDayStr) {
		if (request.getParameter("fistTime") != null
				&& request.getParameter("fistTime").equals("true"))
			return new JsonResult(false, "fistTime").toString();
		
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) 
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		
		if(Utils.isEmpty(sn) || Utils.isEmpty(oneDayStr))
			return new JsonResult(false, "缺少必要参数").toString();
		
		CameraCfg camera = cameraOperService.findCameraBySN(sn);
		if(camera == null)
			return new JsonResult(false, "相机配置检索失败. SN="+sn).toString();
		
		//这一天进出的原始数据
		List<RecvInOutData> listRIOD = cameraOperService.getCmrOnedayRawInOutDatasJDBC(camera, oneDayStr);
		
		if(Utils.isEmpty(listRIOD))
			return new JsonResult(false, "数据检索失败. sn="+sn+", 日期="+oneDayStr).toString();
		
		//丢失的时间段
		List<PjOnedayLostMinute> list = new ArrayList<PjOnedayLostMinute>();
		//从这一天的开始，一分钟一分钟的去找是否存在
		long startMM = camera.oneDayFirstRecordTime(oneDayStr)+60000;
		long endMM = camera.oneDayLastRecordTime(oneDayStr);
		PjOnedayLostMinute lostMinute = null;
		while(startMM < endMM){
			RecvInOutData data = findRIODInListByTime(listRIOD,startMM);
			if(data != null){
				//这一分钟的数据是存在的，则从列表中删除掉
				if(lostMinute != null){
					//已经在记录了
					lostMinute.setEnd(startMM-60000);
					if(lostMinute.getEnd()>=lostMinute.getStart())
						list.add(lostMinute);
					lostMinute = null;
				}
				listRIOD.remove(data);
			}else {
				//不存在，则开始记录
				if(lostMinute == null){
					//开始记录
					lostMinute = new PjOnedayLostMinute();
					lostMinute.setStart(startMM);
				}
			}
			
			//增加一分钟
			startMM += 60000;
		}
		
		//最后结束的时候，还有一个开始对象，也需要保存进去
		if(lostMinute != null){
			lostMinute.setEnd(startMM);
			list.add(lostMinute);
			lostMinute = null;
		}
		
		if(Utils.isEmpty(list))
			return new JsonResult(false, "没有缺失的数据. sn="+sn+", 日期="+oneDayStr).toString();
		
		JsonResult jsonResult = new JsonResult(true, list.size(),list);
		jsonResult.put("sn", sn);
		jsonResult.put("cmrMark", camera.getMark());
		jsonResult.put("oneDayStr", oneDayStr);
		
		return jsonResult.toString();
	}
	
	/**
	 * 在一天的全部原始数据中根据时间找到，一分钟的数据
	 * @param listRIOD
	 * @return
	 */
	private RecvInOutData findRIODInListByTime(List<RecvInOutData> listRIOD,long time){
		if(Utils.isEmpty(listRIOD))
			return null;
		for(RecvInOutData data: listRIOD){
			if(data.getTimeMs() == time)
				return data;
		}
		return null;
	}
	
	/**
	 * 填补相机的缺失数据
	 * @param request
	 * @param sn
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/tianbuCmrOnedayLostIO", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String tianbuCmrOnedayLostIO(HttpServletRequest request, String sn,long startTime,long endTime) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) 
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		
		if(Utils.isEmpty(sn) || startTime==0l || endTime==0l || endTime<startTime)
			return new JsonResult(false, "缺少必要参数").toString();
		
		CameraCfg camera = cameraCfgRepository.findBySn(sn);
		if(camera == null){
			return new JsonResult(false, "设备配置信息不存在. SN="+sn).toString();
		}
		
		if(!camera.isCmrOnline())
			return new JsonResult(false, "设备未联机. SN="+sn).toString();
		
		//手动触发的去获取相机历史数据
		List<RecvInOutData> listNew = cameraManager.fetchCameraHisInOutDatasManually(camera, startTime, endTime);
		if(Utils.isEmpty(listNew))
			return new JsonResult(false, "该时间段内没有历史数据. SN="+sn+", 时间段: "+DateProcess.toString(new Date(startTime), "yyyy-MM-dd HH:mm")+" ~ "+DateProcess.toString(new Date(endTime), "yyyy-MM-dd HH:mm")).toString();
		
		//找到这个相机现在数据库内的原始数据，需要进去匹配，不要有重复的数据出现
		String oneDayStr = DateProcess.toString(new Date(endTime), "yyyy-MM-dd");
		List<RecvInOutData> listOld = cameraOperService.getCmrOnedayRawInOutDatasJDBC(camera, oneDayStr);
		
		//删除新获得的历史数据中，在原数据中已经存在的那些
		List<RecvInOutData> list = new ArrayList<RecvInOutData>();
		//新获得的数据，需要每个都去原来数据中判断一下是否已经存在了
		for(RecvInOutData data: listNew){
			if(findRIODInListByTime(listOld, data.getTimeMs()) != null)
				continue;
			list.add(data);
		}
		
		if(Utils.isEmpty(list))
			return new JsonResult(false, "没有需要填补的数据").toString();
		
		//对这些数据进行排序，并生成新的记录
		Collections.sort(list, new ComparatorRecvInOutData());
		
		//组装成原始数据格式，[2014-06-01 00:00],in:3,out:4;...[2014-06-30 23:59],in:0,out:0;
		String ioRawHisDataStr = RecvInOutData.parseBackToRawString(list);
		
		//合并成一条记录
		RecvInOutData inOutData = CameraManager.mergeToOne(list);
		
		//查找里记录时间最近的数据，要用于图片的引用
		CameraData nearestData = cameraOperService.getNearestByCameraData(sn, inOutData.getTimeMs());
		
		//生成新的数据对象
		CameraData cmrData = new CameraData();
		//这条记录的时间，就用最后个数据的时间
		cmrData.setTime(inOutData.getTimeMs());
		cmrData.setSn(camera.getSn());
		cmrData.setDin(inOutData.getInData());
		cmrData.setDout(inOutData.getOutData());
		cmrData.setPicUrl(nearestData!=null?nearestData.getPicUrl():"");
		cmrData.setRecvIOTime(inOutData.getTimeMs());
		cmrData.setRecvPicTime(nearestData!=null?nearestData.getRecvPicTime():0l);
		//两个lazy的数据也保存起来
		if(!Utils.isEmpty(ioRawHisDataStr))
			cmrData.setIoRawData(ByteUtils.byteArrayToHex(ioRawHisDataStr.getBytes()));
		
		cameraDataRepository.save(cmrData);
		
		return new JsonResult(true).toString();
		
	}
	
	/**
	 * 查询摄像机的报警记录
	 * 
	 * @param request
	 * @param pageParam
	 * @return
	 */
	@RequestMapping(value = "/listCameraAlert", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String listCameraAlert(HttpServletRequest request, PageParam pageParam) {
		if (request.getParameter("fistTime") != null
				&& request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return "{}";
		}
		Map<String, Object> mapCon = new HashMap<String, Object>();
		String sn = Utils.getParamValue(request, "sn");
		if (!Utils.isEmpty(sn))
			mapCon.put("sn", sn);
		String startTime = Utils.getParamValue(request, "startTime");
		if (!Utils.isEmpty(startTime)) {
			Date dateS = DateProcess.toDate(startTime, "yyyy-MM-dd");
			if (dateS != null)
				mapCon.put("startTime", dateS.getTime());
		}
		String endTime = Utils.getParamValue(request, "endTime");
		if (!Utils.isEmpty(endTime)) {
			Date dateS = DateProcess.toDate(endTime, "yyyy-MM-dd");
			if (dateS != null)
				mapCon.put("endTime", dateS.getTime());
		}
		Integer type = Utils.getParamValueInt(request, "type");
		if(type != null && type.intValue()>0){
			mapCon.put("type", type);
		}
		Integer processed = Utils.getParamValueInt(request, "processed");
		if(processed != null && processed.intValue()>0){
			mapCon.put("processed", processed);
		}
		
		PageData<CameraAlert> pageData = cameraAlertRepository.searchBy(mapCon,
				pageParam);
		
		if(!Utils.isEmpty(pageData.getContent())){
			//通过sn得到相机配置，主要是为了获得备注信息使用
			Map<String, String> sncmrMap = new HashMap<String, String>();
			for(CameraAlert po: pageData.getContent()){
				String cmrMark = sncmrMap.get(po.getSn());
				if(cmrMark == null){
					//数据库获取，如果没有备注，则为空
					CameraCfg cfg = cameraCfgRepository.findBySn(po.getSn());
					cmrMark = (cfg!=null && !Utils.isEmpty(cfg.getMark()))?cfg.getMark():"";
					sncmrMap.put(po.getSn(), cmrMark);
				}
				po.setCmrMark(cmrMark);
			}
			sncmrMap.clear();
		}
		
		JsonResult jsonResult = new JsonResult(true, pageData.getTotal(),
				pageData.getContent());
		return jsonResult.toString();
	}
	
	/**
	 * 删除相机的报警信息
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deleteCameraAlert", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteCameraAlert(HttpServletRequest request, String ids) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(ids)){
			return new JsonResult(false,"缺少必要参数 ids").toString();
		}
		String[] idArray = MyStringUtil.getArrayFromStrByChar(ids, "_");
		if(idArray==null || idArray.length == 0){
			return new JsonResult(false,"缺少必要参数 ids (2)").toString();
		}
		List<CameraAlert> poList = new ArrayList<CameraAlert>();
		for(String idStr: idArray){
			long idL = Utils.parseLong(idStr);
			if(idL<=0)
				continue;
			CameraAlert po = cameraAlertRepository.findOne(idL);
			if(po == null)
				continue;
			poList.add(po);
		}
		
		cameraAlertRepository.delete(poList);

		return new JsonResult(true).toString();
	}
	
	/**
	 * 填写处理内容
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/processCameraAlert", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String processCameraAlert(HttpServletRequest request, String ids,String prsMark) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if(Utils.isEmpty(ids)){
			return new JsonResult(false,"缺少必要参数 ids").toString();
		}
		String[] idArray = MyStringUtil.getArrayFromStrByChar(ids, "_");
		if(idArray==null || idArray.length == 0){
			return new JsonResult(false,"缺少必要参数 ids (2)").toString();
		}
		if(Utils.isEmpty(prsMark)){
			return new JsonResult(false, "缺少必要参数 prsMark").toString();
		}
		List<CameraAlert> poList = new ArrayList<CameraAlert>();
		long nowTime = System.currentTimeMillis();
		for(String idStr: idArray){
			long idL = Utils.parseLong(idStr);
			if(idL<=0)
				continue;
			CameraAlert po = cameraAlertRepository.findOne(idL);
			if(po == null)
				continue;
			po.setPrsTime(nowTime);
			po.setPrsMark(prsMark);
			poList.add(po);
		}
		
		cameraAlertRepository.save(poList);

		return new JsonResult(true).toString();
	}

	
	/**
	 * 手动触发在线相机统计
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateCameraOnlineManually", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String updateCameraOnlineManually(HttpServletRequest request) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		cameraManager.updateCameraOnlineManually();
		return new JsonResult(true).toString();
	}
	
	/**
	 * 手工重启相机的监听
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/resetCameraListenerManually", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String resetCameraListenerManually(HttpServletRequest request) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		cameraManager.resetCameraListenerManually();
		return new JsonResult(true).toString();
	}
	
	/**
	 * 手工设置某个相机的时间
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/setCameraTimeManually", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String setCameraTimeManually(HttpServletRequest request, Long id) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		//需要设置时间的相机，如果id存在，则指定一个，否则，全部联机的相机
		List<CameraCfg> cmrList = null;
		if(!Utils.isEmptyId(id)){
			//指定一个相机
			CameraCfg po = cameraCfgRepository.findOne(id);
			if (po == null) {
				return new JsonResult(false, "相机配置信息不存在. CameraCfg.id=" + id)
						.toString();
			}
			if (!po.isCmrOnline()) {
				return new JsonResult(false, "相机未联机. SN=" + po.getSn() + ", IP="
						+ po.getIp()).toString();
			}
			cmrList = new ArrayList<CameraCfg>();
			cmrList.add(po);
		}else{
			//id未指定，找出全部联机的相机
			cmrList = cameraCfgRepository.meFindOnlineCameras();
		}
		
		if(Utils.isEmpty(cmrList)){
			return new JsonResult(false, "没有需要下发设置时间的相机")
			.toString();
		}
		

		// 发送数据
		if (!cameraManager.setCameraTimeManually(cmrList)) {
			return new JsonResult(false, "相机时间设置失败，正在执行下行操作。" ).toString();
		}

		return new JsonResult(true).toString();
	}

	/**
	 * 手工方式去获取摄像机的图像信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fetchCameraManually", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String fetchCameraManually(HttpServletRequest request, Long id) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if (Utils.isEmptyId(id)) {
			return new JsonResult(false, "缺少必要参数id").toString();
		}
		CameraCfg po = cameraCfgRepository.findOne(id);
		if (po == null) {
			return new JsonResult(false, "设备配置信息不存在. CameraCfg.id=" + id)
					.toString();
		}
		if (po.getOnline() == null || po.getOnline().intValue() != 1) {
			return new JsonResult(false, "设备未联机. SN=" + po.getSn() + ", IP="
					+ po.getIp()).toString();
		}

		// 发送数据
		if (!cameraManager.fetchDataImgManually(po)) {
			return new JsonResult(false, "该设备正在获取数据. SN=" + po.getSn()
					+ ", IP=" + po.getIp()).toString();
		}

		return new JsonResult(true).toString();

	}
	
	/**
	 * 查看某个获得数据的详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/viewCameraDataInfo", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String viewCameraDataInfo(HttpServletRequest request, Long id) {
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null || !loginUser.isSuperAdmin()) {
			return new JsonResult(false, "缺少当前管理帐号的登入信息").toString();
		}
		if (Utils.isEmptyId(id)) {
			return new JsonResult(false, "缺少必要参数id").toString();
		}
		CameraData po = cameraDataRepository.findOne(id);
		if(po == null){
			return new JsonResult(false, "数据库检索失败. CameraData.id="+id).toString();
		}
		//得到原始数据
		String ioRawData = po.getIoRawData();
		if(Utils.isEmpty(ioRawData)){
			return new JsonResult(false, "原始数据不存在. CameraData.ioRawData=null").toString();
		}
		List<RecvInOutData> rawDataList = RecvInOutData.parsePCSFlowInfo(new String(ByteUtils.hexToByteArray(ioRawData)));
		if(Utils.isEmpty(rawDataList)){
			return new JsonResult(false, "原始数据解析错误. CameraData.ioRawData="+ioRawData).toString();
		}
		String picTime = "----";
		if(po.getRecvPicTime()!=null && po.getRecvPicTime().longValue()>0l)
			picTime = DateProcess.toString(new Date(po.getRecvPicTime().longValue()), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
		String picUrl = po.getPicUrl();
		JsonResult jsonResult = new JsonResult(true);
		jsonResult.put("picTime", picTime);
		jsonResult.put("picUrl", picUrl);
		jsonResult.putRows(rawDataList);
		
		return jsonResult.toString();
	}
	
	/**
	 * 计算某个相机每天的进出量
	 * @param request
	 * @param sn
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/calcuOnedayInOut", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String calcuOnedayInOut(HttpServletRequest request, String sn,String date) {
		return cameraOperService.calcuOnedayInOut(sn, date).toString();
	}
	
	@RequestMapping(value = "/calcuSpotInOut", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String calcuSpotInOut(HttpServletRequest request, int spotNo,String date){
		if(spotNo<=0){
			return new JsonResult(false, "缺少必要参数. spotNo.").toString();
		}
		OneSpotCfg one = SpotsShowCfgByJson.getInstance().getOneByNo(spotNo);
		if(one == null){
			return new JsonResult(false, "无法找到风景点. spotNo="+spotNo).toString();
		}
		if(!one.action(date)){
			return new JsonResult(false, "统计数据失败. spotNo="+spotNo+", date="+date).toString();
		}
		JsonResult jsonResult = new JsonResult(true);
		jsonResult.put("sumIn", one.sumIn());
		jsonResult.put("sumOut", one.sumOut());
		jsonResult.put("sumStay", one.sumStay());
		jsonResult.put("date", one.preActionDate());
		jsonResult.put("spotNo", one.getSpotNo());
		jsonResult.put("spotName", one.getSpotName());
		
		return jsonResult.toString();
	}
	
	/**
	 * 得到某个相机最新的图片，包括所略图
	 * @param request
	 * @param sn
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "/latestPicOfSN", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String latestPicOfSN(HttpServletRequest request, String sn) {
		CameraData data = cameraDataRepository.getLatestPic(sn);
		if(data == null){
			return new JsonResult(false).toString();
		}
		JsonResult jsonResult = new JsonResult(true);
		jsonResult.put("sn", sn);
		String picOri = data.getPicUrl();
		//原始图片
		jsonResult.put("picOri", picOri);
		//缩略图
		jsonResult.put("picUrl", picOri.substring(0, picOri.length()-4)+"_min.jpg");
		//图片的时间，格式yyyy-MM-dd HH:mm:ss
		String picTime = "";
		//一般图片的格式为：uploadfiles/cameraPics/7_1451901028954_75582_1920_1080.jpg
		int last1 = picOri.lastIndexOf('/');
		int last2 = picOri.lastIndexOf('.');
		if(last1 != -1 && last2 != -1){
			String picFileName = picOri.substring(last1+1,last2);
			//picFileName的格式：7_1451901028954_75582_1920_1080
			String[] array1 = MyStringUtil.getArrayFromStrByChar(picFileName, "_");
			if(array1 != null && array1.length == 5){
				long time1 = Utils.parseLong(array1[1]);
				if(time1>0){
					picTime = DateProcess.toString(new Date(time1), "yyyy-MM-dd HH:mm:ss");
				}
			}
		}
		jsonResult.put("picTime", picTime);
		return jsonResult.toString();
	}

	@RequestMapping(value = "/cmr/dvsdata", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String cmrdvsdata(HttpServletRequest request) {
		String sn = Utils.getParamValue(request, "sn");
		Long timeS = Utils.getParamValueLong(request, "timeS");
		Long timeE = Utils.getParamValueLong(request, "timeE");
		Integer count = Utils.getParamValueInt(request, "count");
		// 如果有sn，则只有一个，否则全部的数据库中sn
		List<String> snList = new ArrayList<String>();
		if (!Utils.isEmpty(sn)) {
			snList.add(sn);
		} else {
			// 所有的数据
			List<CameraCfg> listPo = cameraCfgRepository.findAll();
			if (!Utils.isEmpty(listPo)) {
				for (CameraCfg po : listPo) {
					snList.add(po.getSn());
				}
			}
		}
		if (Utils.isEmpty(snList))
			return "[]";

		// 根据sn进行循环查询
		List<PjCmrData> list = new ArrayList<PjCmrData>();
		for (String snfull : snList) {
			Map<String, Object> mapCon = new HashMap<String, Object>();
			mapCon.put("snfull", snfull);
			if (timeS != null && timeS.longValue() > 0)
				mapCon.put("startTime", timeS.longValue());
			if (timeE != null && timeE.longValue() > 0)
				mapCon.put("endTime", timeE.longValue());

			int ctI = 1;
			if (count != null && count.intValue() > 0)
				ctI = count.intValue();

			PageData<CameraData> pageData = cameraDataRepository.searchBy(
					mapCon, new PageParam(0, ctI));
			if (Utils.isEmpty(pageData.getContent()))
				continue;

			PjCmrData pjData = new PjCmrData();
			pjData.setSn(pageData.getContent().get(0).getSn());
			for (CameraData poData : pageData.getContent()) {
				PjCmrDataInner inndata = new PjCmrDataInner();
				inndata.setTime(poData.getTime());
				inndata.setIn(poData.getDin());
				inndata.setOut(poData.getDout());
				inndata.setPicUrl(poData.getPicUrl());

				pjData.addData(inndata);
			}

			list.add(pjData);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("videos", list);
		// JSONArray array = JSONArray.fromObject(list);
		return jsonObject.toString();
	}
	
	/**
	 * 给蒋涛对接的 返回目前摄像机的信息，全部
	 * 
	 * @param request
	 * @param pageParam
	 * @return
	 */
	@RequestMapping(value = "/cmr/dvsinfo", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String cmrdvsinfo(HttpServletRequest request) {
		List<CameraCfg> listPo = cameraCfgRepository.findAll();
		if (Utils.isEmpty(listPo))
			return "[]";
		List<PjCmrCfg> list = new ArrayList<PjCmrCfg>();
		for (CameraCfg po : listPo) {
			PjCmrCfg pj = new PjCmrCfg();
			pj.setSn(po.getSn());
			pj.setIp(po.getIp());
			pj.setMark(po.getMark());
			pj.setSim(po.getSim());

			list.add(pj);
		}
		JSONArray array = JSONArray.fromObject(list);
		return array.toString();
	}
	
	/**
	 * 蒋涛接口，快速拍照
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/cmr/takepicture", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String takepicture(HttpServletRequest request, String sn) {
		if (Utils.isEmpty(sn)) {
			return new JsonResult(false, "No parameter sn").toString();
		}
		CameraCfg po = cameraCfgRepository.findBySn(sn);
		if (po == null) {
			return new JsonResult(false, "CameraCfg is not exist. CameraCfg.sn=" + sn)
					.toString();
		}
		if (!po.isCmrOnline()) {
			return new JsonResult(false, "Camera is not online. SN=" + po.getSn() + ", IP="
					+ po.getIp()).toString();
		}
		
		return cameraManager.takeAPictureManually(po);

	}
	
	/**
	 * 视频直播流
	 * http://localhost:8080/sc/cmr/livestream?sn=aa-bb-cc&oper=start&username=tmp&password=123456
	 * @param request
	 * @param sn
	 * @return
	 */
	@RequestMapping(value = "/cmr/livestream", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String livestream(HttpServletRequest request, String sn,String oper) {
		
		if (Utils.isEmpty(sn)) {
			return new JsonResult(false, "No parameter sn").toString();
		}
		
		//调用视频，必须要指定用户名和密码
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		if (loginUser == null) {
			//当缺少登入信息的时候，必须判断指定的用户名
			UserSN userSN = LiveStreamUser.findUserBy(request.getParameter("username"), request.getParameter("password"));
			if(userSN == null){
				return new JsonResult(false, "No auth info").toString();
			}
			//判断sn是否属于管理范围的
			if(!userSN.containsSN(sn)){
				return new JsonResult(false, "User: "+userSN.getUser()+" has no right to reach sn: "+sn).toString();
			}
		}
		
		
		boolean blStart = (oper!=null && oper.equals("start"));
		
		//知道相机配置对象，获得运行状态
		CameraCfg cameraCfg = cameraCfgRepository.findBySn(sn);
		if(cameraCfg == null){
			return new JsonResult(false, "No CameraCfg found by sn: "+sn).toString();
		}
		//相机必须是联机的
		if(!cameraCfg.isCmrOnline()){
			return new JsonResult(false, "Camera not online. sn: "+sn).toString();
		}
		
		if(blStart){
			//启动直播流
			//根据当前的ip和端口组织 rtsp url
			String rtsp = "rtsp://"+cameraCfg.getIp()+":554/h264ESVideoTest";
			//调用ffmeg启动直播流
			String rtmp = RtmpManager.getInstance().runRTSP(sn, rtsp);
			if(rtmp == null){
				return new JsonResult(false, "Camera not establish livestram from: "+rtsp).toString();
			}
			
			//直播成功后，获得最新的一张图片，作为初始化的图片
			JsonResult jsonResult = new JsonResult(true);
			jsonResult.put("sn", sn);
			jsonResult.put("rtmpurl", rtmp);
			
			CameraData data = cameraDataRepository.getLatestPic(sn);
			if(data == null){
				return jsonResult.toString();
			}
			
			String picOri = data.getPicUrl();
			//原始图片
			jsonResult.put("pic", picOri);
			
			
			return jsonResult.toString();
		}else {
			//关闭直播流
			RtmpManager.getInstance().stop(sn);
			return new JsonResult(true).toString();
		}
		
		
	}
}
