package com.suyou.singnalway;

import java.util.ArrayList;
import java.util.List;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

/**
 * 通过JNA模拟HvDevice.dll中的若干方法
 * @author IcekingT420
 *
 */
public class HvDeviceDLL {
	/**
	 * dll所在的位置，jre\bin下面
	 */
	public static final String JRE_BIN_DIR = System.getProperty("java.home")
			+ "/bin";
	
	/**
	 * 几个常量的定义
	 */
	public static final int S_OK = 0;
	public static final int E_FAIL = 0x80004005;
	public static final int CALLBACK_TYPE_STRING = 0xFFFF0007;
	public static final int MJPEG_RECV_FLAG_DEBUG = 0xffff0900;
	public static final int MJPEG_RECV_FLAG_REALTIME = 0xffff0901;
	//主动连接的监听类型，默认1
	public static final int LISTEN_TYPE_RECORD = 0x01;
	
	/**
	 * 从一个data数组中获得有效的字符串，从0开始的那些不正确的
	 * @param data
	 * @return
	 */
	public static String getValidString(byte[] data){
		if(data == null || data.length == 0)
			return "";
		List<Byte> list = new ArrayList<Byte>();
		byte byteempty = 0;
		for(int i=0;i<data.length;i++){
			if(data[i] == byteempty)
				break;
			list.add(data[i]);
		}
		if(list.isEmpty())
			return "";
		byte[] array = new byte[list.size()];
		for(int i=0;i<array.length;i++){
			array[i] = list.get(i).byteValue();
		}
		list.clear();
		return new String(array);
	}
	
