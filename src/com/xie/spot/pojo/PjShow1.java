package com.xie.spot.pojo;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JDBC;

import ssin.util.DateProcess;
import ssin.util.MyStringUtil;

/**
 * 神仙居景区展示页面的全部东西
 * 
 * @author IcekingT420
 * 
 */
public class PjShow1 {
	/**
	 * 全部的神仙居景点相机sn和名称
	 */
	public final String[][] allSn = new String[][] {
			{ "VECAM-D01-LS14024063", "北大门出入口" },
			{ "VECAM-D01-LS14025267", "南大门出入口一" },
			{ "VECAM-D01-LS14025024", "南大门出入口二" },
			{ "VECAM-D01-LS14024933", "南大门出入口三" },
			{ "VECAM-D01-LS14025386", "北海索道下站一" },
			{ "VECAM-D01-LS14025404", "北海索道下站二" },
			{ "VECAM-D01-LS14024812", "南天索道下站一" },
			{ "VECAM-D01-LS14025679", "南天索道下站二" } };

	/**
	 * 统计景区进入总人数的相机
	 */
	public final String[] inSn = new String[] { "VECAM-D01-LS14024063",
			"VECAM-D01-LS14025267", "VECAM-D01-LS14025024" };

	/**
	 * 统计景区出去总人数的相机
	 */
	public final String[] outSn = new String[] { "VECAM-D01-LS14024063",
			"VECAM-D01-LS14025267", "VECAM-D01-LS14025024",
			"VECAM-D01-LS14024933" };

	/**
	 * 日期，格式 yyyy-MM-dd 没有传进来的话，就按照今天的
	 */
	private Date date;
	/**
	 * 这一天的开始和结束时间点
	 * 06:00 ~ 18:15
	 */
	private long startTime;
	private long endTime;

	/**
	 * 进出的累计
	 */
	private int sumIn=0;
	private int sumOut=0;
	
	/**
	 * 存放相机数据的map
	 */
	private Map<String, CmrSNInfo> mapSN = new HashMap<String, PjShow1.CmrSNInfo>();

	/**
	 * 构造时候的初始化
	 */
	private void init(){
		String dstr = DateProcess.toString(date, "yyyy-MM-dd");
		this.startTime = DateProcess.toDate(dstr+" 06:00", "yyyy-MM-dd HH:mm").getTime();
		this.endTime = DateProcess.toDate(dstr+" 23:59", "yyyy-MM-dd HH:mm").getTime();
		//按照顺序将内容放置到map中去
		for(int i=0;i<allSn.length;i++){
			CmrSNInfo info = new CmrSNInfo();
			info.sn = allSn[i][0];
			info.name = allSn[i][1];
			mapSN.put(info.sn, info);
		}
	}

	public PjShow1() {
		this.date = new Date();
		init();
	}

	/*public PjShow1(Date date) {
		this.date = date;
		init();
	}*/
	
	public String getNowTime(){
		return DateProcess.toString(date, "MM月dd日HH时mm分");
	}
	
	/**
	 * 得到相机的in语句
	 * @return
	 */
	private String gnrSnInSql(){
		String insql = "";
		for(int i=0;i<allSn.length;i++){
			if(i>0)
				insql += ",";
			insql += "'"+allSn[i][0]+"'";
		}
		return insql;
	}
	
	/**
	 * 执行统计操作
	 * @return
	 */
	public boolean action(){
		try {
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			
			//首先得到这些相机的根据sn汇总的数据
			String sql = "select sn,sum(din),sum(dout) from TCameraData where (time between "+startTime+" and "+endTime+") and sn in("+gnrSnInSql()+")";
			sql += " group by sn";
			ResultSet rs = jdbc.executeQuery(sql);
			while(rs.next()){
				String sn = rs.getString(1);
				int sumdin = rs.getInt(2);
				int sumdout = rs.getInt(3);
				CmrSNInfo info = mapSN.get(sn);
				if(info==null)
					continue;
				info.inSum = sumdin;
				info.outSum = sumdout;
				
				//统计进出口的求和
				if(MyStringUtil.isInArray(inSn, sn)){
					this.sumIn += info.inSum;
				}
				if(MyStringUtil.isInArray(outSn, sn)){
					this.sumOut += info.outSum;
				}
			}
			rs.close();
			
			//统计出去的人不能比进入的人多
			if(this.sumOut>this.sumIn)
				this.sumOut=this.sumIn;
			
			//找到sn最新的一张图片，缩略图
			if(!mapSN.isEmpty()){
				Iterator<String> iterator = mapSN.keySet().iterator();
				while(iterator.hasNext()){
					CmrSNInfo info = mapSN.get(iterator.next());
					sql = "select picUrl from TCameraData where sn='"+info.sn+"' order by time desc limit 1";
					rs = jdbc.executeQuery(sql);
					if(rs.next()){
						String picUrl = rs.getString(1);
						if(!Utils.isEmpty(picUrl) && picUrl.endsWith(".jpg")){
							info.picUrl = picUrl.substring(0, picUrl.length()-4)+"_min.jpg";
						}else {
							info.picUrl = "";
						}
					}
					rs.close();
				}
			}
			
			jdbc.stopConnection();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Date getDate() {
		return date;
	}

	public int getSumIn() {
		return sumIn;
	}

	public int getSumOut() {
		return sumOut;
	}
	
	/**
	 * 停留的
	 * @return
	 */
	public int getSumStay(){
		int stay = sumIn-sumOut;
		if(stay<0)
			stay = 0;
		return stay;
	}
	
	/**
	 * 获得相机的具体信息
	 * @param sn
	 * @return
	 */
	public String getCmrName(String sn){
		CmrSNInfo info = mapSN.get(sn);
		if(info == null)
			return "";
		return info.name;
	}
	
	public int getCmrSumIn(String sn){
		CmrSNInfo info = mapSN.get(sn);
		if(info == null)
			return 0;
		return info.inSum;
	}
	
	public int getCmrSumOut(String sn){
		CmrSNInfo info = mapSN.get(sn);
		if(info == null)
			return 0;
		return info.outSum;
	}
	
	/**
	 * 拿到相机的最新的图片
	 * @param sn
	 * @return
	 */
	public String getCmrPicUrl(String sn){
		CmrSNInfo info = mapSN.get(sn);
		if(info == null)
			return "";
		return info.picUrl;
	}
	
	/**
	 * 相机的序号
	 * @param no
	 * @return
	 */
	public String getCmrSN(int no){
		return allSn[no][0];
	}

	/**
	 * 内部类，存放相机数据
	 * @author IcekingT420
	 *
	 */
	private class CmrSNInfo{
		public String sn;
		public String name;
		public int inSum;
		public int outSum;
		/**
		 * sn最新的一张图片
		 */
		public String picUrl;
	}
}
