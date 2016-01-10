package com.xie.spot.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import ssin.util.DateProcess;

/**
 * 从各个摄像机获得的数据，
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TCameraData")
public class CameraData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 摄像机的唯一号, 
	 * VECAM-D01-LS14024277
	 */
	@Column(length = 100)
	private String sn;
	
	/**
	 * 本次数据获得的时间点，一般是一个整点，
	 */
	private Long time = System.currentTimeMillis();
	
	/**
	 * in数据
	 */
	private Integer din = 0;
	
	/**
	 * out数据
	 */
	private Integer dout = 0;
	
	/**
	 *图片的url地址
	 *保存在uploadfiles/cameraPics里面
	 */
	private String picUrl;
	
	/**
	 * 图片的字节，存放在数据库中
	 */
	@Column(columnDefinition="LONGTEXT",name="picData")
	private String picData;
	
	/**
	 * 保存接收到新数据的时间点，=0，表示使用了原来的数据
	 */
	private Long recvIOTime = 0l;
	
	/**
	 * 保存接收到新图片的时间点，=0，表示使用了原来的数据
	 */
	private Long recvPicTime = 0l;
	
	/**
	 * 每次去获得相机的历史数据，保存，字符，格式time_in_out;time_in_out; 
	 * 16进制字符形式
	 */
	@Column(columnDefinition="LONGTEXT",name="ioRawData")
	private String ioRawData; 
	
	@Transient
	public long longRecvIOTime(){
		return recvIOTime!=null?recvIOTime.longValue():0l;
	}
	
	@Transient
	public long longRecvPicTime(){
		return recvPicTime!=null?recvPicTime.longValue():0l;
	}

	@Transient
	public String getTimeShow(){
		if(time==null || time==0)
			return "----";
		return DateProcess.toString(new Date(time), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getDin() {
		return din;
	}

	public void setDin(Integer din) {
		this.din = din;
	}

	public Integer getDout() {
		return dout;
	}

	public void setDout(Integer dout) {
		this.dout = dout;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getPicData() {
		return picData;
	}

	public void setPicData(String picData) {
		this.picData = picData;
	}

	public Long getRecvIOTime() {
		return recvIOTime;
	}

	public void setRecvIOTime(Long recvIOTime) {
		this.recvIOTime = recvIOTime;
	}

	public Long getRecvPicTime() {
		return recvPicTime;
	}

	public void setRecvPicTime(Long recvPicTime) {
		this.recvPicTime = recvPicTime;
	}

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getIoRawData() {
		return ioRawData;
	}

	public void setIoRawData(String ioRawData) {
		this.ioRawData = ioRawData;
	}
}
