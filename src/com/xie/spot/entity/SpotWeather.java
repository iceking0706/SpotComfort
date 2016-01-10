package com.xie.spot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 景点的天气信息
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TSpotWeather")
public class SpotWeather {
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
}
