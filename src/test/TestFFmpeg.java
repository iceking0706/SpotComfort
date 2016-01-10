package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import com.xie.spot.sys.utils.ffmpeg.TransRTMP;

public class TestFFmpeg {
	
	private static Process p1 = null;
	/**
	 * 产生命令行
	 * @param ffmpeg
	 * @param inputRTSP
	 * @param outputRTMP
	 * @return
	 */
	public static String gnrFfmpegCmdLine(File ffmpeg,String inputRTSP,String outputRTMP){
		if(ffmpeg == null || !ffmpeg.exists() || !ffmpeg.isFile())
			return null;
		if(inputRTSP == null || inputRTSP.equals(""))
			return null;
		if(outputRTMP == null || outputRTMP.equals(""))
			return null;
		String cmdLine = ffmpeg.getPath();
		cmdLine += " -re -i "+inputRTSP+" -vcodec copy -acodec copy -f flv -y "+outputRTMP;
		return cmdLine;
	}
	
	public static List<String> gnrCmd(File ffmpeg,String inputRTSP,String outputRTMP){
		List<String> cmd = new ArrayList<String>();
		cmd.add(ffmpeg.getPath());
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
		cmd.add(outputRTMP);
		return cmd;
	}
	
	public static void test1(){
		final File ffmpeg = new File("E:\\MyTmps\\ffmpeg.exe");
		final String inputRTSP = "E:\\MyTmps\\222.h264";
		final String outputRTMP = "rtmp://127.0.0.1:1935/live/s11";
		String cmdLine = gnrFfmpegCmdLine(ffmpeg, inputRTSP, outputRTMP);
		System.out.println(cmdLine);
		if(cmdLine == null)
			return;
		try {
			
			new Thread(new Runnable() {
				public void run() {
					exec(gnrCmd(ffmpeg, inputRTSP, outputRTMP));
				}
			}).start();
			
			System.out.println("11111111111111111111111111111   start.....");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void exec( List<String> cmd){  
        try {  
            ProcessBuilder builder = new ProcessBuilder();    
            builder.command(cmd);  
            builder.redirectErrorStream(true);  
            p1 = builder.start();  
            BufferedReader stdout = new BufferedReader(  
                    new InputStreamReader(p1.getInputStream()));  
            String line;  
            while ((line = stdout.readLine()) != null) {  
                System.out.println(line);
            }  
            //p1.waitFor();     
            stdout.close();  
            System.out.println("3333333333333333333333333   p1 close....");
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
	
	public static void main(String[] args) throws InterruptedException {
		//test1();
		
		//Thread.sleep(10000);
		
		//p1.destroy();
		
		File ffmpeg = new File("E:\\MyTmps\\ffmpeg.exe");
		String inputRTSP = "rtsp://192.168.9.101/h264ESVideoTest";
		String outputRTMP = "s11";
		TransRTMP transRTMP = new TransRTMP(ffmpeg, inputRTSP, outputRTMP);
		transRTMP.startIt();
		
		if(transRTMP.isInTrans(0)){
			System.out.println("已经开始发送数据了。直播地址："+transRTMP.getRed5LiveStreamUrl());
			try {
				Thread.sleep(60000);
			} catch (Exception e) {
				
			}
			System.out.println("强制停止");
			transRTMP.stopIt();
		}else{
			System.out.println("启动失败了");
		}
		
	}
}
