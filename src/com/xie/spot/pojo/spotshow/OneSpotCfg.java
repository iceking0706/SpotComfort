package com.xie.spot.pojo.spotshow;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.xie.spot.pojo.PjCmrSNInfoForShow;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JDBC;

import ssin.util.DateProcess;
import ssin.util.MyStringUtil;


/**
 * 单个相机的配置
 * @author iceking
 *
 */
public class OneSpotCfg {
	
	/**
	 * 景点的编号
	 */
	private int spotNo;
	
	/**
	 * 景点显示的名称
	 */
	private String spotName;
	
	/**
	 * 景点的备注信息，目前没有使用
	 */
	private String spotMark;
	
	/**
	 * 这个景点全部的相机
	 */
	private String[] allSn;
	
	/**
	 * 这些相机中，用于统计进入数据的
	 */
	private String[] inSn;
	
	/**
	 * 这些相机中，用来统计离开数据的
	 */
	private String[] outSn;
	
	/**
	 * 允许查看视频的相机
	 */
	private String[] liveSn;
	
	/**
	 * 一天统计数据的开始和结束时间
	 */
	private String startTime = "08:00";
	
	private String endTime = "18:15";
	
	/**
	 * 某台相机现在的统计情况
	 */
	private Map<String, PjCmrSNInfoForShow> mapSN;
	
	/**
	 * 当天进出的累计统计
	 */
	private int sumIn=0;
	private int sumOut=0;
	
	/**
	 * 保存上次执行action的日期
	 */
	private String actionDate;
	
	public String preActionDate(){
		return actionDate;
	}
	
	
	/**
	 * 得到相机的in语句
	 * @return
	 */
	private String gnrSnInSql(String[] sns){
		String insql = "";
		for(int i=0;i<sns.length;i++){
			if(i>0)
				insql += ",";
			insql += "'"+sns[i]+"'";
		}
		return insql;
	}
	
	/**
	 * 统计当天的数据
	 * @return
	 */
	public boolean action(){
		return action(null);
	}
	
