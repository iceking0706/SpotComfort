package com.xie.spot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用于临时保存，某个景点的某一天的按小时统计的数据
 * 这样，不用每次去数据库获取了
 * 景点的配置参照：
 * com/xie/spot/pojo/spotshow/SpotsShowCfg.json
 * @author iceking
 *
 */
@Entity
@Table(name="TCameraSpotDHInOut")
public class CameraSpotDHInOut {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer spotNo;
	
	@Column(length=50,name="ddate")
	private String date;
	
	@Column(name="dhour")
	private Integer hour;
	
	@Column(name="din")
	private Integer in;
	
	@Column(name="dout")
	private Integer out;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSpotNo() {
		return spotNo;
	}

	public void setSpotNo(Integer spotNo) {
		this.spotNo = spotNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getIn() {
		return in;
	}

	public void setIn(Integer in) {
		this.in = in;
	}

	public Integer getOut() {
		return out;
	}

	public void setOut(Integer out) {
		this.out = out;
	}
}
