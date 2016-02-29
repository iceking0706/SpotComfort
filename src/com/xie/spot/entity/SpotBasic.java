package com.xie.spot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.xie.spot.sys.Utils;

/**
 * 景点的基础信息表
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TSpotBasic")
public class SpotBasic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 景点所在的城市
	 */
	@ManyToOne
	@JoinColumn(name="cityId")
	private City city;
	
	/**
	 * 景点名称
	 */
	@Column(length = 100)
	private String name;
	
	/**
	 * 景点的代码，以后传输信息的时候可能会用到
	 */
	@Column(length = 100)
	private String code;
	
	/**
	 * 景点的天气代码
	 * 用于天气的查询，如果不指定，则使用城市的天气代码
	 */
	@Column(length = 100)
	private String wcode;
	
	/**
	 * 景点的评级
	 * 如5A等的说明
	 */
	@Column(length = 100)
	private String grade;
	
	/**
	 * 景观等级，绝每1，超美2，美3
	 */
	private Integer viewLevel = 2;
	
	/**
	 * 最大承载量，默认0，表示未设置
	 */
	private Integer maxCapacity = 0;
	
	/**
	 * 景点的坐标，用于计算距离
	 * 可以是gps坐标，或者是map的坐标
	 */
	private Double lonX;
	
	private Double latY;
	
	/**
	 * 首页推荐的,>0，数字越大，越放前面
	 */
	private Integer mainRcmd;
	
	/**
	 * 景点包含的图片数量
	 */
	@Transient
	private long picCount = 0;
	
	/**
	 * 景点包含的修正因子数量
	 */
	@Transient
	private long ccCount = 0;
	
	
	/**
	 * 找到该景点的天气代码，
	 * 如果自己的未指定，则使用所在城市的
	 * @return
	 */
	@Transient
	public String getWeatherCode(){
		String curWCode = getWcode();
		if(Utils.isEmpty(curWCode)){
			if(getCity()!=null)
				curWCode = getCity().getWcode();
		}
		return curWCode;
	}
	
	/**
	 * 景点完整名称，省份->城市->景点名称
	 * @return
	 */
	@Transient
	public String showFullName(){
		return city.showFullName()+"："+name;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Integer getViewLevel() {
		return viewLevel;
	}

	public void setViewLevel(Integer viewLevel) {
		this.viewLevel = viewLevel;
	}

	public Integer getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(Integer maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public Double getLonX() {
		return lonX;
	}

	public void setLonX(Double lonX) {
		this.lonX = lonX;
	}

	public Double getLatY() {
		return latY;
	}

	public void setLatY(Double latY) {
		this.latY = latY;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Transient
	public long getPicCount() {
		return picCount;
	}

	public void setPicCount(long picCount) {
		this.picCount = picCount;
	}

	@Transient
	public long getCcCount() {
		return ccCount;
	}

	public void setCcCount(long ccCount) {
		this.ccCount = ccCount;
	}

	public Integer getMainRcmd() {
		return mainRcmd;
	}

	public void setMainRcmd(Integer mainRcmd) {
		this.mainRcmd = mainRcmd;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SpotBasic){
			SpotBasic other = (SpotBasic)obj;
			if(this.id != null && this.id.equals(other.getId()))
				return true;
		}
		return false;
	}
}
