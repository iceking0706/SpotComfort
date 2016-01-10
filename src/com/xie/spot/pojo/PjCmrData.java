package com.xie.spot.pojo;

import java.util.ArrayList;
import java.util.List;

public class PjCmrData {
	private String sn;
	private List<PjCmrDataInner> data;
	
	public void addData(PjCmrDataInner inner){
		if(data == null){
			data = new ArrayList<PjCmrDataInner>();
		}
		data.add(inner);
	}
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public List<PjCmrDataInner> getData() {
		return data;
	}
	public void setData(List<PjCmrDataInner> data) {
		this.data = data;
	}
}
