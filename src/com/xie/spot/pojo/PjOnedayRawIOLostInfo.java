package com.xie.spot.pojo;

import java.util.Date;
import java.util.List;

import ssin.util.DateProcess;

import com.suyou.singnalway.RecvInOutData;
import com.xie.spot.sys.Utils;

/**
 * 统计某一天某个相机in out数据丢失的情况
 * @author IcekingT420
 *
 */
public class PjOnedayRawIOLostInfo {
	/**
	 * 某一天的字符，yyyy-MM-dd格式
	 */
	private String oneDayStr;
	
	/**
	 * 这一天全部的原始数据
	 */
	private List<RecvInOutData> rawDatas;
	
	/**
	 * 相机对象
	 */
	private String sn;
	
	/**
	 * 这一天的进出求和
	 */
	private int sumIn;
	
	private int sumOut;
	
	
	/**
	 * 这一天应该有多少条，即多少分钟的
	 * 根据相机中配置的开始时间和结束时间计算，减去6分钟(头-1，尾-5)
	 */
	private int onedayShould;
	
	/**
	 * 实际数量，即rawDatas的size
	 */
	private int onedayActual;
	
	/**
	 * 完成率=onedayActual / onedayShould
	 * 如果超过100，则==100
	 */
	private int completePercent;
	
	/**
	 * 丢包率，100-completePercent
	 */
	private int lostPercent;
	
	/**
	 * 根据数据内容来计算
	 * @param oneDayStr
	 * @param rawDatas
	 * @param camera
	 */
	public PjOnedayRawIOLostInfo(String oneDayStr,List<RecvInOutData> rawDatas,int onedayShould){
		this.oneDayStr = oneDayStr;
		this.rawDatas = rawDatas;
		
		//计算
		this.onedayShould = onedayShould-6;
		this.onedayActual = Utils.isEmpty(rawDatas)?0:rawDatas.size();
		this.completePercent = (int)(((double)onedayActual / (double)onedayShould) *100);
		if(this.completePercent>100)
			this.completePercent = 100;
		this.lostPercent = 100 - this.completePercent;
	}
	
	/**
	 * 根据数据量来计算
	 */
	public PjOnedayRawIOLostInfo(String oneDayStr,int onedayActual,int onedayShould){
		this.oneDayStr = oneDayStr;
		
		//计算
		this.onedayShould = onedayShould-6;
		this.onedayActual = onedayActual;
		if(onedayActual<=0){
			this.completePercent = 0;
		}else {
			this.completePercent = (int)(((double)onedayActual / (double)onedayShould) *100);
			if(this.completePercent>100)
				this.completePercent = 100;
		}
		
		this.lostPercent = 100 - this.completePercent;
	}
	
	/**
	 * 获得短日期格式，MM-dd
	 * @return
	 */
	public String getDayShort(){
		Date date = DateProcess.toDate(oneDayStr, "yyyy-MM-dd");
		if(date == null)
			return "----";
		return DateProcess.toString(date, "MM-dd");
	}

	public String getOneDayStr() {
		return oneDayStr;
	}

	public void setOneDayStr(String oneDayStr) {
		this.oneDayStr = oneDayStr;
	}

	public List<RecvInOutData> getRawDatas() {
		return rawDatas;
	}

	public void setRawDatas(List<RecvInOutData> rawDatas) {
		this.rawDatas = rawDatas;
	}


	public int getOnedayShould() {
		return onedayShould;
	}

	public void setOnedayShould(int onedayShould) {
		this.onedayShould = onedayShould;
	}

	public int getOnedayActual() {
		return onedayActual;
	}

	public void setOnedayActual(int onedayActual) {
		this.onedayActual = onedayActual;
	}

	public int getCompletePercent() {
		return completePercent;
	}

	public void setCompletePercent(int completePercent) {
		this.completePercent = completePercent;
	}

	public int getLostPercent() {
		return lostPercent;
	}

	public void setLostPercent(int lostPercent) {
		this.lostPercent = lostPercent;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public int getSumIn() {
		return sumIn;
	}

	public void setSumIn(int sumIn) {
		this.sumIn = sumIn;
	}

	public int getSumOut() {
		return sumOut;
	}

	public void setSumOut(int sumOut) {
		this.sumOut = sumOut;
	}

}
