package com.xie.spot.pojo.wechat;

import java.util.ArrayList;
import java.util.List;

import com.xie.spot.sys.Utils;

/**
 * 城市的景点舒适度
 * @author IcekingT420
 *
 */
public class PjCitySpotC {
	private boolean success = false;
	
	/**
	 * 城市的id号，作为唯一量
	 * =0，表示没有这个数据
	 */
	private Long cityId;
	
	private String province;
	
	private String city;
	
	private String cityPinyin;
	
	private String cityPyszm;
	
	private String cityPicUrl;
	
	private int cityRcmd;
	
	/**
	 * 天气代码
	 */
	private String wcode;
	
	/**
	 * 天气，晴、雨、多云等
	 */
	private String weather;
	
	/**
	 * 天气的图片url
	 * 如果不是以开头的，表示自己工程里面的
	 * 否则是以http开头的
	 */
	private String wthPic;
	
	/**
	 * 温度
	 */
	private int temp;
	
	/**
	 * 湿度
	 */
	private int humi;
	
	/**
	 * PM2.5的值
	 */
	private int pm25;
	
	/**
	 * 空气质量指数
	 */
	private int aqi;
	
	/**
	 * 质量指数文字
	 */
	private String aqiT;
	
	/**
	 * 获得的时间，使用jsontime
	 */
	private String time;
	
	/**
	 * 该城市中，景点的情况
	 */
	private List<PjSpotComfort> spots;
	
	public void addSpot(PjSpotComfort pj){
		if(spots == null)
			spots = new ArrayList<PjSpotComfort>();
		if(!spots.contains(pj))
			spots.add(pj);
	}
	
	public PjSpotComfort findById(Long spotId){
		if(Utils.isEmpty(spots))
			return null;
		for(PjSpotComfort pj: spots){
			if(pj.getSpotId().equals(spotId))
				return pj;
		}
		return null;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getWcode() {
		return wcode;
	}

	public void setWcode(String wcode) {
		this.wcode = wcode;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public int getHumi() {
		return humi;
	}

	public void setHumi(int humi) {
		this.humi = humi;
	}

	public int getPm25() {
		return pm25;
	}

	public void setPm25(int pm25) {
		this.pm25 = pm25;
	}

	public int getAqi() {
		return aqi;
	}

	public void setAqi(int aqi) {
		this.aqi = aqi;
	}

	public String getAqiT() {
		return aqiT;
	}

	public void setAqiT(String aqiT) {
		this.aqiT = aqiT;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<PjSpotComfort> getSpots() {
		return spots;
	}

	public void setSpots(List<PjSpotComfort> spots) {
		this.spots = spots;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getWthPic() {
		return wthPic;
	}

	public void setWthPic(String wthPic) {
		this.wthPic = wthPic;
	}

	public String getCityPinyin() {
		return cityPinyin;
	}

	public void setCityPinyin(String cityPinyin) {
		this.cityPinyin = cityPinyin;
	}

	public String getCityPyszm() {
		return cityPyszm;
	}

	public void setCityPyszm(String cityPyszm) {
		this.cityPyszm = cityPyszm;
	}

	public String getCityPicUrl() {
		return cityPicUrl;
	}

	public void setCityPicUrl(String cityPicUrl) {
		this.cityPicUrl = cityPicUrl;
	}

	public int getCityRcmd() {
		return cityRcmd;
	}

	public void setCityRcmd(int cityRcmd) {
		this.cityRcmd = cityRcmd;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PjCitySpotC){
			PjCitySpotC other = (PjCitySpotC)obj;
			if(this.getCityId().equals(other.getCityId()))
				return true;
		}
		return false;
	}
}
