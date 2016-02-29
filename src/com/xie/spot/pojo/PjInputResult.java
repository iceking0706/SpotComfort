package com.xie.spot.pojo;

/**
 * Excel数据导入的结果
 * 
 * @author iceking
 * 
 */
public class PjInputResult {
	private boolean succ = false;
	private String result = "";

	public PjInputResult() {

	}

	public PjInputResult(boolean succ, String result) {
		this.succ = succ;
		this.result = result;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