	/**
	 * 线程休眠
	 * @param mm
	 */
	public static void sleepIt(long mm){
		try {
			Thread.sleep(mm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从一个数组中得到一定长度的
	 * @param array
	 * @param len
	 * @return
	 */
	public static byte[] getDataByLen(byte[] array,int len){
		if(len<=0 || len>array.length)
			return array;
		byte[] data = new byte[len];
		for(int i=0;i<len;i++){
			data[i] = array[i];
		}
		return data;
	}
	
	
	/**
	 * 设备基础信息的结构体定义
	 */
	public static class CDevBasicInfo extends Structure{
		public static class ByReference extends CDevBasicInfo implements Structure.ByReference{}
		public static class ByValue extends CDevBasicInfo implements Structure.ByValue{}
		
		public byte[] szIP = new byte[64];
		public byte[] szMask = new byte[64];
		public byte[] szGateway = new byte[64];
		public byte[] szMac = new byte[128];
		public byte[] szModelVersion = new byte[128];
		public byte[] szSN = new byte[128];
		public byte[] szWorkMode = new byte[128];
		public byte[] szDevType = new byte[128];
		public byte[] szDevVersion = new byte[128];
		public byte[] szMode = new byte[128];
		public byte[] szRemark = new byte[128];
		public byte[] szBackupVersion = new byte[128];
		public byte[] szFPGAVersion = new byte[128];
		public byte[] szKernelVersion = new byte[128];
		public byte[] szUbootVersion = new byte[128];
		public byte[] szUBLVersion = new byte[128];
	}
	
	/**
	 * 字符串的回调函数定义
	 * @author IcekingT420
	 *
	 */
	public static interface GetStringCallBack extends Callback{
		public int invoke(Pointer pUserData,String pString,int dwStrLen);
	}
	
	/**
	 * 图像的回调函数
	 * 
	 */
	public static interface GetMJPEGCallBack extends Callback{
		public int invoke(Pointer pUserData,int dwImageFlag,int dwImageType,int dwWidth,int dwHeight,long dw64TimeMS,Pointer pbImageData,int dwImageDataLen,String szImageExtInfo);
	}
	
	/**
	 * 内部接口，进行模拟
	 * @author IcekingT420
	 *
	 */
	public interface CLib_HvDeviceDLL extends Library{
		CLib_HvDeviceDLL INSTANCE = (CLib_HvDeviceDLL)Native.loadLibrary(JRE_BIN_DIR + "/HvDevice.dll",CLib_HvDeviceDLL.class);
		
		//开始模拟dll中的方法
		
		//普通连接模式
		/**
		 * 搜索局域网内的设备
		 * @param devArray 搜索到的设备信息，放在结构中
		 * @param devCount 本次搜索到的数量
		 * @return
		 */
		public int HVAPI_SearchDeviceEx(CDevBasicInfo[] devArray,IntByReference devCount);
		
		/**
		 * 根据ip地址打开摄像头的连接，并获得连接的句柄指针
		 * @param szIP
		 * @param szApiVer
		 * @return
		 */
		public Pointer HVAPI_OpenEx(String szIP,String szApiVer);
		
		/**
		 * 打开设备后，根据获得的句柄关闭连接
		 * @param m_hHv
		 * @return
		 */
		public int HVAPI_CloseEx(Pointer m_hHv);
		
		/**
		 * 读取in，out数据时候设置回调函数
		 * 启动in，out数据的接收
		 * @param m_hHv
		 * @param callBack
		 * @param pUserData
		 * @param iVideoID
		 * @param iStreamType
		 * @param szConnCmd
		 * @return
		 */
		public int HVAPI_SetCallBackEx(Pointer m_hHv,GetStringCallBack callBack,Pointer pUserData,int iVideoID,int iStreamType,String szConnCmd);
		
		/**
		 * 启动图片数据的接收
		 * @param m_hHv
		 * @param callBack
		 * @param pUserData
		 * @param iVideoID
		 * @param dwRecvFlag
		 * @return
		 */
		public int HVAPI_StartRecvMJPEG(Pointer m_hHv,GetMJPEGCallBack callBack,Pointer pUserData,int iVideoID,int dwRecvFlag);
		
		/**
		 * 激活调试码流
		 * @param m_hHv
		 * @param fEnable
		 * @return
		 */
		public int HVAPI_SetDebugJpegStatus(Pointer m_hHv,boolean fEnable);
		
		/**
		 * 停止图片数据的接收
		 * @param m_hHv
		 * @return
		 */
		public int HVAPI_StopRecvMJPEG(Pointer m_hHv);
		
		/**
		 * 获取设备中的一定时间段内的in和out数据，返回一个字符串
		 * @param m_hHv
		 * @param dw64StartTime
		 * @param dw64EndTime
		 * @param szRetInfo
		 * @param iLen
		 * @return
		 */
		public int HVAPI_GetPCSFlow(Pointer m_hHv,long dw64StartTime,long dw64EndTime,byte[] szRetInfo,IntByReference iLen);
		
		/**
		 * 设置相机的时间
		 */
		public int HVAPI_SetTime(Pointer m_hHv,int nYear,int nMon,int nDay,int nHour,int nMin,int nSec,int nMSec);
		
		/**
		 * 以下几个是主动连接需要的函数
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 加载监听模块
		 * @param nMaxMonitorCount，最大监控数（默认是 100）<=100
		 * @param szApiVer，版本，null就可以了
		 * @return
		 */
		public int HVAPI_LoadMonitor(int nMaxMonitorCount,String szApiVer);
		
		/**
		 * 打开监听服务
		 * @param nPort, 端口号，6665
		 * @param nType，监听服务类型,LISTEN_TYPE_RECORD=1
		 * @param szApiVer, 版本，null就可以了
		 * @return
		 */
		public int HVAPI_OpenServer(int nPort,int nType,String szApiVer);
		
		/**
		 * 监听服务打开后，获得目前连接上来的设备列表长度
		 * 即缓冲区的大小，并不是设备的数量
		 * @param nDevListLen, 已连接的数量
		 * @param szApiVer
		 * @return
		 */
		public int HVAPI_GetDeviceListSize(IntByReference nDevListLen,String szApiVer);
		
		/**
		 * 获取设备列表的缓冲区，得到SN,（设备的各个SN间以 ; 分隔）
		 * @param szDevList
		 * @param nDevListLen
		 * @param szApiVer
		 * @return
		 */
		public int HVAPI_GetDeviceList(byte[] szDevList,int nDevListLen,String szApiVer);
		
		/**
		 * 通过设备的SN获得现在设备的ip和端口号
		 * @param szDevNameSN，设备名字，SN
		 * @param szApiVer
		 * @param strIP，得到的设备IP地址，byte[256] 
		 * @param strLen，ip地址的长度，默认放进去就是64
		 * @param dwPORT，得到的设备端口号
		 * @return
		 */
		public int HVAPI_GetDeviceInfoAutoLink(String szDevNameSN,String szApiVer,byte[] strIP,int strLen,IntByReference dwPORT);
		
		/**
		 * 关闭监听服务
		 * 关闭监听(该操作会主动去关闭主动连接句柄，必须要放在HVAPI_CloseEx之后，否则会报错)
		 * @param nType，监听服务类型,LISTEN_TYPE_RECORD=1
		 * @return
		 */
		public int HVAPI_CloseServer(int nType);
		
		/**
		 * 卸载监听模块
		 * @return
		 */
		public int HVAPI_UnLoadMonitor();
	}
	
	/**
	 * 获得dll的控制对象
	 * @return
	 */
	public static CLib_HvDeviceDLL getInstance(){
		return CLib_HvDeviceDLL.INSTANCE;
	}
}
