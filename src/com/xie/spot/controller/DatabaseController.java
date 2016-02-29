package com.xie.spot.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ssin.util.DateProcess;

import com.xie.spot.entity.User;
import com.xie.spot.pojo.ComparatorPjFileInfo;
import com.xie.spot.pojo.FileExtFilter;
import com.xie.spot.pojo.PjFileInfo;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JDBC;
import com.xie.spot.sys.utils.JsonResult;

/**
 * 数据库备份迁移等的操作，专门放到这个controller里面来
 * @author IcekingT420
 *
 */
@Controller
public class DatabaseController {
	/**
	 * 数据库备份
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/doMysqlDump",produces="application/json;charset=utf-8")
	@ResponseBody
	public String doMysqlDump(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		File file = Utils.doMysqlDump();
		if(file == null){
			JsonResult jsonResult = new JsonResult(false,"do mysql dump fail.");
			return jsonResult.toString();
		}
		return new JsonResult(true,file.getName()).toString();
	}
	
	/**
	 * 列出已经备份的数据库文件
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/listMysqlDumpFiles",produces="application/json;charset=utf-8")
	@ResponseBody
	public String listMysqlDumpFiles(HttpServletRequest request){
		if(request.getParameter("fistTime") != null && request.getParameter("fistTime").equals("true"))
			return "{}";
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		//找到备份目录下面的所有.sql文件
		File[] files = Utils.getMysqlBackupDir().listFiles(new FileExtFilter(new String[]{".sql",".his",".tmp",".txt"}));
		if(files == null || files.length == 0){
			JsonResult jsonResult = new JsonResult(false,"No sql files found.");
			return jsonResult.toString();
		}
		List<PjFileInfo> list = new ArrayList<PjFileInfo>();
		for(File tmp: files){
			list.add(new PjFileInfo(tmp));
		}
		
		//根据时间排序
		Collections.sort(list, new ComparatorPjFileInfo());
		
		JsonResult jsonResult = new JsonResult(true);
		jsonResult.putTotal(list.size());
		jsonResult.putRows(list);
		
		return jsonResult.toString();
	}
	
	@RequestMapping(value="/deleteMysqlDumpFile",produces="application/json;charset=utf-8")
	@ResponseBody
	public String deleteMysqlDumpFile(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		String fileName = request.getParameter("fileName");
		File file = new File(Utils.getMysqlBackupDir(),fileName);
		if(file == null || !file.exists()){
			JsonResult jsonResult = new JsonResult(false,"file not exist. "+file.getPath());
			return jsonResult.toString();
		}
		if(!file.delete()){
			JsonResult jsonResult = new JsonResult(false,"file delete false. "+file.getPath());
			return jsonResult.toString();
		}
		return new JsonResult(true).toString();
	}
	
	/**
	 * 分析 TCameraData 的历史数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/analyHisData",produces="application/json;charset=utf-8")
	@ResponseBody
	public String analyHisData(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		String endTime = request.getParameter("endTime");
		if(Utils.isEmpty(endTime)){
			JsonResult jsonResult = new JsonResult(false,"缺少必要参数 endTime");
			return jsonResult.toString();
		}
		Date endDate = DateProcess.toDate(endTime+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
		if(endDate==null){
			JsonResult jsonResult = new JsonResult(false,"日期格式不正确: "+endTime);
			return jsonResult.toString();
		}
		try {
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			String sql = "select count(*) from TCameraData where time<="+endDate.getTime();
			long count = 0;
			ResultSet rs = jdbc.executeQuery(sql);
			if(rs.next()){
				count = rs.getLong(1);
			}
			rs.close();
			jdbc.stopConnection();
			return new JsonResult(true,""+count).toString();
		} catch (Exception e) {
			e.printStackTrace();
			JsonResult jsonResult = new JsonResult(false,"数据库操作异常: "+e.getMessage());
			return jsonResult.toString();
		}
	}
	
	/**
	 * 迁移到历史文件
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/transHisData",produces="application/json;charset=utf-8")
	@ResponseBody
	public String transHisData(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		String endTime = request.getParameter("endTime");
		if(Utils.isEmpty(endTime)){
			JsonResult jsonResult = new JsonResult(false,"缺少必要参数 endTime");
			return jsonResult.toString();
		}
		Date endDate = DateProcess.toDate(endTime+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
		if(endDate==null){
			JsonResult jsonResult = new JsonResult(false,"日期格式不正确: "+endTime);
			return jsonResult.toString();
		}
		
		//有数据，开始迁移，一个数据文件，一个删除的文件
		long nowTime = System.currentTimeMillis();
		File hisFile = new File(Utils.getMysqlBackupDir(),"his-bf-"+endTime+"-"+nowTime+".his");
		File delTmpFile = new File(Utils.getMysqlBackupDir(),"deltmp-bf-"+endTime+"-"+nowTime+".tmp");
		
		try {
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			String sql = "select count(*) from TCameraData where time<="+endDate.getTime();
			long total = 0;
			ResultSet rs = jdbc.executeQuery(sql);
			if(rs.next()){
				total = rs.getLong(1);
			}
			rs.close();
			if(total==0){
				jdbc.stopConnection();
				hisFile.delete();
				delTmpFile.delete();
				return new JsonResult(false,"没有需要迁移的数据").toString();
			}
			
			if(!hisFile.createNewFile() || !delTmpFile.createNewFile()){
				jdbc.stopConnection();
				return new JsonResult(false,"备份文件创建失败").toString();
			}
			
			//写入流
			BufferedWriter bwHis = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hisFile, true),"utf-8"));
			bwHis.write("//// TCameraData backup. Before date: "+endTime+". Total: "+total);
			bwHis.newLine();
			bwHis.write("id,din,dout,picUrl,sn,time,recvIOTime,recvPicTime,ioRawData");
			BufferedWriter bwDel = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(delTmpFile, true),"utf-8"));
			bwDel.write("//// TCameraData delete tmp. Before date: "+endTime+". Total: "+total);
			//查询语句每2000条一次
			jdbc.startTransaction();
			while(true){
				String sqlHis = "select id,din,dout,picUrl,sn,time,recvIOTime,recvPicTime,ioRawData from TCameraData where time<="+endDate.getTime()+" limit 2000";
				ResultSet rsHis = jdbc.executeQuery(sqlHis);
				int curCount = 0;
				while(rsHis.next()){
					curCount++;
					long id = rsHis.getLong(1);
					int din = rsHis.getInt(2);
					int dout = rsHis.getInt(3);
					String picUrl = rsHis.getString(4);
					String sn = rsHis.getString(5);
					long time = rsHis.getLong(6);
					long recvIOTime = rsHis.getLong(7);
					long recvPicTime = rsHis.getLong(8);
					String ioRawData = rsHis.getString(9);
					
					//写历史文件
					String lineHis = id+","+din+","+dout+","+picUrl+","+sn+","+time+","+recvIOTime+","+recvPicTime+","+ioRawData;
					bwHis.newLine();
					bwHis.write(lineHis);
					
					//写删除文件
					String lineDel = "delete from TCameraData where id="+id;
					bwDel.newLine();
					bwDel.write(lineDel);
					
					//执行数据库的删除
					jdbc.executeUpdate(lineDel);
				}
				rsHis.close();
				
				//查询没有结果了，就退出循环
				if(curCount==0)
					break;
				
				//文件flush
				bwDel.flush();
				bwHis.flush();
				
			}
			jdbc.commit();
			jdbc.stopTransaction();
			jdbc.stopConnection();
			
			bwDel.close();
			bwHis.close();
			
			return new JsonResult(true).toString();
		} catch (Exception e) {
			hisFile.delete();
			delTmpFile.delete();
			e.printStackTrace();
			JsonResult jsonResult = new JsonResult(false,"数据库操作异常: "+e.getMessage());
			return jsonResult.toString();
		}
	}
	
	/**
	 * 按照日期将相机数据导出到文本
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/outputCmrData",produces="application/json;charset=utf-8")
	@ResponseBody
	public String outputCmrData(HttpServletRequest request){
		User loginUser = (User)request.getSession().getAttribute("loginUser");
		if(loginUser == null || !loginUser.isSuperAdmin()){
			return new JsonResult(false,"缺少当前管理帐号的登入信息").toString();
		}
		String startTime = Utils.getParamValue(request, "startTime");
		long startLL = 0;
		if (!Utils.isEmpty(startTime)) {
			Date dateS = DateProcess.toDate(startTime+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			if (dateS != null)
				startLL = dateS.getTime();
		}
		if(startLL == 0){
			return new JsonResult(false,"开始日期不正确").toString();
		}
		String endTime = Utils.getParamValue(request, "endTime");
		long endLL = 0;
		if (!Utils.isEmpty(endTime)) {
			Date dateS = DateProcess.toDate(endTime+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			if (dateS != null)
				endLL = dateS.getTime();
		}
		if(endLL == 0){
			return new JsonResult(false,"结束日期不正确").toString();
		}
		
		long nowTime = System.currentTimeMillis();
		//导出的文件
		File fileOtPt = new File(Utils.getMysqlBackupDir(),"output-btw-"+startTime+"-"+endTime+"-"+nowTime+".txt");
		
		try {
			if(!fileOtPt.createNewFile()){
				return new JsonResult(false,"导出文件创建失败. "+fileOtPt.getPath()).toString();
			}
			
			JDBC jdbc = JDBC.newOne();
			jdbc.startConnection();
			//先找出全部的相机基本数据，有效的
			//key=sn, value=mark
			Map<String, String> mapCmr = new HashMap<String, String>();
			String sql = "select sn,mark from TCameraCfg";
			ResultSet rs = jdbc.executeQuery(sql);
			while(rs.next()){
				mapCmr.put(rs.getString(1), rs.getString(2));
			}
			rs.close();
			if(mapCmr.isEmpty()){
				jdbc.stopConnection();
				return new JsonResult(false,"没有相机数据").toString();
			}
			
			BufferedWriter bwHis = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOtPt, true),"utf-8"));
			bwHis.write("//// TCameraData output. Between date: "+startTime+" to "+endTime);
			//先把相机sn和名称打印出来
			Iterator<String> iterator = mapCmr.keySet().iterator();
			while(iterator.hasNext()){
				String sn = iterator.next();
				bwHis.newLine();
				bwHis.write(sn+"="+mapCmr.get(sn));
			}
			bwHis.newLine();
			bwHis.write("//// Data information.");
			bwHis.newLine();
			bwHis.write("id,din,dout,picUrl,sn,time,timeStr,recvIOTime,recvPicTime,ioRawData");
			
				String sqlHis = "select id,din,dout,picUrl,sn,time,recvIOTime,recvPicTime,ioRawData from TCameraData where time between "+startLL+" and "+endLL;
				ResultSet rsHis = jdbc.executeQuery(sqlHis);
				int curCount = 0;
				while(rsHis.next()){
					curCount++;
					long id = rsHis.getLong(1);
					int din = rsHis.getInt(2);
					int dout = rsHis.getInt(3);
					String picUrl = rsHis.getString(4);
					String sn = rsHis.getString(5);
					long time = rsHis.getLong(6);
					long recvIOTime = rsHis.getLong(7);
					long recvPicTime = rsHis.getLong(8);
					String ioRawData = rsHis.getString(9);
					
					//写历史文件
					String lineHis = id+","+din+","+dout+","+picUrl+","+sn+","+time+","+(DateProcess.toString(new Date(time), "yyyy-MM-dd HH:mm:ss"))+","+recvIOTime+","+recvPicTime+","+ioRawData;
					bwHis.newLine();
					bwHis.write(lineHis);
					if(curCount % 1000 == 0)
						bwHis.flush();
				}
				rsHis.close();
				
				
				
				bwHis.flush();
			
			
			bwHis.close();
			jdbc.stopConnection();
			
			if(curCount==0)
				fileOtPt.delete();
			
			return new JsonResult(true,""+curCount).toString();
		} catch (Exception e) {
			fileOtPt.delete();
			e.printStackTrace();
			JsonResult jsonResult = new JsonResult(false,"数据库操作异常: "+e.getMessage());
			return jsonResult.toString();
		}
		
	}
}
