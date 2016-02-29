package com.suyou.singnalway;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ssin.util.MyStringUtil;

import com.sun.jna.ptr.IntByReference;

/**
 * 主动连接模式，
 * 
 * 获得连接上来的SN和ip的对应关系
 * @author IcekingT420
 *
 */
public class AutoLinkModel {
	/**
	 * 维系设备sn和ip地址的map
	 * 仅仅表示online的设备
	 * 每次解析设备的时候去清除
	 */
	private ConcurrentMap<String, SnIpPort> snIpMap = new ConcurrentHashMap<String, SnIpPort>();
	
	/**
	 * 由外部设定的接口
	 */
	private SnIpChangeListener snIpChangeListener;
	
	/**
	 * 监听是否启动了
	 */
	private boolean started = false;
	
	/**
	 * 监听的端口号，默认就是6665了
	 */
	private int serverPort = 6665;
	
	/**
	 * 记录下操作的错误信息
	 */
	private String errorStr;
	
	/**
	 * 更新sn和ip对应关系的间隔时间，默认60秒
	 */
	private long updateInterval = 60000;
	
	/**
	 * 周期性的去获取设备ip信息的计时器
	 */
	private Timer timer0;
	
	public AutoLinkModel(){
	}
	
	private void debug(String str){
		System.out.println("AutoLinkModel.debug->"+str);
	}
	
	/**
	 * 启动监听
	 * @return
	 */
	public boolean startListen(){
		if(started)
			return true;
		try {
			debug("start to listen camera auto link...Port="+serverPort+", interval="+updateInterval);
			//加载监听模块
			if(HvDeviceDLL.getInstance().HVAPI_LoadMonitor(100, null) != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_LoadMonitor fail.";
				debug(errorStr);
				return false;
			}
			debug("Load Monitor success.");
			//打开监听服务，默认端口为6665
			if(HvDeviceDLL.getInstance().HVAPI_OpenServer(serverPort, HvDeviceDLL.LISTEN_TYPE_RECORD,null) != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_OpenServer fail.";
				debug(errorStr);
				return false;
			}
			debug("Open server success.");
			started = true;
			snIpMap.clear();
			timer0 = new Timer();
			debug("Update device info Timer start working...");
			//启动计时器轮询，第一次30秒之后开始
			timer0.schedule(new TimerTask() {
				
				@Override
				public void run() {
					updateSNIP();
				}
			}, 60000, updateInterval);
			return true;
		} catch (Exception e) {
			started = false;
			e.printStackTrace();
			errorStr = "startListen error: "+e.getMessage();
			debug(errorStr);
			return false;
		}
	}
	
