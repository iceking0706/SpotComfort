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
 * 根据wcode获得的天气信息
 * 每个1个小时获取一次，替代SpotWeather，因为景点的天气一般获取的是城市天气
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TCodeWeather")
public class CodeWeather {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 城市(或景点)的天气代码
	 */
	@Column(length = 100)
	private String wcode;
	
	/**
	 * 记录生成的时间戳
	 */
	private Long time = System.currentTimeMillis();
	
	/**
	 * 天气，晴、雨、多云等
	 */
	@Column(length = 50)
	private String weather;
	
	/**
	 * 温度
	 */
	private Integer temperature;
	
	/**
	 * 湿度
	 */
	private Integer humidity;
	
	/**
	 * PM2.5的值
	 */
	private Integer pm25;
	
	/**
	 * 空气质量指数
	 */
	private Integer aqi;
	
	/**
	 * 请求的json返回值中的时间戳
	 * 表示这些天气信息是某个时间点的
	 */
	private Long jsonTime;
	
	/**
	 * 获得的json数据，保存一下，可以从中得到其它的更多信息
	 */
	@Column(columnDefinition="LONGTEXT",name="jsonData")
	private String jsonData;
	
	/**
	 * 通过wcode找到的城市或景点的名称
	 */
	@Transient
	private String cityOrSpot;
	
	@Transient
	public String getJsonTimeShow(){
		if(jsonTime==null || jsonTime==0)
			return "----";
		return DateProcess.toString(new Date(jsonTime), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	@Transient
	public String getAqiShow(){
		if(aqi>300){
			return "六级（严重污染）";
		}else if (aqi>200) {
			return "五级（重度污染）";
		}else if (aqi>150) {
			return "四级（中度污染）";
		}else if (aqi>100) {
			return "三级（轻度污染）";
		}else if (aqi>50) {
			return "二级（良）";
		}else if (aqi>0) {
			return "一级（优）";
		}else {
			return "未知";
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWcode() {
		return wcode;
	}

	public void setWcode(String wcode) {
		this.wcode = wcode;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public Integer getHumidity() {
		return humidity;
	}

	public void setHumidity(Integer humidity) {
		this.humidity = humidity;
	}

	public Integer getPm25() {
		return pm25;
	}

	public void setPm25(Integer pm25) {
		this.pm25 = pm25;
	}

	public Long getJsonTime() {
		return jsonTime;
	}

	public void setJsonTime(Long jsonTime) {
		this.jsonTime = jsonTime;
	}

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	public Integer getAqi() {
		return aqi;
	}

	public void setAqi(Integer aqi) {
		this.aqi = aqi;
	}

	@Transient
	public String getCityOrSpot() {
		return cityOrSpot;
	}

	public void setCityOrSpot(String cityOrSpot) {
		this.cityOrSpot = cityOrSpot;
	}
}
