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
 * 景点舒适度修正因子表
 * 默认一个景点一条修正因子记录
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TSpotComfortCorrect")
public class SpotComfortCorrect {
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
	 * 季节修正
	 */
	@Column(length = 100)
	private String seasonFactor;
	
	/**
	 * 对舒适度最终值的影响，可以有正负
	 */
	private Integer seasonScore;
	
	@Column(length = 100)
	private String weatherFactor;
	
	private Integer weatherScore;
	
	@Column(length = 100)
	private String tempFactor;
	
	private Integer tempScore;
	
	@Column(length = 100)
	private String passengerFactor;
	
	private Integer passengerScore;
	
	/**
	 * 备注
	 */
	private String mark;
	
	/**
	 * 产生一个字符串
	 * @return
	 */
	@Transient
	public String getShowStr(){
		String str = "";
		if(!Utils.isEmpty(seasonFactor) && seasonScore!=null && !seasonScore.equals(0))
			str += (str.equals("")?"":", ")+"季节："+seasonFactor+" "+(seasonScore>0?"+":"")+String.valueOf(seasonScore);
		if(!Utils.isEmpty(weatherFactor) && weatherScore!=null && !weatherScore.equals(0))
			str += (str.equals("")?"":", ")+"天气："+weatherFactor+" "+(weatherScore>0?"+":"")+String.valueOf(weatherScore);
		if(!Utils.isEmpty(tempFactor) && tempScore!=null && !tempScore.equals(0))
			str += (str.equals("")?"":", ")+"温度："+tempFactor+" "+(tempScore>0?"+":"")+String.valueOf(tempScore);
		if(!Utils.isEmpty(passengerFactor) && passengerScore!=null && !passengerScore.equals(0))
			str += (str.equals("")?"":", ")+"客流："+passengerFactor+" "+(passengerScore>0?"+":"")+String.valueOf(passengerScore);
		if(!Utils.isEmpty(mark))
			str += (str.equals("")?"":", ")+"备注："+mark;
		return str;
	}
	
	/**
	 * 判断是否有效
	 * @return
	 */
	@Transient
	public boolean isValid(){
		//四个因子至少有一个不为空
		if(Utils.isEmpty(seasonFactor) && Utils.isEmpty(weatherFactor) && Utils.isEmpty(tempFactor) && Utils.isEmpty(passengerFactor))
			return false;
		//因子有，但是调整值为0
		if(!Utils.isEmpty(seasonFactor) && (seasonScore==null || seasonScore.equals(0)))
			return false;
		if(!Utils.isEmpty(weatherFactor) && (weatherScore==null || weatherScore.equals(0)))
			return false;
		if(!Utils.isEmpty(tempFactor) && (tempScore==null || tempScore.equals(0)))
			return false;
		if(!Utils.isEmpty(passengerFactor) && (passengerScore==null || passengerScore.equals(0)))
			return false;
		return true;
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

	public String getSeasonFactor() {
		return seasonFactor;
	}

	public void setSeasonFactor(String seasonFactor) {
		this.seasonFactor = seasonFactor;
	}

	public Integer getSeasonScore() {
		return seasonScore;
	}

	public void setSeasonScore(Integer seasonScore) {
		this.seasonScore = seasonScore;
	}

	public String getWeatherFactor() {
		return weatherFactor;
	}

	public void setWeatherFactor(String weatherFactor) {
		this.weatherFactor = weatherFactor;
	}

	public Integer getWeatherScore() {
		return weatherScore;
	}

	public void setWeatherScore(Integer weatherScore) {
		this.weatherScore = weatherScore;
	}

	public String getTempFactor() {
		return tempFactor;
	}

	public void setTempFactor(String tempFactor) {
		this.tempFactor = tempFactor;
	}

	public Integer getTempScore() {
		return tempScore;
	}

	public void setTempScore(Integer tempScore) {
		this.tempScore = tempScore;
	}

	public String getPassengerFactor() {
		return passengerFactor;
	}

	public void setPassengerFactor(String passengerFactor) {
		this.passengerFactor = passengerFactor;
	}

	public Integer getPassengerScore() {
		return passengerScore;
	}

	public void setPassengerScore(Integer passengerScore) {
		this.passengerScore = passengerScore;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
