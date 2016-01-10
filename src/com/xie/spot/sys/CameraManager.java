package com.xie.spot.sys;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ssin.util.DateProcess;

import com.eg.intf3.security.ByteUtils;
import com.suyou.singnalway.AutoLinkModel;
import com.suyou.singnalway.CameraModel;
import com.suyou.singnalway.RecvImageData;
import com.suyou.singnalway.RecvInOutData;
import com.suyou.singnalway.SnIpChangeListener;
import com.suyou.singnalway.SnIpPort;
import com.xie.spot.entity.CameraAlert;
import com.xie.spot.entity.CameraCfg;
import com.xie.spot.entity.CameraData;
import com.xie.spot.service.CameraOperService;
import com.xie.spot.sys.utils.JsonResult;

/**
 * 管理设备的
 * @author IcekingT420
 *
 */
public class CameraManager {
	private static final Logger logger = LoggerFactory.getLogger(CameraManager.class);
	
	/**
	 * 获取设备数据的线程池大小
	 */
	private int poolSize = 10;
	
	/**
	 * 线程池管理，长度指定的
	 */
	private ExecutorService executorService;
	
	/**
	 * 内部计时器的间隔
	 */
	private long timerInterval = 60000;
	
	/**
	 * 重启监听器的时间，一个小时
	 */
	private long resetListenerInterval = 1800000;
	
	/**
	 * 去获取设备数据的时间
	 */
	private long fetchDataInterval = 600000;
	
	/**
	 * 去更新sn和ip关系的时间
	 */
	private long updateSnIpInterval = 300000;
	
	/**
	 * 拍照，如果时间没有到，则采取上一次拍照的图片
	 */
	private long takeAPictureInterval = 3600000;
	
	/**
	 * 目前就考虑一个计时器来完成事情
	 */
	private Timer timer0;
	
	/**
	 * 记录上一次去获取数据的时间
	 */
	private long preFetchDataTime = 0;
	
	/**
	 * 记录上一次重启监听器的时间点
	 */
	private long preResetListenerTime = System.currentTimeMillis();
	
	/**
	 * 主动连接模式的监听器
	 */
	private AutoLinkModel autoLinkModel;
	
	@Autowired
	private CameraOperService cameraOperService;
	
	/**
	 * 正在下发的设备，记录，避免多线程对同一个进行下发
	 * key: sn
	 * value: ip
	 */
	private ConcurrentMap<String, String> downDvsMap;
	
	/**
	 * 是否去相机获取数据，按照相机自己的参数设置
	 */
	private boolean useCameraInnerDef = true;
	
	/**
	 * 将未处理的报警记录发送邮件的间隔，默认5分钟
	 */
	private long sendMailInterval = 300000;
	
	/**
	 * 每个邮件中最多的报警数量，默认50
	 */
	private int maxAlertInOneMail = 30;
	
	private long preSendMailTime = System.currentTimeMillis();
	
