package com.xie.spot.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import ssin.util.DateProcess;

/**
 * 景点采集到的照片
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TSpotPicture")
public class SpotPicture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 所属的景点
	 */
	@ManyToOne
	@JoinColumn(name="spotId")
	private SpotBasic spot;
	
	/**
	 * 获得的时间戳
	 */
	private Long time = System.currentTimeMillis();
	
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
	 * 图片的来源
	 * 平台添加的统一为：SystemAdd
	 */
	private String comefrom;
	
	/**
	 * 首页推荐的,>0，数字越大，越放前面
	 */
	private Integer mainRcmd;
	
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

	public SpotBasic getSpot() {
		return spot;
	}

	public void setSpot(SpotBasic spot) {
		this.spot = spot;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
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

	public String getComefrom() {
		return comefrom;
	}

	public void setComefrom(String comefrom) {
		this.comefrom = comefrom;
	}

	public Integer getMainRcmd() {
		return mainRcmd;
	}

	public void setMainRcmd(Integer mainRcmd) {
		this.mainRcmd = mainRcmd;
	}
}
