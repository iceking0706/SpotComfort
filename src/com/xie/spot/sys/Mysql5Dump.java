package com.xie.spot.sys;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 进行mysql5数据的备份操作
 * @author IcekingT420
 *
 */
public class Mysql5Dump {
	/**
	 * msyql5里面的 mysqldump.exe 所在的文件
	 */
	private File mysql5DumpExe;
	
	/**
	 * msyql5 服务器的ip地址，端口默认3306
	 */
	private String serverIp = "127.0.0.1";
	
	private int serverPort = 3306;
	
	/**
	 * 连接数据库的用户名和密码 一般用户是root
	 */
	private String username;
	
	private String password;
	
	/**
	 * 备份文件的输出文件夹
	 */
	private File outputDir;
	
	private Process process;

	public Mysql5Dump(File mysql5DumpExe, String serverIp, int serverPort,
			String username, String password, File outputDir) {
		this.mysql5DumpExe = mysql5DumpExe;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.username = username;
		this.password = password;
		this.outputDir = outputDir;
	}
	
	/**
	 * 给默认的mysql5数据库使用
	 * @param mysql5DumpExe
	 * @param outputDir
	 */
	public Mysql5Dump(File mysql5DumpExe,File outputDir){
		this(mysql5DumpExe, Utils.db_ip, Utils.db_port, Utils.db_username, Utils.db_password, outputDir);
	}
	
	/**
	 * 执行批处理
	 * @param cmd
	 * @return
	 */
	private List<String> actCMD(String cmd){
		List<String> relist = null;
		try{
			
			relist = new ArrayList<String>();
			Process p1 = Runtime.getRuntime().exec("cmd /c "+cmd);
			
			LineNumberReader input = new LineNumberReader(new InputStreamReader(p1.getInputStream()));
			String line = null;
			int count = 0;
			while ((line = input.readLine()) != null) {
				line = line.trim();
				if(line.equals(""))
					continue;
				relist.add(line);
				count++;
			}
			input.close();
			
			p1.destroy();
			
		}catch(Exception e){
			e.printStackTrace();
			relist = null;
		}
		
		return relist;
	}
	
	/**
	 * 对某个一个数据库进行备份操作
	 * @param dbName
	 * @return
	 */
	public File dump(String dbName){
		File backupFile = null;
		try {
			if(mysql5DumpExe == null || !mysql5DumpExe.exists()){
				System.out.println("File mysqldump.exe is not exist");
				return null;
			}
			if(outputDir == null || !outputDir.exists()){
				System.out.println("Output directory is not exist");
				return null;
			}
			
			String cmdLine = ""+mysql5DumpExe.getPath();
			cmdLine = cmdLine.substring(0, cmdLine.length()-4);
			cmdLine += " -h"+serverIp;
			cmdLine += " -P"+serverPort;
			cmdLine += " -u"+username;
			cmdLine += " -p"+password;
			cmdLine += " "+dbName+" >";
			cmdLine += outputDir.getPath()+"\\";
			//生成文件名称 dbName_日期时间.sql
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			String fileName = dbName+"_"+format.format(new Date())+".sql";
			cmdLine += fileName;
			
			//开始执行批处理
			List<String> cmdList = actCMD(cmdLine);
			
			File outFile = new File(outputDir,fileName);
			if(outFile.exists())
				backupFile = outFile;
		} catch (Exception e) {
			e.printStackTrace();
			backupFile = null;
		}
		return backupFile;
	}

	public File getMysql5DumpExe() {
		return mysql5DumpExe;
	}

	public void setMysql5DumpExe(File mysql5DumpExe) {
		this.mysql5DumpExe = mysql5DumpExe;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}
	
	/*
	public static void main(String[] args) {
		Mysql5Dump mysql5Dump = new Mysql5Dump(new File("E:\\临时文件\\mysqldump\\mysqldump.exe"), new File("E:\\临时文件\\mysqldump"));
		File file = mysql5Dump.dump("eglockcloud");
		if(file!=null)
			System.out.println(file.getPath());
	}
	*/
	
}