	/**
	 * 启动
	 */
	public void start(){
		logger.info("CameraManager start...");
		autoLinkModel = new AutoLinkModel();
		autoLinkModel.setUpdateInterval(updateSnIpInterval);
		autoLinkModel.setSnIpChangeListener(new SnIpChangeListener() {
			
			@Override
			public void onSnIpChange(ConcurrentMap<String, SnIpPort> snIpMap) {
				cameraOperService.theSnIpChange(snIpMap);
			}
		});
		autoLinkModel.startListen();
		
		//线程池初始化
		executorService = Executors.newFixedThreadPool(poolSize);
		
		downDvsMap = new ConcurrentHashMap<String, String>();
		
		//启动计时器任务
		timer0 = new Timer();
		timer0.schedule(new TimerTask() {
			
			@Override
			public void run() {
				theTimerAction();
			}
		}, 60000, timerInterval);
	}
	
	
	/**
	 * 计时器的任务
	 * 1、定时重启监听
	 * 2、定时去找设备数据
	 */
	public void theTimerAction(){
		try {
			long nowTime = System.currentTimeMillis();
			
			//任务1，定时重启设备
			if(nowTime-preResetListenerTime > resetListenerInterval){
				//需要重启设备了
				//autoLinkModel.resetListen();
				logger.info("reset listener......");
				preResetListenerTime = nowTime;
			}
			
			//任务3，针对那些未发送过邮件通知的报警发送通知
			if(nowTime-preSendMailTime > sendMailInterval){
				//需要查询报警记录，并发送邮件了
				cameraOperService.checkCmrAlertAndSendMail(maxAlertInOneMail);
				preSendMailTime = nowTime;
			}
			
			//任务2，联机相机去获取数据
			if(useCameraInnerDef){
				//找出全部有效的摄像机，
				List<CameraCfg> cmrList = cameraOperService.findInUseCmrs();
				if(Utils.isEmpty(cmrList))
					return;
				//去找这些设备的数据，多线程分别去找
				for(CameraCfg po: cmrList){
					//需要根据相机是否联机来进行区分操作
					if(po.isCmrOnline()){
						//联机的，进行数据获取
						if(isSNDowning(po.getSn()))
							continue;
						if(!po.inValidHours())
							continue;
						final CameraCfg fpo = po;
						executorService.execute(new Runnable() {
							public void run() {
								processFetchOneCameraIOData(fpo,false);
							}
						});
					}else{
						//脱机的，进行报警判断
						long ndtoa = po.needTimeoutOfflineAlert();
						if(ndtoa == 0l)
							continue;
						//判断同类型的报警（长时间脱机报警）是否已经存在了，存在就不要继续了
						if(cameraOperService.meHasUnProcessedCmrAlert(po.getSn(), 1))
							continue;
						//创建新的报警（长时间脱机报警）
						CameraAlert alert = new CameraAlert();
						alert.setSn(po.getSn());
						alert.setMark("脱机时长: "+DateProcess.timeShow(ndtoa));
						cameraOperService.saveCmrAlert(alert);
						logger.info("New CameraAlert. "+alert.forLogStr());
					}
				}
			}else{
				//任务2，查询联机设备的数据，多线程去找
				if(nowTime-preFetchDataTime > fetchDataInterval){
					//找出全部联机的摄像机，
					List<CameraCfg> cmrList = cameraOperService.findOnlineCmrs();
					if(Utils.isEmpty(cmrList))
						return;
					//去找这些设备的数据，多线程分别去找
					for(CameraCfg po: cmrList){
						if(isSNDowning(po.getSn()))
							continue;
						if(!po.inValidHours())
							continue;
						final CameraCfg fpo = po;
						executorService.execute(new Runnable() {
							public void run() {
								processFetchOneCameraIOData(fpo,false);
							}
						});
					}
					
					preFetchDataTime = nowTime;
				}
			}
			
			
		} catch (Exception e) {
			logger.error("theTimerAction error: "+e.getMessage(), e);
		}
	}
	
	/**
	 * 手动去触发检测相机连线的操作
	 */
	public void updateCameraOnlineManually(){
		executorService.execute(new Runnable() {
			public void run() {
				autoLinkModel.updateSNIP();
			}
		});
	}
	
	/**
	 * 手动触发的去重启监听
	 */
	public void resetCameraListenerManually(){
		executorService.execute(new Runnable() {
			public void run() {
				autoLinkModel.resetListen();
			}
		});
	}
	
