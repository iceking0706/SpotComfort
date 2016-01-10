package com.xie.spot.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import ssin.util.DateProcess;

/**
 * 相机的报警数据
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TCameraAlert")
public class CameraAlert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 报警的类型：
	 * 1：相机长时间断线的报警
	 * 2：相机时间调整，通过图片获得的时间与系统时间进行匹配，超过10分钟了，则报警，并调整时间
	 */
	private Integer type = 1;
	
	/**
	 * 摄像机的唯一号, 
	 * VECAM-D01-LS14024277
	 * 
	 * 相同相机，相同类型的报警如果存在，则不发送邮件
	 */
	@Column(length = 100)
	private String sn;
	
	/**
	 * 报警内容的备注
	 */
	private String mark;
	
	/**
	 * 报警记录产生的时间
	 */
	private Long time = System.currentTimeMillis();
	
	/**
	 * 本次报警的处理时间，
	 * 0=未处理
	 */
	private Long prsTime = 0l;
	
	/**
	 * 处理的内容，填写一个文本，发生原因等
	 */
	private String prsMark;
	
	/**
	 * 本次报警会发送邮件的，该字段表示邮件发送的时间
	 * 不重复发送邮件了
	 */
	private Long mailTime = 0l;
	
	/**
	 * 相机备注，不记录数据库
	 */
	@Transient
	private String cmrMark;
	
	/**
	 * 日志打印中的信息
	 * @return
	 */
	@Transient
	public String forLogStr(){
		String str = "报警记录: [ SN="+sn;
		str += ", 类型="+showTypeZh();
		str += ", 备注="+mark;
		str += ", 时间="+getTimeShow();
		if(isProcessed()){
			str += ", 已处理";
			str += ", 时间="+getPrsTimeShow();
			str += ", 内容="+prsMark;
		}else {
			str += ", 未处理";
		}
		if(isSendMailed()){
			str += ", 已发邮件";
			str += ", 时间="+getMailTimeShow();
		}else {
			str += ", 未发邮件";
		}
		str += " ]";
		return str;
	}
	
	@Transient
	public String forMailStr(){
		String str = "[ 时间="+getTimeShow()+", SN="+sn;
		if(cmrMark!=null)
			str += " ("+cmrMark+")";
		str += ", 类型="+showTypeZh();
		str += ", 备注="+mark;
		str += " ]";
		return str;
	}
	
	/**
	 * 报警类型的中文
	 * @return
	 */
	@Transient
	public String showTypeZh(){
		if(type==null)
			return "未知";
		switch (type.intValue()) {
		case 1:
			return "长时间脱机";
		case 2:
			return "时钟偏差";
		default:
			return "未知";
		}
	}
	
	/**
	 * 判断是否处理了，根据处理时间来判断
	 * @return
	 */
	@Transient
	public boolean isProcessed(){
		if(prsTime==null || prsTime==0)
			return false;
		return true;
	}
	
	/**
	 * 判断是否已经发送过email通知了
	 * @return
	 */
	@Transient
	public boolean isSendMailed(){
		if(mailTime==null || mailTime==0)
			return false;
		return true;
	}
	
	@Transient
	public String getTimeShow(){
		if(time==null || time==0)
			return "----";
		return DateProcess.toString(new Date(time), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	@Transient
	public String getPrsTimeShow(){
		if(prsTime==null || prsTime==0)
			return "----";
		return DateProcess.toString(new Date(prsTime), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	@Transient
	public String getMailTimeShow(){
		if(mailTime==null || mailTime==0)
			return "----";
		return DateProcess.toString(new Date(mailTime), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Long getPrsTime() {
		return prsTime;
	}

	public void setPrsTime(Long prsTime) {
		this.prsTime = prsTime;
	}

	public String getPrsMark() {
		return prsMark;
	}

	public void setPrsMark(String prsMark) {
		this.prsMark = prsMark;
	}

	public Long getMailTime() {
		return mailTime;
	}

	public void setMailTime(Long mailTime) {
		this.mailTime = mailTime;
	}

	@Transient
	public String getCmrMark() {
		return cmrMark;
	}

	public void setCmrMark(String cmrMark) {
		this.cmrMark = cmrMark;
	}
}