	/**
	 * 计时器中的轮询获得sn和ip对应的方法
	 */
	public void updateSNIP(){
		//每次更新设备信息的时候，清除原来的
		snIpMap.clear();
		
		if(!started)
			return;
		try {
			//目前连接上来的设备列表长度
			IntByReference nDevListLen = new IntByReference(0);
			if(HvDeviceDLL.getInstance().HVAPI_GetDeviceListSize(nDevListLen, null) != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_GetDeviceListSize fail.";
				debug(errorStr);
				return;
			}
			if(nDevListLen.getValue() <= 1){
				errorStr = "No device online. nDevListLen="+nDevListLen.getValue();
				debug(errorStr);
				return;
			}
			//获得设备列表信息，其中的SN以；隔开
			byte[] szDevList = new byte[nDevListLen.getValue()];
			if(HvDeviceDLL.getInstance().HVAPI_GetDeviceList(szDevList, nDevListLen.getValue(), null) != HvDeviceDLL.S_OK){
				errorStr = "HVAPI_GetDeviceList fail.";
				debug(errorStr);
				return;
			}
			String devSNListInfo = HvDeviceDLL.getValidString(szDevList);
			if(devSNListInfo.equals("")){
				errorStr = "getValidString fail.";
				debug(errorStr);
				return;
			}
			debug("updateSNIP->nDevListLen="+nDevListLen.getValue()+", info="+devSNListInfo);
			//得到当前连接的设备sn数组
			String[] snArray = MyStringUtil.getArrayFromStrByChar(devSNListInfo, ";");
			if(snArray == null || snArray.length == 0){
				errorStr = "can not get sn array from info: "+devSNListInfo;
				debug(errorStr);
				return;
			}
			
			//获得每个sn的当前ip信息，并保存到map中
			for(String sn: snArray){
				try {
					byte[] strIP = new byte[256];
					IntByReference dwPORT = new IntByReference(0);
					//调用根据sn获得ip的函数
					if(HvDeviceDLL.getInstance().HVAPI_GetDeviceInfoAutoLink(sn, null, strIP, 64, dwPORT) != HvDeviceDLL.S_OK){
						debug("HVAPI_GetDeviceInfoAutoLink fail. sn="+sn);
						continue;
					}
					String curIp = HvDeviceDLL.getValidString(strIP);
					if(curIp.equals("")){
						debug("Cur IP is not valid. sn="+sn);
						continue;
					}
					//到map中查看是否有原来的
					SnIpPort sipObj = new SnIpPort();
					sipObj.setSn(sn);
					sipObj.setIp(curIp);
					sipObj.setPort(dwPORT.getValue());
					sipObj.setTime(System.currentTimeMillis());
					snIpMap.put(sn, sipObj);
					debug("Update linkInfo. ("+sipObj.toString()+")");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			//更新设备的循环结束以后，调用外部的接口，可以更新信息
			if(snIpChangeListener != null){
				snIpChangeListener.onSnIpChange(snIpMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = "updateSNIP error: "+e.getMessage();
			debug(errorStr);
		}
	}
	
	/**
	 * 调试用的设备列表信息打印
	 */
	public void showDebugMapInfo(){
		int size = snIpMap.size();
		debug("snIPMap size is "+size);
		if(size == 0)
			return;
		Iterator<String> iterator = snIpMap.keySet().iterator();
		int no = 0;
		while(iterator.hasNext()){
			SnIpPort sipObj = snIpMap.get(iterator.next());
			if(sipObj == null)
				continue;
			debug("Device"+(++no)+": "+sipObj.toString());
		}
	}
	
	/**
	 * 关闭监听
	 * @return
	 */
	public boolean stopListen(){
		if(!started)
			return true;
		try {
			HvDeviceDLL.getInstance().HVAPI_CloseServer(HvDeviceDLL.LISTEN_TYPE_RECORD);
			debug("Close server success.");
			HvDeviceDLL.getInstance().HVAPI_UnLoadMonitor();
			debug("Unload Monitor success.");
			if(timer0 != null){
				timer0.cancel();
				timer0 = null;
			}
			debug("Listen stopped.");
			started = false;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			errorStr = "stopListen error: "+e.getMessage();
			return false;
		}
	}
	
	/**
	 * 重启监听
	 */
	public void resetListen(){
		//先关闭，再打开
		if(!stopListen())
			return;
		if(!startListen())
			return;
	}
	
	/**
	 * 判断时候有设备在线的
	 * @return
	 */
	public boolean hasCameraOnline(){
		return snIpMap.size()>0;
	}
	
	/**
	 * 通过sn得到设备的ip信息对象
	 * @param sn
	 * @return
	 */
	public SnIpPort getBySN(String sn){
		return snIpMap.get(sn);
	}
	
	/**
	 * 通过sn直接得到ip地址
	 * @param sn
	 * @return
	 */
	public String getIpBySN(String sn){
		SnIpPort obj = getBySN(sn);
		return obj!=null?obj.getIp():null;
	}

	public long getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public ConcurrentMap<String, SnIpPort> getSnIpMap() {
		return snIpMap;
	}

	public boolean isStarted() {
		return started;
	}

	public String getErrorStr() {
		return errorStr;
	}

	public void setSnIpChangeListener(SnIpChangeListener snIpChangeListener) {
		this.snIpChangeListener = snIpChangeListener;
	}
}
