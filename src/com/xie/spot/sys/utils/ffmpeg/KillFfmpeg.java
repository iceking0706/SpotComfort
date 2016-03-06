package com.xie.spot.sys.utils.ffmpeg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xie.spot.sys.Utils;

/**
 * 通过cmd的方式，查看是否有ffmpeg的进程没有停止掉的，
 * 利益windows命令直接杀掉
 * @author iceking
 *
 */
public class KillFfmpeg extends Thread{
	private static final Logger logger = LoggerFactory.getLogger(KillFfmpeg.class);
	
	/**
	 * tasklist /fi "imagename eq ffmpeg.exe"
	 */
	@Override
	public void run() {
		try {
			//解析到的pid
			List<Integer> listPid = new ArrayList<Integer>();
			//查询是否存在ffmpeg运行
			List<String> cmd = new ArrayList<String>();
			cmd.add("tasklist");
			cmd.add("/fi");
			cmd.add("\"imagename eq ffmpeg.exe\"");
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(cmd);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = stdout.readLine();
			while(null != line){
				line = stdout.readLine();
				if(null == line)
					break;
				if(line.startsWith("ffmpeg.exe")){
					//从这行可以找到pid
					//ffmpeg.exe                    8276 Console                    3     22,516 K
					String tmp = line.substring("ffmpeg.exe".length());
					tmp = tmp.trim();
					tmp = tmp.substring(0, tmp.indexOf(' '));
					int pid = Utils.parseInt(tmp);
					if(pid>0)
						listPid.add(pid);
				}
				
			}
			stdout.close();
			if(process != null){
				process.destroy();
				process = null;
			}
			
			//如果找到pid，那么就执行kill
			if(!Utils.isEmpty(listPid)){
				logger.warn("Found ffmpeg running, kill...");
				for(Integer pid: listPid){
					kill(pid.intValue());
				}
			}
			
		} catch (Exception e) {
			logger.error("kill ffmpeg error: "+e.getMessage(), e);
		}
	}
	
	/**
	 * taskkill /pid 3456 /f
	 * @param pid
	 */
	private void kill(int pid){
		try {
			logger.info("start to kill pid: "+pid);
			List<String> cmd = new ArrayList<String>();
			cmd.add("taskkill");
			cmd.add("/pid");
			cmd.add(""+pid);
			cmd.add("/f");
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(cmd);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = stdout.readLine();
			while(null != line){
				line = stdout.readLine();
				if(null == line)
					break;
				
			}
			stdout.close();
			if(process != null){
				process.destroy();
				process = null;
			}
			logger.info("task with pid("+pid+") is killed.");
		} catch (Exception e) {
			logger.error("kill pid("+pid+") error: "+e.getMessage(), e);
		}
	}
}