	/**
	 * 手工去获取相机的历史数据，主要用于数据的填补
	 * @param cmr
	 * @param start
	 * @param end
	 * @return
	 */
	public List<RecvInOutData> fetchCameraHisInOutDatasManually(final CameraCfg cmr,long start,long end){
		if(isSNDowning(cmr.getSn())){
			return null;
		}
		try {
			String curSN = cmr.getSn();
			String curIP = cmr.getIp();
			//加入下载map，表示正在下载了
			setSnDown(curSN, curIP);
			//根据ip地址建立普通连接
			CameraModel model = new CameraModel(curIP);
			//连接摄像头
			if(!model.connect()){
				logger.error("Camera connect fail. SN="+curSN+", IP="+curIP);
				setSnNotDown(curSN);
				//设置该摄像机断线的
				cameraOperService.offlineOneCmr(cmr);
				return null;
			}
			//获取历史数据
			List<RecvInOutData> list = model.getInOutFlow(start, end);
			
			setSnNotDown(curSN);
			if(!model.disConnect()){
				logger.error("Camera disConnect fail. SN="+curSN+", IP="+curIP);
			}
			
			return list;
		} catch (Exception e) {
			logger.error("fetchCameraHisInOutDatasManually error: "+e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 给蒋涛用的接口，触发相机拍照，非线程
	 * 返回的是一个json
	 * @param cmr
	 * @return
	 */
	public String takeAPictureManually(final CameraCfg cmr){
		if(isSNDowning(cmr.getSn())){
			return new JsonResult(false, "Camera operation is in processing...SN="+cmr.getSn()).toString();
		}
		try {
			String curSN = cmr.getSn();
			String curIP = cmr.getIp();
			//加入下载map，表示正在下载了
			setSnDown(curSN, curIP);
			//根据ip地址建立普通连接
			CameraModel model = new CameraModel(curIP);
			//连接摄像头
			if(!model.connect()){
				logger.error("Camera connect fail. SN="+curSN+", IP="+curIP);
				setSnNotDown(curSN);
				//设置该摄像机断线的
				cameraOperService.offlineOneCmr(cmr);
				return new JsonResult(false, "Camera connect fail. SN="+curSN+", IP="+curIP).toString();
			}
			//触发拍照
			RecvImageData imageData = model.takeAPicture();
			if(imageData == null || !imageData.isValid()){
				logger.error("Camera get image data fail. Error="+model.getErrorStr()+", SN="+curSN+", IP="+curIP);
				setSnNotDown(curSN);
				model.disConnect();
				return new JsonResult(false, "Camera connect fail. SN="+curSN+", IP="+curIP).toString();
			}
			//保存照片到硬盘，QTP开头的表示快速拍照的照片
			long curTime = System.currentTimeMillis();
			String saveOk = imageData.saveTo(Utils.getCameraPicsDir(),"QTP_"+cmr.getId()+"_"+curTime+"_"+imageData.getLen()+"_"+imageData.getWidth()+"_"+imageData.getHeight()+".jpg");
			if(!saveOk.equals("ok")){
				logger.error("Camera pictures save to file fail: "+saveOk);
				setSnNotDown(curSN);
				model.disConnect();
				return new JsonResult(false, "Camera pictures save to file fail: "+saveOk).toString();
			}
			String pictureUrl = "uploadfiles/cameraPics/"+imageData.getJpgFile().getName();
			
			//根据图片的时间设置时钟
			//如果当前图片的时间（可以认为是相机里面的时钟），与服务器时间相差超过了10分钟，则需要调整了
			long subTM = Math.abs(curTime-imageData.getTimeMs());
			if(subTM >= 600000l){
				boolean timeresult = model.setCameraTime();
				//调整时间，并产生报警记录，该报警自动处理
				CameraAlert alert = new CameraAlert();
				alert.setSn(curSN);
				alert.setType(2);
				alert.setMark("时钟误差: "+DateProcess.timeShow(subTM));
				alert.setPrsTime(curTime);
				alert.setPrsMark("系统自动调整: "+(timeresult?"成功":"失败"));
				cameraOperService.saveCmrAlert(alert);
			}
			
			setSnNotDown(curSN);
			if(!model.disConnect()){
				logger.error("Camera disConnect fail. SN="+curSN+", IP="+curIP);
			}
			//成功，包含4个信息
			JsonResult jsonResult = new JsonResult(true);
			jsonResult.put("picUrl", pictureUrl);
			jsonResult.put("sn", curSN);
			jsonResult.put("ip", curIP);
			jsonResult.put("time", imageData.getTimeShow());
			return jsonResult.toString();
		} catch (Exception e) {
			logger.error("takeAPictureManually error: "+e.getMessage(), e);
			return new JsonResult(false, "takeAPictureManually error: "+e.getMessage()).toString();
		}
	}
	
	/**
	 * 手动方式触发，也是线程去跑
	 * @param cmr
	 */
	public boolean fetchDataImgManually(final CameraCfg cmr){
		if(!cmr.isCmrOnline()){
			logger.info("Camera is not online...SN="+cmr.getSn());
			return false;
		}
		if(isSNDowning(cmr.getSn())){
			logger.info("Fetch camera operation is processing...SN="+cmr.getSn());
			return false;
		}
		if(!cmr.inValidHours()){
			logger.info("Not in camera valid fetch data time...SN="+cmr.getSn()+", Hours: "+cmr.getTkpHourSt()+" ~ "+cmr.getTkpHourEd());
			return false;
		}
		executorService.execute(new Runnable() {
			public void run() {
				processFetchOneCameraIOData(cmr,true);
			}
		});
		return true;
	}
	
	/**
	 * 设置某个相机的时间
	 * @param cmr
	 * @return
	 */
	private void setCameraTimeInner(CameraCfg cmr){
		String curSN = cmr.getSn();
		String curIP = cmr.getIp();
		try {
			//加入下载map，表示正在下载了
			setSnDown(curSN, curIP);
			//根据ip地址建立普通连接
			CameraModel model = new CameraModel(curIP);
			//连接摄像头
			if(!model.connect()){
				logger.error("Camera connect fail. SN="+curSN+", IP="+curIP);
				setSnNotDown(curSN);
				//设置该摄像机断线的
				cameraOperService.offlineOneCmr(cmr);
				return;
			}
			
			//设置时间
			if(model.setCameraTime()){
				logger.info("Camera time set success. SN="+curSN+", IP="+curIP);
			}else{
				logger.error("Camera time set fail. SN="+curSN+", IP="+curIP);
			}
			
			setSnNotDown(curSN);
			
			//关闭连接
			if(!model.disConnect()){
				logger.error("Camera disConnect fail. SN="+curSN+", IP="+curIP);
			}
		} catch (Exception e) {
			logger.error("setCameraTimeInner error: "+e.getMessage());
			setSnNotDown(curSN);
		}
	}
	
	/**
	 * 手工发起的设置相机时间
	 * @param cmr
	 * @return
	 */
	public boolean setCameraTimeManually(List<CameraCfg> cmrList){
		int doCount = 0;
		for(CameraCfg cmr: cmrList){
			if(isSNDowning(cmr.getSn())){
				continue;
			}
			final CameraCfg fcmr = cmr;
			executorService.execute(new Runnable() {
				public void run() {
					setCameraTimeInner(fcmr);
				}
			});
			doCount++;
		}
		return doCount>0;
	}
	
	/**
	 * 判断某个sn的设备是否正处于下发状态
	 * @param sn
	 * @return
	 */
	private boolean isSNDowning(String sn){
		if(downDvsMap.containsKey(sn))
			return true;
		return false;
	}
	
	/**
	 * 设置某个sn的相机正在处于下载操作中
	 * 抓取数据，设置时间等操作
	 * @param sn
	 * @param ip
	 */
	private void setSnDown(String sn,String ip){
		downDvsMap.put(sn, ip);
	}
	
	private void setSnNotDown(String sn){
		downDvsMap.remove(sn);
	}
	
	/**
	 * 在线程池中处理一个联机的设备
	 * 抓取某个相机的In out 数据
	 * @param cmr
	 */
	@Transactional
	private void processFetchOneCameraIOData(CameraCfg cmr,boolean force){
		String curSN = cmr.getSn();
		String curIP = cmr.getIp();
		long cmrOnedayStartTime = cmr.oneDayFirstRecordTime(null);
		/*if(isSNDowning(curSN)){
			logger.info("Fetch camera operation is processing...SN="+curSN);
			return;
		}*/
		try {
			//查找该sn的最新一条数据记录
			CameraData latestData = cameraOperService.getLatestCameraData(curSN);
			//判断是否需要获取inout和图片
			boolean fetchIOData = true;
			boolean fetchPic = true;
			long nowTime = System.currentTimeMillis();
			if(!force && latestData != null){
				long subIO = nowTime-latestData.longRecvIOTime();
				long subPic = nowTime-latestData.longRecvPicTime();
				
				if(latestData.longRecvIOTime()>0 && subIO < cmr.getInoutInterval().longValue()*60000){
					fetchIOData = false;
				}
				if(latestData.longRecvPicTime()>0 && subPic < cmr.getTkpInterval().longValue()*60000){
					fetchPic = false;
				}
				
				//如果拍照的间隔时间小于数据的间隔时间，那么以数据为准
				//数据肯定要拿，图片不一定要获取
				if(cmr.getTkpInterval().intValue() <= cmr.getInoutInterval().intValue())
					fetchPic = fetchIOData;
				
				//数据和图片都不需要获取的话，那么就直接返回
//				if(!fetchIOData && !fetchPic)
//					return;
				
				//拿数据为主，如果数据都不拿的话，那么图片就不要拿了，否则会出错的
				if(!fetchIOData)
					return;
			}
			logger.info("Start to fetch Camera data.fetchIOData="+fetchIOData+", fetchPic="+fetchPic+", SN="+curSN+", IP="+curIP);
			//加入下载map，表示正在下载了
			setSnDown(curSN, curIP);
			//根据ip地址建立普通连接
			CameraModel model = new CameraModel(curIP);
			//连接摄像头
			if(!model.connect()){
				logger.error("Camera connect fail. SN="+curSN+", IP="+curIP);
				setSnNotDown(curSN);
				//设置该摄像机断线的
				cameraOperService.offlineOneCmr(cmr);
				return;
			}
			//先保存上一次的数据
			int tmpInData = latestData!=null?latestData.getDin():0;
			int tmpOutData = latestData!=null?latestData.getDout():0;
			long llRealIOTms = latestData!=null?latestData.longRecvIOTime():0l;
			//保存本次得到的历史数据字符串，16进制形式
			String tmpIORawData = null;
			if(fetchIOData){
				/*
				//获取in、out数据，等待，以回调函数形式去获取
				if(!model.startReceiveInOutData()){
					logger.error("Camera startReceiveInOutData fail. Error="+model.getErrorStr()+", SN="+curSN+", IP="+curIP);
					downDvsMap.remove(curSN);
					model.disConnect();
					return;
				}
				//等待得到in out数据，最多2分钟
				RecvInOutData inOutData = model.getLastestInOut(120000);
				if(inOutData == null){
					logger.error("Camera get in out data fail. SN="+curSN+", IP="+curIP);
					downDvsMap.remove(curSN);
					model.disConnect();
					return;
				}*/
				
				//改成使用了sd卡后，获取从现在开始到最近一条的全部记录，并进行相加，得到最终的一条
				long llstartTime = llRealIOTms>0l?llRealIOTms:nowTime-600000;
				//如果刚好这一次是本天的第一次，那么从本天开始之前的那些就不要去获取了
				if(llstartTime < cmrOnedayStartTime){
					llstartTime = cmrOnedayStartTime;
				}
				
				//避免重复，比startTime多一分钟的
				llstartTime = llstartTime+60000;
				logger.info("Get In out Flow Data. Start="+DateProcess.toString(new Date(llstartTime), "yyyy-MM-dd HH:mm")+", End="+DateProcess.toString(new Date(nowTime), "yyyy-MM-dd HH:mm"));
				List<RecvInOutData> inoutflowList = model.getInOutFlow(llstartTime, nowTime);
				if(Utils.isEmpty(inoutflowList)){
					logger.error("Camera get in out flow data fail. SN="+curSN+", IP="+curIP+", errorStr="+model.getErrorStr());
					setSnNotDown(curSN);
					model.disConnect();
					return;
				}
				//这一批数据需要汇总成一个数据，时间就用最新的一次的
				RecvInOutData inOutData = mergeToOne(inoutflowList);
				
				tmpInData = inOutData.getInData();
				tmpOutData = inOutData.getOutData();
				llRealIOTms = inOutData.getTimeMs();
				if(!Utils.isEmpty(model.getIoRawHisDataStr()))
					tmpIORawData = ByteUtils.byteArrayToHex(model.getIoRawHisDataStr().getBytes());
			}
			
			//拍照并保存，看看是否达到了需要拍照的时间
			//判断是否需要拍新的照片
			String pictureUrl = latestData!=null?latestData.getPicUrl():"";
			long llRealPicTms = latestData!=null?latestData.longRecvPicTime():0l;
			//图片的原始数据，不保存了，2010-10-2
			//String tmpPicRawData = null;
			//如果，没有最近的图片，或者距离最新的图片已经超过了设定的拍照时间，那么就表示需要拍照了
			if(fetchPic){
				RecvImageData imageData = model.takeAPicture();
				if(imageData == null || !imageData.isValid()){
					logger.error("Camera get image data fail. Error="+model.getErrorStr()+", SN="+curSN+", IP="+curIP);
					setSnNotDown(curSN);
					model.disConnect();
					return;
				}
				long curTime = System.currentTimeMillis();
				String saveOk = imageData.saveTo(Utils.getCameraPicsDir(),cmr.getId()+"_"+curTime+"_"+imageData.getLen()+"_"+imageData.getWidth()+"_"+imageData.getHeight()+".jpg");
				if(!saveOk.equals("ok")){
					logger.error("Camera pictures save to file fail: "+saveOk);
					setSnNotDown(curSN);
					model.disConnect();
					return;
				}
				pictureUrl = "uploadfiles/cameraPics/"+imageData.getJpgFile().getName();
				llRealPicTms = curTime;
				//tmpPicRawData = ByteUtils.byteArrayToHex(imageData.getData());
				
				//如果当前图片的时间（可以认为是相机里面的时钟），与服务器时间相差超过了10分钟，则需要调整了
				long subTM = Math.abs(nowTime-imageData.getTimeMs());
				if(subTM >= 600000l){
					boolean timeresult = model.setCameraTime();
					//调整时间，并产生报警记录，该报警自动处理
					CameraAlert alert = new CameraAlert();
					alert.setSn(curSN);
					alert.setType(2);
					alert.setMark("时钟误差: "+DateProcess.timeShow(subTM));
					alert.setPrsTime(nowTime);
					alert.setPrsMark("系统自动调整: "+(timeresult?"成功":"失败"));
					cameraOperService.saveCmrAlert(alert);
				}
				
			}
			
			//开始保存数据库记录
			CameraData cmrData = new CameraData();
			cmrData.setSn(curSN);
			cmrData.setDin(tmpInData);
			cmrData.setDout(tmpOutData);
			cmrData.setPicUrl(pictureUrl);
			cmrData.setRecvIOTime(llRealIOTms);
			cmrData.setRecvPicTime(llRealPicTms);
			//两个lazy的数据也保存起来
			cmrData.setIoRawData(tmpIORawData);
			//cmrData.setPicData(tmpPicRawData);
			
			cameraOperService.saveCmrData(cmrData);
			setSnNotDown(curSN);
			logger.info("Camera data and image save to DB. SN="+curSN+", IP="+curIP);
			
			if(!model.disConnect()){
				logger.error("Camera disConnect fail. SN="+curSN+", IP="+curIP);
			}
			
		} catch (Exception e) {
			logger.error("processOneCamera error: "+e.getMessage(), e);
			setSnNotDown(curSN);
		}
	}
	
	/**
	 * 将
	 * @param inoutflowList
	 * @return
	 */
	public static RecvInOutData mergeToOne(List<RecvInOutData> inoutflowList){
		int sumIn = 0;
		int sumOut = 0;
		for(RecvInOutData data: inoutflowList){
			sumIn += data.getInData();
			sumOut += data.getOutData();
		}
		RecvInOutData inOutData = new RecvInOutData();
		inOutData.setInData(sumIn);
		inOutData.setOutData(sumOut);
		//用最后一条的时间
		inOutData.setTimeMs(inoutflowList.get(inoutflowList.size()-1).getTimeMs());
		return inOutData;
	}
	
	/**
	 * 停止
	 */
	public void stop(){
		logger.info("CameraManager stop.");
		executorService.shutdown();
		timer0.cancel();
		timer0 = null;
		autoLinkModel.stopListen();
	}

	public long getTimerInterval() {
		return timerInterval;
	}

	public void setTimerInterval(long timerInterval) {
		this.timerInterval = timerInterval;
	}

	public long getResetListenerInterval() {
		return resetListenerInterval;
	}

	public void setResetListenerInterval(long resetListenerInterval) {
		this.resetListenerInterval = resetListenerInterval;
	}

	public long getFetchDataInterval() {
		return fetchDataInterval;
	}

	public void setFetchDataInterval(long fetchDataInterval) {
		this.fetchDataInterval = fetchDataInterval;
	}

	public long getUpdateSnIpInterval() {
		return updateSnIpInterval;
	}

	public void setUpdateSnIpInterval(long updateSnIpInterval) {
		this.updateSnIpInterval = updateSnIpInterval;
	}

	public long getPreFetchDataTime() {
		return preFetchDataTime;
	}

	public void setPreFetchDataTime(long preFetchDataTime) {
		this.preFetchDataTime = preFetchDataTime;
	}

	public long getPreResetListenerTime() {
		return preResetListenerTime;
	}

	public void setPreResetListenerTime(long preResetListenerTime) {
		this.preResetListenerTime = preResetListenerTime;
	}
	
	public CameraOperService getCameraOperService() {
		return cameraOperService;
	}
	
	public void setCameraOperService(CameraOperService cameraOperService) {
		this.cameraOperService = cameraOperService;
	}
	
	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}


	public long getTakeAPictureInterval() {
		return takeAPictureInterval;
	}


	public void setTakeAPictureInterval(long takeAPictureInterval) {
		this.takeAPictureInterval = takeAPictureInterval;
	}


	public boolean isUseCameraInnerDef() {
		return useCameraInnerDef;
	}


	public void setUseCameraInnerDef(boolean useCameraInnerDef) {
		this.useCameraInnerDef = useCameraInnerDef;
	}


	public long getSendMailInterval() {
		return sendMailInterval;
	}


	public void setSendMailInterval(long sendMailInterval) {
		this.sendMailInterval = sendMailInterval;
	}


	public long getPreSendMailTime() {
		return preSendMailTime;
	}


	public void setPreSendMailTime(long preSendMailTime) {
		this.preSendMailTime = preSendMailTime;
	}


	public int getMaxAlertInOneMail() {
		return maxAlertInOneMail;
	}


	public void setMaxAlertInOneMail(int maxAlertInOneMail) {
		this.maxAlertInOneMail = maxAlertInOneMail;
	}
	
}
