package com.xie.spot.sys.utils.weatherinfo;

/**
 * 实时天气情况解析
 * @author IcekingT420
 *
 */
public class WRealTime {
	/**
	 * 湿度
	 */
	private int sd;
	
	/**
	 * 风向
	 */
	private String wd;
	
	/**
	 * 风速
	 */
	private String ws;
	
	/**
	 * 当前温度
	 */
	private int temp;
	
	/**
	 * 温度获得的时间，12:43 格式
	 */
	private String time;
	
	/**
	 * 把time转换成long型，记录数据库
	 */
	private long timeStamp;
	
	/**
	 * 实时天气，晴、雨之类
	 */
	private String weather;

	public int getSd() {
		return sd;
	}

	public void setSd(int sd) {
		this.sd = sd;
	}

	public String getWd() {
		return wd;
	}

	public void setWd(String wd) {
		this.wd = wd;
	}

	public String getWs() {
		return ws;
	}

	public void setWs(String ws) {
		this.ws = ws;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("实时天气：[");
		sb.append("时间="+time);
		sb.append(", 天气="+weather);
		sb.append(", 温度="+temp);
		sb.append(", 湿度="+sd);
		sb.append(", 风向="+wd);
		sb.append(", 风速="+ws);
		sb.append("]");
		return sb.toString();
	}
}
