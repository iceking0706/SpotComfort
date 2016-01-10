package com.xie.spot.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import ssin.util.DateProcess;

/**
 * 景点的客流
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TSpotPassengerFlow")
public class SpotPassengerFlow {
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
	 * 获得的时间戳
	 */
	private Long time = System.currentTimeMillis();
	
	/**
	 * 存量
	 * 当前时间，景点的客流存量，景点中有多少人
	 */
	private Integer quantity;
	
	/**
	 * 客流的拥挤度，
	 * 百分比，计算得到，游客数量/最大瞬时承载量
	 * 
	 * 很多 90~100
	 * 较多 70~89
	 * 适中 50~69
	 * 较少 20~40
	 * 很少 
	 * 
	 */
	private Integer crowdDegree;
	
	/**
	 * 流量
	 * 记录流量，拥挤度=flow /  (最大承载量/12) *100;
	 */
	private Integer flow;
	
	/**
	 * 图片的来源
	 * 平台添加的统一为：SystemAdd
	 */
	private String comefrom;
	
	@Transient
	public String getTimeShow(){
		if(time==null || time==0)
			return "----";
		return DateProcess.toString(new Date(time), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	@Transient
	public String getSpotFullName(){
		if(spot == null)
			return "----";
		return spot.showFullName();
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

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getCrowdDegree() {
		return crowdDegree;
	}

	public void setCrowdDegree(Integer crowdDegree) {
		this.crowdDegree = crowdDegree;
	}

	public String getComefrom() {
		return comefrom;
	}

	public void setComefrom(String comefrom) {
		this.comefrom = comefrom;
	}

	public Integer getFlow() {
		return flow;
	}

	public void setFlow(Integer flow) {
		this.flow = flow;
	}
}
