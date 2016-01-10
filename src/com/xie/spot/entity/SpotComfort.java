package com.xie.spot.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import ssin.util.DateProcess;

/**
 * 景点的舒适度
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TSpotComfort")
public class SpotComfort {
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
	 * 舒适度，百分比
	 */
	private Integer comfortDegree;
	
	/**
	 * 当时的客流情况
	 */
	private Integer psgrFlow;
	
	/**
	 * 客流评分
	 */
	private Integer psgrScore;
	
	/**
	 * 景观评分
	 */
	private Integer viewScore;
	
	/**
	 * 天气评分
	 */
	private Integer weatherScore; 
	
	@Transient
	public String getViewScoreShow(){
		if(viewScore==null)
			return "一般";
		if(viewScore>=100)
			return "绝美";
		else if(viewScore>=80)
			return "超美";
		else
			return "美";
	}
	
	public String getPsgrScoreShow(){
		if(viewScore==null)
			return "未知";
		if(viewScore>=90)
			return "很多";
		else if(viewScore>=70)
			return "较多";
		else if(viewScore>=50)
			return "适中";
		else if(viewScore>=20)
			return "较少";
		else
			return "很少";
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

	public Integer getComfortDegree() {
		return comfortDegree;
	}

	public void setComfortDegree(Integer comfortDegree) {
		this.comfortDegree = comfortDegree;
	}

	public Integer getPsgrFlow() {
		return psgrFlow;
	}

	public void setPsgrFlow(Integer psgrFlow) {
		this.psgrFlow = psgrFlow;
	}

	public Integer getPsgrScore() {
		return psgrScore;
	}

	public void setPsgrScore(Integer psgrScore) {
		this.psgrScore = psgrScore;
	}

	public Integer getViewScore() {
		return viewScore;
	}

	public void setViewScore(Integer viewScore) {
		this.viewScore = viewScore;
	}

	public Integer getWeatherScore() {
		return weatherScore;
	}

	public void setWeatherScore(Integer weatherScore) {
		this.weatherScore = weatherScore;
	}
}
