package com.xie.spot.sys.utils.ffmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用ffmpeg来转发rtmp流
 * 
 * @author IcekingT420
 * 
 */
public class TransRTMP extends Thread {
	/**
	 * ffmpeg.exe文件
	 */
	private File ffmpegExe;

	/**
	 * 输入流，相机的RTSP
	 */
	private String inputRTSP;
	
	/**
	 * 输入的input是否是mp4文件
	 */
	private boolean mp4File;
	
	/**
	 * 是否记录控制台输出到list中保存
	 */
	private boolean recordResult;
	
	/**
	 * 输出流，到red5的live中的直播流名称
	 * 如s1,s2之类的
	 */
	private String outputStreamName;
	
	/**
	 * red5服务器的ip和端口号
	 * 启动之前，设置过来
	 */
	private static String red5Ip = "127.0.0.1";
	
	private static int red5Port = 1935;
	
	private static String appName = "live";

	/**
	 * 处理的线程，命令窗口
	 */
	private Process process;

	/**
	 * 命令行输出的结果
	 */
	private List<String> resultList;

	/**
	 * 输出的片段数量， frame= 14 fps=0.0 q=-1.0 size= 46kB time=00:00:00.52 bitrate=
	 * 732.0kbits/s 每次line从frame=开始的， >3表示已经在成功输出了
	 */
	private int frameCount;
	
	/**
	 * 是否已经启动了
	 */
	private boolean start;
	
	/**
	 * 最近一次直播的时间的时间
	 * 使用直播开始的时间
	 */
	private long liveStreamTime = 0l;
	
	/**
	 * 有需要直播的连接上来的时间，用这个时间来进行timeout的计算
	 */
	private long oneConnectTime = 0l;
	
	/**
	 * 设置本次直播的超时时间，默认是5分钟
	 * 0表示不超时的
	 */
	private long timeout = 300000l;
	
	/**
	 * 分辨率，默认960*540
	 */
	private int ratioWidth = 960;
	private int ratioHeight = 540;
	
	/**
	 * 处理ffmepg执行过程中的进程 process 得到的中间过程的字符串
	 */
	private ShowLineInfo showLineInfo;

	/**
	 * 构造函数
	 * ffmpeg -rtsp_transport tcp -i rtsp://192.168.5.26:554/h264ESVideoTest -vcodec copy -acodec copy -f flv -r 12 -s 960x640 -an rtmp://127.0.0.1:1935/live/s11
	 * 
	 * rtmp地址，最后的名字是相机sn的md5值
	 * @param ffmpegExe
	 * @param inputRTSP
	 * @param outputRTMP
	 */
	public TransRTMP(File ffmpegExe, String inputRTSP, String outputStreamName) {
		this.ffmpegExe = ffmpegExe;
		this.inputRTSP = inputRTSP;
		this.outputStreamName = outputStreamName;
		this.resultList = new ArrayList<String>();
		this.frameCount = 0;
	}
	
	/**
	 * 使用相机的rtsp
	 * @return
	 */
	private List<String> gnrCmd() {
		List<String> cmd = new ArrayList<String>();
		cmd.add(ffmpegExe.getPath());
		//-rtsp_transport tcp
		//强制以tcp方式
		if(!mp4File){
			cmd.add("-rtsp_transport");
			cmd.add("tcp");
		}
		cmd.add("-i");
		cmd.add("\""+inputRTSP+"\"");
		cmd.add("-vcodec");
		cmd.add("copy");
		cmd.add("-acodec");
		cmd.add("copy");
		cmd.add("-f");
		cmd.add("flv");
		cmd.add("-s");
		cmd.add(ratioWidth+"x"+ratioHeight);
		cmd.add("-an");
		//传输的地址，肯定是本地的，nginx
		cmd.add("\"rtmp://127.0.0.1:"+red5Port+"/"+appName+"/"+outputStreamName+"\"");
		//cmd.add("\""+getRed5LiveStreamUrl()+"\"");
		return cmd;
	}

	/**
	 * 产生命令行
	 * 
	 * @return
	 */
	private List<String> gnrCmd_mp4() {
		List<String> cmd = new ArrayList<String>();
		cmd.add(ffmpegExe.getPath());
		cmd.add("-re");
		cmd.add("-i");
		cmd.add(inputRTSP);
		cmd.add("-vcodec");
		cmd.add("copy");
		cmd.add("-acodec");
		cmd.add("copy");
		cmd.add("-f");
		cmd.add("flv");
		cmd.add("-y");
		cmd.add("rtmp://"+red5Ip+":"+red5Port+"/"+appName+"/"+outputStreamName);
		return cmd;
	}

	public boolean isTransing() {
		return start && frameCount > 3;
	}
	
