package com.xie.spot.pojo.wechat;

import java.util.ArrayList;
import java.util.List;

import com.xie.spot.sys.Utils;

/**
 * 微信查询的结果
 * @author IcekingT420
 *
 */
public class PjWxResult {
	private boolean success = false;
	private String error;
	//符合查询条件的总共有多少，可以计算页码的
	private long total = 0l;
	//当前查询的页码和大小
	private int page = 0;
	private int size = 10;
	/**
	 * 以城市为单位的返回
	 */
	private List<PjCitySpotC> cities;
	
	public void addCity(PjCitySpotC pj){
		if(cities == null)
			cities = new ArrayList<PjCitySpotC>();
		if(!cities.contains(pj))
			cities.add(pj);
	}
	
	public PjCitySpotC findById(Long cityId){
		if(Utils.isEmpty(cities))
			return null;
		for(PjCitySpotC pj: cities){
			if(pj.getCityId().equals(cityId))
				return pj;
		}
		return null;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List<PjCitySpotC> getCities() {
		return cities;
	}
	public void setCities(List<PjCitySpotC> cities) {
		this.cities = cities;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
