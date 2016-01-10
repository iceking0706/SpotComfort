package com.xie.spot.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.xie.spot.sys.Utils;

import ssin.util.DateProcess;

/**
 * 摄像机设备的配置信息，目前按照仙居8个点来设计
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TCameraCfg")
public class CameraCfg {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 摄像机的类型，考虑以后的扩展
	 * 1：信路威，默认
	 */
	private Integer type = 1;
	
	/**
	 * 摄像机的唯一号, 
	 * VECAM-D01-LS14024277
	 * sn确保数据库中唯一
	 */
	@Column(length = 100)
	private String sn;
	
	/**
	 * SIM卡的信息，一般是IMEI的号码
	 * ICCID
	 */
	@Column(length = 100)
	private String sim;
	
	/**
	 * SIM卡的PUK数字
	 */
	@Column(length = 50)
	private String puk;
	
	/**
	 * 运营商
	 */
	@Column(length = 50)
	private String provider;
	
	/**
	 * 该sim的手机号码，可以考虑后期的短信使用
	 */
	@Column(length = 50)
	private String telNo;
	
	/**
	 * 备注，目前就是安装位置
	 */
	private String mark;
	
	/**
	 * 摄像机连接上来的ip地址
	 */
	@Column(length = 50)
	private String ip;
	
	/**
	 * 摄像机连接上来的端口号
	 */
	private Integer port;
	
	/**
	 * ip和port得到的时间
	 * 这个时间也可以作为检测到联机的时间，即onlineTime
	 */
	private Long time;
	
	/**
	 * 判断是否联机，1=联机
	 */
	private Integer online = 0;
	
	/**
	 * 拍照的间隔时间，距离上一张
	 * 分钟
	 */
	private Integer tkpInterval = 60;
	
	/**
	 * 拍照的开始和结束时间，早上8点~晚上20点
	 */
	private Integer tkpHourSt = 8;
	private Integer tkpHourEd = 20;
	
	/**
	 * 获得inout的时间间隔
	 */
	private Integer inoutInterval = 10;
	
	/**
	 * 该相机是否在用
	 * 0/null: 不使用
	 * 1：在用的
	 */
	private Byte inUse = (byte)1;
	
	/**
	 * 断线超时报警的时间，分钟
	 */
	private Integer offlineTimeout;
	
	/**
	 * 应用场景，进行分类使用的
	 */
	private String scene;
	
	/**
	 * 是否需要长时间断线报警了
	 * 返回已经超时的时间
	 * 0=未超时
	 * 当返回值大于0的时候，表示该相机需要断线报警了
	 * @return
	 */
	@Transient
	public long needTimeoutOfflineAlert(){
		if(!isUsing())
			return 0l;
		if(isCmrOnline())
			return 0l;
		if(time==null || time==0)
			return 0l;
		//现在时间 - 最近一次联机时间 >= 设定的断线超时报警时间
		long sub = System.currentTimeMillis()-time.longValue();
		if(sub >= offlineTimeout.longValue()*60000)
			return sub;
		return 0l;
	}
	
	/**
	 * 启用
	 */
	@Transient
	public void useIt(){
		inUse = (byte)1;
	}
	
	/**
	 * 不启用
	 */
	@Transient
	public void useItNot(){
		inUse = (byte)0;
	}
	
	@Transient
	public boolean isUsing(){
		if(inUse!=null && inUse.intValue() == 1)
			return true;
		return false;
	}
	
	@Transient
	public String getTimeShow(){
		if(time==null || time==0)
			return "----";
		return DateProcess.toString(new Date(time), DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}
	
	/**
	 * 判断相机是否联机的
	 * @return
	 */
	@Transient
	public boolean isCmrOnline(){
		if(online == null || online.intValue() != 1)
			return false;
		return true;
	}
	
	/**
	 * 获得每天记录的最开始的时间点，
	 * 这样，第二天一开始获取数据时候，就不用记录晚上那些不需要的时间中，
	 * 相机保存下来的数据了
	 * 
	 * oneDayStr: yyyy-MM-dd格式的
	 * @return
	 */
	@Transient
	public long oneDayFirstRecordTime(String oneDayStr){
		//某一天的最开始
		String todayStr = Utils.isEmpty(oneDayStr)?DateProcess.toString(new Date(), "yyyy-MM-dd"):oneDayStr;
		long todayLl = DateProcess.toDate(todayStr+" 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		//拍照起始时间未设置，默认不拍照，返回0
		if(tkpHourSt==null || tkpHourEd==null){
			return todayLl;
		}
		
		if(tkpHourEd.intValue() < tkpHourSt.intValue()){
			//表示开始时间是前一天的
			todayLl = todayLl - DateProcess.onedaymm;
		}
		return todayLl+tkpHourSt.longValue()*3600000;
	}
	
	/**
	 * 一天最后的记录时间
	 * @return
	 */
	@Transient
	public long oneDayLastRecordTime(String oneDayStr){
		String todayStr = Utils.isEmpty(oneDayStr)?DateProcess.toString(new Date(), "yyyy-MM-dd"):oneDayStr;
		if(tkpHourSt==null || tkpHourEd==null){
			return DateProcess.toDate(todayStr+" 23:59:00", "yyyy-MM-dd HH:mm:ss").getTime();
		}
		long todayLl = DateProcess.toDate(todayStr+" 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		return todayLl+tkpHourEd.longValue()*3600000-60000;
	}
	
	/**
	 * 某一天原始数据应该获得的总数量
	 * @return
	 */
	@Transient
	public int oneDayRawTotalMinutes(){
		long sub = oneDayLastRecordTime(null)-oneDayFirstRecordTime(null);
		return (int)sub/60000;
	}
	
	/**
	 * 判断当前时间是否在拍照时间之内
	 * @return
	 */
	@Transient
	public boolean inValidHours(){
		//拍照起始时间未设置，默认不拍照
		if(tkpHourSt==null || tkpHourEd==null)
			return false;
		int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		//要分几种情况来考虑
		if(tkpHourEd.intValue() == tkpHourSt.intValue()){
			//开始和结束一样，分两张，如果都是0，则表示全天
			if(tkpHourSt.intValue() == 0)
				return true;
			//不是0，则就是这个小时内的运行获得数据
			if(curHour == tkpHourSt.intValue())
				return true;
			
		}else if(tkpHourEd.intValue() > tkpHourSt.intValue()){
			//结束大于开始，表示同一天的，正常情况
			if(curHour>=tkpHourSt.intValue() && curHour<tkpHourEd.intValue())
				return true;
		}else {
			//结束比开始小，表示跨天的
			if((curHour>=tkpHourSt.intValue() && curHour<=23) || (curHour>=0 && curHour<tkpHourEd.intValue()))
				return true;
		}
		
		return false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getSim() {
		return sim;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getOnline() {
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getPuk() {
		return puk;
	}

	public void setPuk(String puk) {
		this.puk = puk;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}


	public Integer getTkpHourSt() {
		return tkpHourSt;
	}

	public void setTkpHourSt(Integer tkpHourSt) {
		this.tkpHourSt = tkpHourSt;
	}

	public Integer getTkpHourEd() {
		return tkpHourEd;
	}

	public void setTkpHourEd(Integer tkpHourEd) {
		this.tkpHourEd = tkpHourEd;
	}

	public Integer getTkpInterval() {
		return tkpInterval;
	}

	public void setTkpInterval(Integer tkpInterval) {
		this.tkpInterval = tkpInterval;
	}

	public Integer getInoutInterval() {
		return inoutInterval;
	}

	public void setInoutInterval(Integer inoutInterval) {
		this.inoutInterval = inoutInterval;
	}

	public Byte getInUse() {
		return inUse;
	}

	public void setInUse(Byte inUse) {
		this.inUse = inUse;
	}

	public Integer getOfflineTimeout() {
		return offlineTimeout;
	}

	public void setOfflineTimeout(Integer offlineTimeout) {
		this.offlineTimeout = offlineTimeout;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

}
