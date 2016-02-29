package com.xie.spot.pojo;

import java.util.Date;

import ssin.util.DateProcess;

/**
 * 某个相机
 * @author IcekingT420
 *
 */
public class PjOnedayLostMinute {
	/**
	 * 丢失开始和结束的时间，分钟来计算的
	 */
	private long start;
	private long end;
	
	/**
	 * 转成字符，json只能是int型的
	 * @return
	 */
	public String getStartLong(){
		return String.valueOf(start);
	}
	
	public String getEndLong(){
		return String.valueOf(end);
	}
	
	public String getStartStr(){
		if(start == 0)
			return "";
		return DateProcess.toString(new Date(start), "HH:mm");
	}
	
	public String getEndStr(){
		if(end == 0)
			return "";
		return DateProcess.toString(new Date(end), "HH:mm");
	}
	
	public PjOnedayLostMinute() {
	}

	public PjOnedayLostMinute(long start, long end) {
		this.start = start;
		this.end = end;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
}
