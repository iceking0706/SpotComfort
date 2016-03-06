package com.xie.spot.sys.utils.ffmpeg;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 多路直播对象的管理
 * @author iceking
 *
 */
public class RtmpManager {
	private static final Logger logger = LoggerFactory.getLogger(RtmpManager.class);
	
	//单例模式
	private static RtmpManager instance;

	public static RtmpManager getInstance() {
		if(instance == null){
			instance = new RtmpManager();
		}
		return instance;
	}
	
	private RtmpManager(){
		mapLive = new ConcurrentHashMap<String, TransRTMP>();
		threadPool = Executors.newCachedThreadPool();
		init();
	}
	
	/**
	 * 线程池
	 */
	private ExecutorService threadPool;
	
	/**
	 * 直播流的对象
	 */
	private ConcurrentMap<String, TransRTMP> mapLive;
	
	/**
	 * ffmpeg.exe 文件
	 */
	private File ffmpegExe;
	
	/**
	 * 检测视频流是否超时的循环判断条件
	 */
	private volatile boolean ckStop;
	
	/**
	 * 线程阻塞标记
	 */
	private final String blockObj = "RtmpManagerWaitObject";
	
	/**
	 * 初始化
	 */
	private void init(){
		//启动超时监听线程
		threadPool.execute(new Runnable() {
			public void run() {
				logger.info("RtmpManager livestream timeout checker start...");
				logger.info("FFMepg path: "+((ffmpegExe!=null && ffmpegExe.exists())?ffmpegExe.getPath():"null"));
				ckStop = false;
				while(!ckStop){
					if(mapLive.isEmpty()){
						logger.info("Map is empty, sleep...");
						waitTimeoutChecker();
						continue;
					}
					//判断是否 有超时的，有的话停止掉
					synchronized (mapLive) {
						Iterator<String> iterator = mapLive.keySet().iterator();
						while(iterator.hasNext()){
							String uuid = iterator.next();
							TransRTMP transRTMP = mapLive.get(uuid);
							if(transRTMP.isTimeout()){
								logger.debug("Found livestream timeout: "+transRTMP.getRed5LiveStreamUrl());
								transRTMP.stopIt();
								mapLive.remove(uuid);
							}
						}
					}
					//处理过一次之后，休眠10秒钟
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}
				}
				logger.info("RtmpManager livestream timeout checker stopped.");
			}
		});
	}
	
	private void waitTimeoutChecker(){
		synchronized (blockObj) {
			try {
				blockObj.wait();
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void notifyTimeoutChecker(){
		synchronized (blockObj) {
			blockObj.notifyAll();
		}
	}
	
	/**
	 * 停止超时检测线程
	 */
	public void stopCheckTimeout(){
		ckStop = true;
	}
	
	/**
	 * 结束时候的
	 */
	public void unInit(){
		ckStop = true;
		notifyTimeoutChecker();
		//当前正在直播的停止，并清空map
		if(!mapLive.isEmpty()){
			Iterator<String> iterator = mapLive.keySet().iterator();
			while(iterator.hasNext()){
				TransRTMP transRTMP = mapLive.get(iterator.next());
				transRTMP.stopIt();
			}
			mapLive.clear();
		}
		//线程池停止
		threadPool.shutdown();
	}
	
	/**
	 * 启动相机的rtsp转播，UUID作为直播的唯一标识符
	 * 返回直播的地址，错误返回null
	 * @param inputRTSP
	 * @return
	 */
	public String runRTSP(String uuid,String inputRTSP){
		if(ffmpegExe==null || !ffmpegExe.exists())
			return null;
		
		TransRTMP transRTMP = mapLive.get(uuid);
		if(transRTMP != null){
			//已经在直播，又有连接上来，则更新下时间
			transRTMP.updateOneConnectionTime();
			return transRTMP.getRed5LiveStreamUrl();
		}
		//新建立一个直播对象
		transRTMP = new TransRTMP(ffmpegExe, inputRTSP, uuid);
		transRTMP.setShowLineInfo(new ShowLineInfo() {
			
			@Override
			public void doOneline(String line) {
				logger.info("Return from ffmpeg-> "+line);
			}
		});
		threadPool.execute(transRTMP);
		
		if(transRTMP.isInTrans(8000)){
			//如果正在直播中了，那么就加入到map，否则返回null
			mapLive.put(uuid, transRTMP);
			logger.info("Add new livestream: "+transRTMP.getRed5LiveStreamUrl()+". MapSize="+mapLive.size());
			notifyTimeoutChecker();
			return transRTMP.getRed5LiveStreamUrl(); 
		}
		return null;
	}
	
	/**
	 * 直播一个map4文件
	 * @param uuid
	 * @param mp4Path
	 * @return
	 */
	public String runMP4(String uuid,String mp4Path){
		if(ffmpegExe==null || !ffmpegExe.exists())
			return null;
		
		TransRTMP transRTMP = mapLive.get(uuid);
		if(transRTMP != null){
			//已经在直播，又有连接上来，则更新下时间
			transRTMP.updateOneConnectionTime();
			return transRTMP.getRed5LiveStreamUrl();
		}
		//新建立一个直播对象
		transRTMP = new TransRTMP(ffmpegExe, mp4Path, uuid);
		transRTMP.setMp4File(true);
		threadPool.execute(transRTMP);
		
		if(transRTMP.isInTrans(8000)){
			//如果正在直播中了，那么就加入到map，否则返回null
			mapLive.put(uuid, transRTMP);
			notifyTimeoutChecker();
			return transRTMP.getRed5LiveStreamUrl(); 
		}
		return null;
	}
	
	/**
	 * 根据uuid来结束一个直播流
	 * @param uuid
	 * @return
	 */
	public boolean stop(String uuid){
		TransRTMP transRTMP = findByUuid(uuid);
		if(transRTMP == null)
			return true;
		transRTMP.stopIt();
		mapLive.remove(uuid);
		return true;
	}
	
	/**
	 * 根据直播uuid找到直播对象
	 * @param uuid
	 * @return
	 */
	public TransRTMP findByUuid(String uuid){
		return mapLive.get(uuid);
	}
	
	public File getFfmpegExe() {
		return ffmpegExe;
	}

	public void setFfmpegExe(File ffmpegExe) {
		this.ffmpegExe = ffmpegExe;
	}
	
	
}
