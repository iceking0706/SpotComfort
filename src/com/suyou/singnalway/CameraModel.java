package com.suyou.singnalway;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * 普通连接
 * 
 * 根据某个ip地址来控制摄像机的
 * @author IcekingT420
 *
 */
public class CameraModel {
	/**
	 * 摄像机的ip地址
	 */
	private String ip;
	
	/**
	 * 连接成功之后获得的句柄
	 */
	private Pointer m_hHv;
	
	/**
	 * 获得in out数据的回调函数
	 */
	private HvDeviceDLL.GetStringCallBack inoutCallBack;
	
	/**
	 * 判断in out回调函数是否成功添加了
	 */
	private boolean inoutCallBackSet;
	
	/**
	 * 抓取图片的回调函数
	 */
	private HvDeviceDLL.GetMJPEGCallBack jpgImageCallBack;
	
	/**
	 * 接收到的in out 数据，最多放60个
	 * 1分钟来一个的
	 */
	private List<RecvInOutData> inoutList;
	
	/**
	 * 获得到的一张图片
	 * 图片每次获得一张
	 */
	private RecvImageData imageData;
	
	/**
	 * 记录下操作的错误信息
	 */
	private String errorStr;
	
	/**
	 * 每次启动读取历史io数据时，保存一下获得到的字符型数据
	 * 已经通过了处理的, 分号变逗号
	 */
	private String ioRawHisDataStr;
	
	public CameraModel(String ip){
		this.ip = ip;
	}
	
	/**
	 * 判断是否已经连接了
	 * @return
	 */
	public boolean isConnected(){
		return m_hHv != null;
	}
	
	/**
	 * 连接摄像头
	 * @return
	 */
	public boolean connect(){
		if(m_hHv != null)
			return true;
		try {
			m_hHv = HvDeviceDLL.getInstance().HVAPI_OpenEx(ip, null);
			return m_hHv!=null;
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = e.getMessage();
			m_hHv = null;
			return false;
		}
	}
	
