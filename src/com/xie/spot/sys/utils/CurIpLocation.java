package com.xie.spot.sys.utils;

import net.sf.json.JSONObject;

/**
 * 根据现在的ip地址得到所在的城市
 * 一般是 省份->城市
 * @author IcekingT420
 *
 */
public class CurIpLocation {
	/**
	 * 国家
	 */
	private String country;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 地区
	 */
	private String district;
	
	/**
	 * 通过url获取
	 */
	public void fetch(){
		String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
		HttpCall httpCall = new HttpCall(url);
		String result = httpCall.call();
		JSONObject jsonObject = JSONObject.fromObject(result);
		if(jsonObject == null)
			return;
		if(jsonObject.get("country") != null){
			country = new String((String)jsonObject.get("country"));
		}
		if(jsonObject.get("province") != null){
			province = new String((String)jsonObject.get("province"));
		}
		if(jsonObject.get("city") != null){
			city = new String((String)jsonObject.get("city"));
		}
		if(jsonObject.get("district") != null){
			district = new String((String)jsonObject.get("district"));
		}
	}
	
	public String showFullName(){
		String str = "";
		if(province!=null && !province.equals(""))
			str += province;
		if(city!=null && !city.equals("")){
			if(!str.equals(""))
				str += "->";
			str += city;
		}
		if(district!=null && !district.equals("")){
			if(!str.equals(""))
				str += "->";
			str += district;
		}
		return str.equals("")?"unknow":str;
	}
	
	public String getCountry() {
		return country;
	}
	public String getProvince() {
		return province;
	}
	public String getCity() {
		return city;
	}
	public String getDistrict() {
		return district;
	}
}
