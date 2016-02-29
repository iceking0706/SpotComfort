package com.xie.spot.pojo;

import java.util.Date;

import ssin.util.DateProcess;

import com.xie.spot.entity.CameraData;

/**
 * 相机数据，不要每次都去得到lazy的两个data信息
 * 并且使用基础数据
 * @author IcekingT420
 *
 */
public class PjCameraData {
	private long id;
	private String sn;
	private long time;
	private int din;
	private int dout;
	private String picUrl;
	private long recvIOTime;
	private long recvPicTime;
	/**
	 * 这两个data，不是必须的，详情时候再查看
	 */
	private String picData;
	private String ioRawData;
	
	/**
	 * 相机的备注信息，列表显示时候使用
	 */
	private String cmrMark;
	
	public PjCameraData(){
		
	}
	
	/**
	 * 通过数据库记录解析，不包括两个lazy的data
	 * @param po
	 */
	public PjCameraData(CameraData po){
		this.id = po.getId()!=null?po.getId().longValue():0l;
		this.sn = po.getSn();
		this.time = po.getTime()!=null?po.getTime().longValue():0l;
		this.din = po.getDin()!=null?po.getDin().intValue():0;
		this.dout = po.getDout()!=null?po.getDout().intValue():0;
		this.picUrl = po.getPicUrl();
		this.recvIOTime = po.getRecvIOTime()!=null?po.getRecvIOTime().longValue():0l;
		this.recvPicTime = po.getRecvPicTime()!=null?po.getRecvPicTime().longValue():0l;
	}
	
	public String getTimeShow(){
		if(time==0)
			return "----";
		return DateProcess.toString(new Date(time), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getDin() {
		return din;
	}
	public void setDin(int din) {
		this.din = din;
	}
	public int getDout() {
		return dout;
	}
	public void setDout(int dout) {
		this.dout = dout;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public long getRecvIOTime() {
		return recvIOTime;
	}
	public void setRecvIOTime(long recvIOTime) {
		this.recvIOTime = recvIOTime;
	}
	public long getRecvPicTime() {
		return recvPicTime;
	}
	public void setRecvPicTime(long recvPicTime) {
		this.recvPicTime = recvPicTime;
	}
	public String getPicData() {
		return picData;
	}
	public void setPicData(String picData) {
		this.picData = picData;
	}
	public String getIoRawData() {
		return ioRawData;
	}
	public void setIoRawData(String ioRawData) {
		this.ioRawData = ioRawData;
	}

	public String getCmrMark() {
		return cmrMark;
	}

	public void setCmrMark(String cmrMark) {
		this.cmrMark = cmrMark;
	}
}
