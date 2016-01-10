package com.xie.spot.pojo;

import com.xie.spot.entity.SpotBasic;

/**
 * 景点舒适度的计算结果
 * @author IcekingT420
 *
 */
public class PjSpotComfortCalcuResult {
	private SpotBasic spot;
	
	private String spotFullName;
	//当时的客流情况
	private int psgrFlow;
	//客流拥挤度的评分
	private int psgrScore;
	//景观评分
	private int viewScore;
	//气象评分
	private int weatherScore;
	
	/**
	 * 根据以上三个值计算舒适度
	 * @return
	 */
	public int getComfortDegree(){
		double d1 = (double)psgrScore;
		double d2 = (double)viewScore;
		double d3 = (double)weatherScore;
		double cd = d1*0.7+d2*0.2+d3*0.1;
		return (int)cd;
	}
	
	public String getSpotFullName() {
		return spotFullName;
	}
	public void setSpotFullName(String spotFullName) {
		this.spotFullName = spotFullName;
	}
	public int getPsgrScore() {
		return psgrScore;
	}
	public void setPsgrScore(int psgrScore) {
		this.psgrScore = psgrScore;
	}
	public int getViewScore() {
		return viewScore;
	}
	public void setViewScore(int viewScore) {
		this.viewScore = viewScore;
	}
	public int getWeatherScore() {
		return weatherScore;
	}
	public void setWeatherScore(int weatherScore) {
		this.weatherScore = weatherScore;
	}

	public SpotBasic getSpot() {
		return spot;
	}

	public void setSpot(SpotBasic spot) {
		this.spot = spot;
	}

	public int getPsgrFlow() {
		return psgrFlow;
	}

	public void setPsgrFlow(int psgrFlow) {
		this.psgrFlow = psgrFlow;
	}
}
