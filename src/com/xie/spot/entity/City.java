package com.xie.spot.entity;

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

/**
 * 城市信息表
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TCity")
public class City {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 城市名称
	 */
	@Column(length = 100)
	private String name;
	
	/**
	 * 城市的天气代码
	 */
	@Column(length = 100)
	private String wcode;
	
	/**
	 * 城市的省份
	 */
	@Column(length = 100)
	private String province;
	
	/**
	 * 名字的拼音字母，用于排序
	 * 首字母大写
	 * 杭州->HangZhou
	 */
	@Column(length = 100)
	private String pinyin;
	
	/**
	 * 仅仅得到首字母，全部大写
	 * 杭州->HZ
	 */
	@Column(length = 50)
	private String pinyszm;
	
	/**
	 * 城市图片的url地址
	 */
	private String picUrl;
	
	/**
	 * 图片的字节，存放在数据库中
	 */
	@Column(columnDefinition="LONGTEXT",name="picData")
	private String picData;
	
	/**
	 * 城市的推荐值,=0不推荐，越大越热门推荐
	 */
	private Integer rcmd;
	
	/**
	 * 返回城市的全部名称，省份->城市
	 * @return
	 */
	@Transient
	public String showFullName(){
		return province+"->"+name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWcode() {
		return wcode;
	}

	public void setWcode(String wcode) {
		this.wcode = wcode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyszm() {
		return pinyszm;
	}

	public void setPinyszm(String pinyszm) {
		this.pinyszm = pinyszm;
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

	public Integer getRcmd() {
		return rcmd;
	}

	public void setRcmd(Integer rcmd) {
		this.rcmd = rcmd;
	}
}