	/**
	 * 断开连接
	 * @return
	 */
	public boolean disConnect(){
		if(m_hHv == null)
			return true;
		try {
			int result = HvDeviceDLL.getInstance().HVAPI_CloseEx(m_hHv);
			if(result == HvDeviceDLL.S_OK){
				m_hHv = null;
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = e.getMessage();
			return false;
		}
	}
	
	/**
	 * 开始接收in和out数据，获得到的会放到列表中去
	 * 这里就是添加回调函数
	 * @return
	 */
	public boolean startReceiveInOutData(){
		if(m_hHv == null){
			errorStr = "Camera not connected";
			return false;
		}
		if(inoutCallBackSet)
			return true;
		try {
			inoutCallBackSet = false;
			if(inoutList == null){
				inoutList = Collections.synchronizedList(new ArrayList<RecvInOutData>());
			}
			if(inoutCallBack == null){
				inoutCallBack = new HvDeviceDLL.GetStringCallBack() {
					
					@Override
					public int invoke(Pointer pUserData, String pString, int dwStrLen) {
						RecvInOutData data = new RecvInOutData(pString);
						inoutList.add(data);
						if(inoutList.size() > 60)
							inoutList.remove(0);
						return 1;
					}
				};
			}
			//设置回调
			int ret = HvDeviceDLL.getInstance().HVAPI_SetCallBackEx(m_hHv, inoutCallBack, null, 0, HvDeviceDLL.CALLBACK_TYPE_STRING, null);
			if(ret != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_SetCallBackEx fail.";
				return false;
			}else{
				inoutCallBackSet = true;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = e.getMessage();
			inoutCallBackSet = false;
			return false;
		}
	}
	
	/**
	 * 当前时间设置到相机去
	 * 当前时间设置到相机去
	 * @return
	 */
	public boolean setCameraTime(){
		if(m_hHv == null){
			errorStr = "Camera not connected";
			return false;
		}
		try {
			Calendar calendar = Calendar.getInstance();
			int result = HvDeviceDLL.getInstance().HVAPI_SetTime(m_hHv, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 0);
			return result==HvDeviceDLL.S_OK;
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = e.getMessage();
			m_hHv = null;
			return false;
		}
	}
	
	/**
	 * 获得一个时间段内的in out 数据
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<RecvInOutData> getInOutFlow(long startTime,long endTime){
		if(m_hHv == null){
			errorStr = "Camera not connected";
			return null;
		}
		//计算开始和结束之间几分钟
		int subMinute = (int)((endTime-startTime)/60000)+1;
		if(subMinute <= 0){
			errorStr = "Time is not valid when get in out flow data";
			return null;
		}
		try {
			//每次启动之后，设置原始历史数据为null
			ioRawHisDataStr = null;
			//每分钟的大小是64
			byte[] szRetInfo = new byte[32*subMinute];
			IntByReference iLen = new IntByReference(szRetInfo.length);
			int ret = HvDeviceDLL.getInstance().HVAPI_GetPCSFlow(m_hHv, startTime, endTime, szRetInfo, iLen);
			if(ret != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_GetPCSFlow fail.";
				return null;
			}else{
				if(iLen.getValue() <= 0){
					errorStr = "iLen.value=0";
					return null;
				}
				String pcsFlowInfo = new String(HvDeviceDLL.getDataByLen(szRetInfo,iLen.getValue()));
				errorStr = "iLen.value="+iLen.getValue()+", pcsFlowInfo="+pcsFlowInfo;
				//将原始数据中的in后面的；号改成逗号
				ioRawHisDataStr = RecvInOutData.parse111(pcsFlowInfo);
				return RecvInOutData.parsePCSFlowInfo(ioRawHisDataStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = e.getMessage();
			return null;
		}
	}
	
	/**
	 * 得到一个小时之内的in、out数据对象
	 * @return
	 */
	public List<RecvInOutData> getOneHourInOutFlowData(){
		long end = System.currentTimeMillis();
		return getInOutFlow(end - 3600000, end);
	}
	
	/**
	 * 从流水数据中获得最新的
	 * @return
	 */
	public RecvInOutData getLatestInOutFlowData(){
		List<RecvInOutData> list = getOneHourInOutFlowData();
		if(list == null || list.isEmpty())
			return null;
		return list.get(list.size()-1);
	}
	
	/**
	 * 拍照，并返回照片对象
	 * @return
	 */
	public RecvImageData takeAPicture(){
		if(m_hHv == null){
			errorStr = "Camera not connected";
			return null;
		}
		try {
			if(jpgImageCallBack == null){
				jpgImageCallBack = new HvDeviceDLL.GetMJPEGCallBack() {
					
					@Override
					public int invoke(Pointer pUserData, int dwImageFlag, int dwImageType,
							int dwWidth, int dwHeight, long dw64TimeMS, Pointer pbImageData,
							int dwImageDataLen, String szImageExtInfo) {
						if(imageData != null)
							return 2;
						imageData = new RecvImageData();
						imageData.setWidth(dwWidth);
						imageData.setHeight(dwHeight);
						imageData.setTimeMs(dw64TimeMS);
						imageData.setLen(dwImageDataLen);
						if(pbImageData != null){
							byte[] dataArray = pbImageData.getByteArray(0, dwImageDataLen);
							if(dataArray != null && dataArray.length>0){
								imageData.setData(dataArray);
							}
						}
						if(!imageData.isValid()){
							imageData = null;
							return 3;
						}
						return 1;
					}
				};
			}
			//添加图像接口
			int ret = HvDeviceDLL.getInstance().HVAPI_StartRecvMJPEG(m_hHv,jpgImageCallBack,null, 0, HvDeviceDLL.MJPEG_RECV_FLAG_DEBUG);
			if(ret != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_StartRecvMJPEG fail.";
				return null;
			}else{
				//图像回调设置成功
				//照片对象清空
				imageData = null;
				// 激活调试码流，必须有此步骤
				HvDeviceDLL.getInstance().HVAPI_SetDebugJpegStatus(m_hHv, true);
				//等待直到有数据，等待50秒
				int waitCount = 0;
				while(imageData == null && waitCount<1000){
					Thread.sleep(50);
					waitCount++;
				}
				//停止接收图片
				HvDeviceDLL.getInstance().HVAPI_StopRecvMJPEG(m_hHv);
				return imageData;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = e.getMessage();
			return null;
		}
	}
	
	/**
	 * 获取最新一次的in out数据
	 * @return
	 */
	public RecvInOutData getLastestInOut(){
		if(inoutList==null || inoutList.isEmpty())
			return null;
		return inoutList.get(inoutList.size()-1);
	}
	
	/**
	 * 获得最新一次的in out数据，设置等待时候，
	 * 如果>0，则循环等待，直到超时
	 * @param waitTime
	 * @return
	 */
	public RecvInOutData getLastestInOut(long waitTime){
		if(waitTime<=0)
			return getLastestInOut();
		long startTime = System.currentTimeMillis();
		RecvInOutData data = null;
		while(true){
			data = getLastestInOut();
			if(data != null)
				break;
			//没有数据的话，每隔5秒去获取一次
			HvDeviceDLL.sleepIt(5000);
			if(System.currentTimeMillis()-startTime > waitTime)
				break;
		}
		return data;
	}

	public List<RecvInOutData> getInoutList() {
		return inoutList;
	}

	public RecvImageData getImageData() {
		return imageData;
	}

	public String getErrorStr() {
		return errorStr;
	}

	public String getIp() {
		return ip;
	}

	public String getIoRawHisDataStr() {
		return ioRawHisDataStr;
	}
	
	
}
