package com.suyou.singnalway;

import java.util.Date;

import ssin.util.DateProcess;

/**
 * 设备连接的基本信息
 * @author IcekingT420
 *
 */
public class SnIpPort {
	/**
	 * 设备sn，唯一标示符
	 */
	private String sn;
	/**
	 * 设备当前连接上来的ip地址
	 */
	private String ip;
	/**
	 * 设备当前连接上来的端口号
	 */
	private int port;
	/**
	 * 获得ip和端口的时间，备用，可以考虑超时是否再次获取
	 */
	private long time;
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getTimeShow(){
		if(time == 0)
			return "";
		return DateProcess.toString(new Date(time), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	@Override
	public String toString() {
		return "SnIpPort: sn="+sn+", ip="+ip+", port="+port+", time="+getTimeShow();
	}
}
