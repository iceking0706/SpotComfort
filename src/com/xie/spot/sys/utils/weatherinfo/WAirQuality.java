package com.xie.spot.sys.utils.weatherinfo;

/**
 * 控器质量指数
 * @author IcekingT420
 *
 */
public class WAirQuality {
	/**
	 * 发布时间
	 */
	private String pubTime;
	
	/**
	 * 空气质量指数
	 * 0~50，一级（优）
	 * 51~100，二级（良）
	 * 101~150，三级（轻度污染）
	 * 151~200，四级（中度污染）
	 * 201~300，五级（重度污染）
	 * >300，六级（严重污染）
	 */
	private int aqi;
	
	private int pm25;
	
	private int pm10;
	
	/**
	 * 二氧化硫
	 */
	private int so2;
	
	/**
	 * 二氧化氮
	 */
	private int no2;

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public int getAqi() {
		return aqi;
	}

	public void setAqi(int aqi) {
		this.aqi = aqi;
	}

	public int getPm25() {
		return pm25;
	}

	public void setPm25(int pm25) {
		this.pm25 = pm25;
	}

	public int getPm10() {
		return pm10;
	}

	public void setPm10(int pm10) {
		this.pm10 = pm10;
	}

	public int getSo2() {
		return so2;
	}

	public void setSo2(int so2) {
		this.so2 = so2;
	}

	public int getNo2() {
		return no2;
	}

	public void setNo2(int no2) {
		this.no2 = no2;
	}
	
	/**
	 * 空气质量指数的说明
	 * @return
	 */
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("空气质量：[");
		sb.append("时间="+pubTime);
		sb.append(", 质量指数="+aqi+" "+getAqiShow());
		sb.append(", PM2.5="+pm25);
		sb.append(", PM10="+pm10);
		sb.append(", 二氧化硫="+so2);
		sb.append(", 二氧化氮="+no2);
		sb.append("]");
		return sb.toString();
	}
}