	/**
	 * 统计某一天的数据
	 * @param date
	 * @return
	 */
	public boolean action(String date){
		try {
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			
			//相机统计的map的初始化
			if(mapSN == null){
				//需要把所有相机的名称去数据库中读取出来
				//查询所有相机的备注，即名字
				mapSN = new HashMap<String, PjCmrSNInfoForShow>();
				//临时存放查询结果的map，为了排序使用
				String sql = "select sn,mark from tcameracfg where sn in ("+gnrSnInSql(allSn)+")";
				ResultSet rs = jdbc.executeQuery(sql);
				Map<String, String> tmpMap = new HashMap<String, String>();
				while(rs.next()){
					String sn = rs.getString(1);
					String mark = rs.getString(2);
					if(mark == null)
						mark = "No Match";
					tmpMap.put(sn, mark);
				}
				rs.close();
				//按照顺序将内容放置到map中去
				for(int i=0;i<allSn.length;i++){
					PjCmrSNInfoForShow info = new PjCmrSNInfoForShow();
					info.sn = allSn[i];
					info.name = tmpMap.get(info.sn);
					mapSN.put(info.sn, info);
				}
				tmpMap.clear();
			}
			
			//获得当天的时间点
			actionDate = Utils.isEmpty(date)?DateProcess.toString(new Date(), "yyyy-MM-dd"):date;
			long timeStart = DateProcess.toDate(actionDate+" "+startTime, "yyyy-MM-dd HH:mm").getTime();
			long timeEnd = DateProcess.toDate(actionDate+" "+endTime, "yyyy-MM-dd HH:mm").getTime();
			
			//首先得到这些相机的根据sn汇总的数据
			String sql = "select sn,sum(din),sum(dout) from TCameraData where (time between "+timeStart+" and "+timeEnd+") and sn in("+gnrSnInSql(allSn)+")";
			sql += " group by sn";
			ResultSet rs = jdbc.executeQuery(sql);
			while(rs.next()){
				String sn = rs.getString(1);
				int sumdin = rs.getInt(2);
				int sumdout = rs.getInt(3);
				PjCmrSNInfoForShow info = mapSN.get(sn);
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
//			if(this.sumOut>this.sumIn)
//				this.sumOut=this.sumIn;
			//找到sn最新的一张图片，缩略图
			if(!mapSN.isEmpty()){
				Iterator<String> iterator = mapSN.keySet().iterator();
				while(iterator.hasNext()){
					PjCmrSNInfoForShow info = mapSN.get(iterator.next());
					sql = "select picUrl from TCameraData where sn='"+info.sn+"' order by time desc limit 1";
					rs = jdbc.executeQuery(sql);
					if(rs.next()){
						String picUrl = rs.getString(1);
						if(!Utils.isEmpty(picUrl) && picUrl.endsWith(".jpg")){
							info.picOri = picUrl;
							info.picUrl = picUrl.substring(0, picUrl.length()-4)+"_min.jpg";
						}else {
							info.picOri = "";
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
	
	/**
	 * 当前时间，格式：MM月dd日HH时mm分
	 * @return
	 */
	public String nowCurTime(){
		return DateProcess.toString(new Date(), "MM月dd日HH时mm分");
	}
	
	/**
	 * 停留的
	 * @return
	 */
	public int sumStay(){
		int stay = sumIn-sumOut;
		if(stay<0)
			stay = 0;
		return stay;
	}
	

	public int sumIn() {
		return sumIn;
	}


	public int sumOut() {
		return sumOut;
	}
	
	/**
	 * 获得相机的具体信息
	 * @param sn
	 * @return
	 */
	public String cmrName(String sn){
		PjCmrSNInfoForShow info = mapSN.get(sn);
		if(info == null)
			return "";
		return info.name;
	}
	
	/**
	 * 某个相机的进入数据
	 * @param sn
	 * @return
	 */
	public int cmrSumIn(String sn){
		PjCmrSNInfoForShow info = mapSN.get(sn);
		if(info == null)
			return 0;
		return info.inSum;
	}
	
	/**
	 * 某个相机的离开数据
	 * @param sn
	 * @return
	 */
	public int cmrSumOut(String sn){
		PjCmrSNInfoForShow info = mapSN.get(sn);
		if(info == null)
			return 0;
		return info.outSum;
	}
	
	/**
	 * 拿到相机的最新的图片
	 * @param sn
	 * @return
	 */
	public String cmrPicUrl(String sn){
		PjCmrSNInfoForShow info = mapSN.get(sn);
		if(info == null)
			return "";
		return info.picUrl;
	}
	
	/**
	 * 拿到相机的最新原始图片s
	 * @param sn
	 * @return
	 */
	public String cmrPicOri(String sn){
		PjCmrSNInfoForShow info = mapSN.get(sn);
		if(info == null)
			return "";
		return info.picOri;
	}
	
	/**
	 * 相机的序号
	 * @param no
	 * @return
	 */
	public String cmrSN(int no){
		return allSn[no];
	}

	/**
	 * 该景点相机的数量
	 * @return
	 */
	public int cmrCount(){
		return allSn.length;
	}
	
	/**
	 * 某个相机是否允许视频直播
	 * @param sn
	 * @return
	 */
	public boolean cmrAllLive(String sn){
		return MyStringUtil.isInArray(liveSn, sn);
	}


	public int getSpotNo() {
		return spotNo;
	}

	public void setSpotNo(int spotNo) {
		this.spotNo = spotNo;
	}

	public String getSpotName() {
		return spotName;
	}

	public void setSpotName(String spotName) {
		this.spotName = spotName;
	}

	public String getSpotMark() {
		return spotMark;
	}

	public void setSpotMark(String spotMark) {
		this.spotMark = spotMark;
	}

	public String[] getAllSn() {
		return allSn;
	}

	public void setAllSn(String[] allSn) {
		this.allSn = allSn;
	}

	public String[] getInSn() {
		return inSn;
	}

	public void setInSn(String[] inSn) {
		this.inSn = inSn;
	}

	public String[] getOutSn() {
		return outSn;
	}

	public void setOutSn(String[] outSn) {
		this.outSn = outSn;
	}

	public String[] getLiveSn() {
		return liveSn;
	}

	public void setLiveSn(String[] liveSn) {
		this.liveSn = liveSn;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("景点序号："+spotNo);
		sb.append("\n景点名称："+spotName);
		sb.append("\n景点备注："+spotMark);
		sb.append("\n全部相机："+MyStringUtil.arrayToString(allSn, ","));
		sb.append("\n统计进入相机："+MyStringUtil.arrayToString(inSn, ","));
		sb.append("\n统计离开相机："+MyStringUtil.arrayToString(outSn, ","));
		sb.append("\n允许视频相机："+MyStringUtil.arrayToString(liveSn, ","));
		sb.append("\n统计时间段："+startTime+" ~ "+endTime);
		return sb.toString();
	}
	
}
