package com.xie.spot.pojo;

import java.io.File;
import java.util.Date;

import ssin.util.DateProcess;
import ssin.util.MyConverter;

/**
 * 文件的信息，目前主要给mysq备份文件使用
 * 
 * @author IcekingT420
 * 
 */
public class PjFileInfo {
	/**
	 * 名称、时间、大小
	 */
	private String name;
	private String time;
	private long lastModify;
	private String size;
	/**
	 * 从文件名里面解析得到的时间，避免复制时候时间出错
	 */
	private int timeInName;
	/**
	 * 文件大小，如果时间一致的时候，则按照大小排序
	 */
	private long length;
	
	public PjFileInfo(){
		
	}
	
	public PjFileInfo(File file){
//		if(file == null || !file.exists() || !file.isFile())
//			return;
		this.name = file.getName();
		this.lastModify = file.lastModified();
		this.time = DateProcess.toString(new Date(this.lastModify), "yyyy-MM-dd HH:mm:ss");
		this.length = file.length();
		this.size = MyConverter.fileSize(file.length());
		
		//解析文件名字 eglockcloud_20141208154721.sql
		String tmpstr = this.name.substring(this.name.indexOf('_')+1, this.name.indexOf('.'));
		Date tmpDate = DateProcess.toDate(tmpstr, "yyyyMMddHHmmss");
		if(tmpDate != null){
			this.timeInName = (int)(tmpDate.getTime()/1000);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public long getLastModify() {
		return lastModify;
	}

	public void setLastModify(long lastModify) {
		this.lastModify = lastModify;
	}

	public int getTimeInName() {
		return timeInName;
	}

	public void setTimeInName(int timeInName) {
		this.timeInName = timeInName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
}
