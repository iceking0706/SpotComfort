package com.xie.spot.sys.utils.weatherinfo;

/**
 * 一天的天气
 * 小米中记录今天和昨天的
 * @author IcekingT420
 *
 */
public class WOneday {
	/**
	 * 日期
	 */
	private String date;
	
	/**
	 * 最大湿度
	 */
	private int humidityMax;
	
	/**
	 * 最小湿度
	 */
	private int humidityMin;
	
	/**
	 * 最高温度
	 */
	private int tempMax;
	
	/**
	 * 最低温度
	 */
	private int tempMin;
	
	/**
	 * 天气 结束
	 */
	private String weatherEnd;
	
	/**
	 * 天气 开始
	 */
	private String weatherStart;
	
	/**
	 * 风向 结束
	 */
	private String windDirectionEnd;
	
	/**
	 * 风向 开始
	 */
	private String windDirectionStart;
	
	/**
	 * 最大风速
	 */
	private int windMax;
	
	/**
	 * 最小风速
	 */
	private int windMin;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getHumidityMax() {
		return humidityMax;
	}

	public void setHumidityMax(int humidityMax) {
		this.humidityMax = humidityMax;
	}

	public int getHumidityMin() {
		return humidityMin;
	}

	public void setHumidityMin(int humidityMin) {
		this.humidityMin = humidityMin;
	}

	public int getTempMax() {
		return tempMax;
	}

	public void setTempMax(int tempMax) {
		this.tempMax = tempMax;
	}

	public int getTempMin() {
		return tempMin;
	}

	public void setTempMin(int tempMin) {
		this.tempMin = tempMin;
	}

	public String getWeatherEnd() {
		return weatherEnd;
	}

	public void setWeatherEnd(String weatherEnd) {
		this.weatherEnd = weatherEnd;
	}

	public String getWeatherStart() {
		return weatherStart;
	}

	public void setWeatherStart(String weatherStart) {
		this.weatherStart = weatherStart;
	}

	public String getWindDirectionEnd() {
		return windDirectionEnd;
	}

	public void setWindDirectionEnd(String windDirectionEnd) {
		this.windDirectionEnd = windDirectionEnd;
	}

	public String getWindDirectionStart() {
		return windDirectionStart;
	}

	public void setWindDirectionStart(String windDirectionStart) {
		this.windDirectionStart = windDirectionStart;
	}

	public int getWindMax() {
		return windMax;
	}

	public void setWindMax(int windMax) {
		this.windMax = windMax;
	}

	public int getWindMin() {
		return windMin;
	}

	public void setWindMin(int windMin) {
		this.windMin = windMin;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("一天天气：[");
		sb.append("日期="+date);
		sb.append(", 天气="+weatherStart+" ~ "+weatherEnd);
		sb.append(", 温度="+tempMin+" ~ "+tempMax);
		sb.append(", 湿度="+humidityMin+" ~ "+humidityMax);
		sb.append(", 风向="+windDirectionStart+" ~ "+windDirectionEnd);
		sb.append(", 风速="+windMin+" ~ "+windMax);
		sb.append("]");
		return sb.toString();
	}
}
