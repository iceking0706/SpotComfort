package com.xie.spot.pojo.wechat;

/**
 * 景点舒适度情况
 * @author IcekingT420
 *
 */
public class PjSpotComfort {
	private Long spotId;
	
	private String name;
	
	private String time;
	
	/**
	 * 该景点显示的图片，最新的一张
	 * 以http开头表示外部的，否则内部的
	 * 有默认图片
	 */
	private String spotPic = "images/216x170.jpg";
	
	/**
	 * 舒适度指数，当前的
	 */
	private int cdIndex;
	
	/**
	 * 客流量，每小时
	 */
	private int psgrFlow;
	
	/**
	 * 客流结果
	 */
	private String psgr;
	
	/**
	 * 景观
	 */
	private String view;
	
	/**
	 * 景观等级
	 */
	private int viewL;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCdIndex() {
		return cdIndex;
	}

	public void setCdIndex(int cdIndex) {
		this.cdIndex = cdIndex;
	}

	public int getPsgrFlow() {
		return psgrFlow;
	}

	public void setPsgrFlow(int psgrFlow) {
		this.psgrFlow = psgrFlow;
	}

	public String getPsgr() {
		return psgr;
	}

	public void setPsgr(String psgr) {
		this.psgr = psgr;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public int getViewL() {
		return viewL;
	}

	public void setViewL(int viewL) {
		this.viewL = viewL;
	}

	public Long getSpotId() {
		return spotId;
	}

	public void setSpotId(Long spotId) {
		this.spotId = spotId;
	}
	
	public String getSpotPic() {
		return spotPic;
	}

	public void setSpotPic(String spotPic) {
		this.spotPic = spotPic;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PjSpotComfort){
			PjSpotComfort other = (PjSpotComfort)obj;
			if(this.getSpotId().equals(other.getSpotId()))
				return true;
		}
		return false;
	}
}
