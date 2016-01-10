package com.xie.spot.sys.utils.weatherinfo;

/**
 * 预计的天气
 * 小米中是6天的
 * @author IcekingT420
 *
 */
public class WForecastDay {
	/**
	 * 日期，通过date_y一天天的增加
	 */
	private String date;
	
	/**
	 * 预计的温度，是一个范围
	 */
	private String temp;
	
	/**
	 * 预计的天气，晴转多云，范围
	 */
	private String weather;
	
	/**
	 * 风向情况
	 */
	private String wind;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("预计天气：[");
		sb.append("日期="+date);
		sb.append(", 天气="+weather);
		sb.append(", 温度="+temp);
		sb.append(", 风向="+wind);
		sb.append("]");
		return sb.toString();
	}
}
