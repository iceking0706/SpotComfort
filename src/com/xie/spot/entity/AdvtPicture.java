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
 * 在首页滚动的5张广告图片
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TAdvtPicture")
public class AdvtPicture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long time = System.currentTimeMillis();
	//图片的说明
	private String mark;
	
	/**
	 * 图片的url地址，这时候图片存放在硬盘上
	 */
	private String url;
	
	/**
	 * 图片的字节，存放在数据库中
	 */
	@Column(columnDefinition="LONGTEXT",name="data")
	private String data;
	
	/**
	 * 首页推荐的,>0，数字越大，越放前面
	 */
	private Integer mainRcmd;
	
	/**
	 * 该图片点击后的连接地址
	 */
	private String linkUrl;
	
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

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getMainRcmd() {
		return mainRcmd;
	}

	public void setMainRcmd(Integer mainRcmd) {
		this.mainRcmd = mainRcmd;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
}