	private void sleepIt(long mm){
		try {
			Thread.sleep(mm);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 等待一定时间判断是否已经在传输了
	 * @param timeout
	 * @return
	 */
	public boolean isInTrans(long tmot){
		long curTimeout = tmot>0?tmot:5000;
		long startTime = System.currentTimeMillis();
		sleepIt(1000);
		while(System.currentTimeMillis()-startTime <= curTimeout){
			if(isTransing())
				return true;
			sleepIt(1000);
		}
		return false;
	}
	
	public boolean isStart() {
		return start;
	}
	
	public List<String> getResultList() {
		return resultList;
	}

	public void startIt(){
		if(isStart())
			return;
		start();
	}
	
	public void stopIt(){
		if(!isStart())
			return;
		if(process != null){
			process.destroy();
			process = null;
		}
		start = false;
	}
	
	/**
	 * 获得直播的地址
	 * @return
	 */
	public String getRed5LiveStreamUrl(){
		/*if(red5Port == 1935)
			return "rtmp://"+red5Ip+"/"+appName+"/"+outputStreamName;
		else
			return "rtmp://"+red5Ip+":"+red5Port+"/"+appName+"/"+outputStreamName; */
		return "rtmp://"+red5Ip+":"+red5Port+"/"+appName+"/"+outputStreamName;
	}
	
	/**
	 * 判断是否超时
	 * 只有在直播开始之后才能够判断
	 * @return
	 */
	public boolean isTimeout(){
		if(liveStreamTime == 0l)
			return false;
		if(System.currentTimeMillis()-oneConnectTime >= timeout)
			return true;
		return false;
	}

	/**
	 * 在线程中启动时候
	 */
	@Override
	public void run() {
		try {
			resultList.clear();
			frameCount = 0;
			start = true;
			liveStreamTime = 0l;
			oneConnectTime = 0l;
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(gnrCmd());
			builder.redirectErrorStream(true);
			process = builder.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
//			String line;
//			while ((line = stdout.readLine()) != null) {
//				resultList.add(line);
//				if(line.startsWith("frame=")){
//					frameCount ++;
//					//表示直播开始的起点时间
//					if(liveStreamTime == 0)
//						liveStreamTime = System.currentTimeMillis();
//				}
//				System.out.println(line);
//			}
			
			//while 循环的写法换一下
			String line = stdout.readLine();
			while(null != line){
				line = stdout.readLine();
				if(null == line)
					break;
				if(recordResult)
					resultList.add(line);
				if(line.startsWith("frame=")){
					frameCount ++;
					//表示直播开始的起点时间
					if(liveStreamTime == 0l){
						liveStreamTime = System.currentTimeMillis();
						oneConnectTime = liveStreamTime;
					}
				}
				if(showLineInfo!=null)
					showLineInfo.doOneline(line);
			}
			stdout.close();
			if(process != null){
				process.destroy();
				process = null;
			}
			start = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getOutputStreamName() {
		return outputStreamName;
	}

	public void setOutputStreamName(String outputStreamName) {
		this.outputStreamName = outputStreamName;
	}

	

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getLiveStreamTime() {
		return liveStreamTime;
	}

	public long getOneConnectTime() {
		return oneConnectTime;
	}

	public void setOneConnectTime(long oneConnectTime) {
		this.oneConnectTime = oneConnectTime;
	}
	
	public void updateOneConnectionTime(){
		this.oneConnectTime = System.currentTimeMillis();
	}

	public int getRatioWidth() {
		return ratioWidth;
	}

	public void setRatioWidth(int ratioWidth) {
		this.ratioWidth = ratioWidth;
	}

	public int getRatioHeight() {
		return ratioHeight;
	}

	public void setRatioHeight(int ratioHeight) {
		this.ratioHeight = ratioHeight;
	}

	public ShowLineInfo getShowLineInfo() {
		return showLineInfo;
	}

	public void setShowLineInfo(ShowLineInfo showLineInfo) {
		this.showLineInfo = showLineInfo;
	}

	public boolean isMp4File() {
		return mp4File;
	}

	public void setMp4File(boolean mp4File) {
		this.mp4File = mp4File;
	}

	public boolean isRecordResult() {
		return recordResult;
	}

	public void setRecordResult(boolean recordResult) {
		this.recordResult = recordResult;
	}

	public static String getRed5Ip() {
		return red5Ip;
	}

	public static void setRed5Ip(String red5Ip) {
		TransRTMP.red5Ip = red5Ip;
	}

	public static int getRed5Port() {
		return red5Port;
	}

	public static void setRed5Port(int red5Port) {
		TransRTMP.red5Port = red5Port;
	}

	public static String getAppName() {
		return appName;
	}

	public static void setAppName(String appName) {
		TransRTMP.appName = appName;
	}
}
