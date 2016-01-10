package com.xie.spot.sys.utils;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xie.spot.sys.Utils;




/**
 * 直连数据库
 * @author IcekingT420
 *
 */
public class JDBC {
	private static final Logger logger = LoggerFactory.getLogger(JDBC.class);
	/**
	 * 第一次去生成的时候，使用连接
	 */
	private static String defaultDbDriver = null;
	private static String defaultDbUrl = null;
	private static String defaultDbUser = null;
	private static String defaultDbPass = null;
	
	/**
	 * 以下是一些静态操作，辅助性质的
	 * 获取默认的一个jdbc对象
	 * @return
	 */
	public static JDBC newOne(){
		if(defaultDbDriver == null){
			//去获得几项内容
			if(Utils.getWebAppRootDir() == null){
				defaultDbDriver = "com.mysql.jdbc.Driver";
				defaultDbUrl = "jdbc:mysql://localhost:3306/spotcomfort?useUnicode=true&characterEncoding=utf8&reConnect=true";
				defaultDbUser = "root";
				defaultDbPass = "onecard";
			}else{
				try {
					FileInputStream fis = new FileInputStream(new File(Utils.getWebAppRootDir()+"/WEB-INF/spring/database.properties"));
					Properties properties = new Properties();
					properties.load(fis);
					defaultDbDriver = properties.getProperty("database.driverClassName", "com.mysql.jdbc.Driver");
					String mysql5Ip = properties.getProperty("mysql5.ip", "localhost");
					String mysql5Port = properties.getProperty("mysql5.port", "3306");
					String mysql5DbName = properties.getProperty("mysql5.dbname", "spotcomfort");
					defaultDbUrl = "jdbc:mysql://"+mysql5Ip+":"+mysql5Port+"/"+mysql5DbName+"?useUnicode=true&characterEncoding=utf8&reConnect=true";
					//defaultDbUrl = properties.getProperty("database.url", "jdbc:mysql://localhost:3306/eglockcloud?useUnicode=true&characterEncoding=utf8&reConnect=true");
					defaultDbUser = properties.getProperty("database.username", "root");
					defaultDbPass = properties.getProperty("database.password", "onecard");
					fis.close();
				} catch (Exception e) {
					logger.error("JDBC.newOne error: "+e.getMessage(), e);
					defaultDbDriver = "com.mysql.jdbc.Driver";
					defaultDbUrl = "jdbc:mysql://localhost:3306/spotcomfort?useUnicode=true&characterEncoding=utf8&reConnect=true";
					defaultDbUser = "root";
					defaultDbPass = "onecard";
				}
			}
		}
		return new JDBC(defaultDbDriver, defaultDbUrl, defaultDbUser, defaultDbPass);
	}
	
	/**
	 * 启动之初，判断一下数据库 spotcomfort 是否存在，不存在的话创建
	 */
	public static void createDataBase(){
		try {
			JDBC jdbc = null;
			if(Utils.getWebAppRootDir() == null){
				jdbc = new JDBC("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/mysql", "root", "onecard");
			}else {
				FileInputStream fis = new FileInputStream(new File(Utils.getWebAppRootDir()+"/WEB-INF/spring/database.properties"));
				Properties properties = new Properties();
				properties.load(fis);
				String mysql5Ip = properties.getProperty("mysql5.ip", "localhost");
				String mysql5Port = properties.getProperty("mysql5.port", "3306");
				jdbc = new JDBC(properties.getProperty("database.driverClassName", "com.mysql.jdbc.Driver"), "jdbc:mysql://"+mysql5Ip+":"+mysql5Port+"/mysql", properties.getProperty("database.username", "root"), properties.getProperty("database.password", "onecard"));
			}
			jdbc.startConnection();
			//判断数据库是否存在
			String dbName = "spotcomfort";
			boolean dbExist = false;
			String sql = "show databases";
			ResultSet rs = jdbc.executeQuery(sql);
			while(rs.next()){
				if(rs.getString(1).equals(dbName)){
					dbExist = true;
					break;
				}
			}
			rs.close();
			if(dbExist){
				logger.info("Database "+dbName+" is already exist.");
				jdbc.stopConnection();
				return;
			}
			//开始创建新的数据库
			logger.info("Database "+dbName+" is not exist, start to create...");
			sql = "CREATE DATABASE "+dbName+";";
			jdbc.executeUpdate(sql);
			
			jdbc.stopConnection();
		} catch (Exception e) {
			logger.error("JDBC.createDataBaseEgLockCloud error: "+e.getMessage(), e);
		}
	}
	
	/**
	 * 得到数据库中的全部表的名字，如果有startWith，则名字从startWith开始
	 * @param jdbc
	 * @param startWith
	 * @return
	 */
	public static List<String> showAllTableName(JDBC jdbc,String startWith){
		List<String> list = new ArrayList<String>();
		try {
			String sql = "show tables";
			ResultSet rs = jdbc.executeQuery(sql);
			while(rs.next()){
				String tableName = rs.getString(1);
				if(startWith != null){
					if(tableName.startsWith(startWith))
						list.add(tableName);
				}else{
					list.add(tableName);
				}
			}
			rs.close();
		} catch (Exception e) {
			logger.error("JDBC.showAllTableName error: "+e.getMessage(), e);
			list.clear();
		}
		return list;
	}
	
	/**
	 * 获得一个新的历史数据表的名字，命名规则：
	 * this_原表名字_编号(1,2,3,4,5)
	 * @param jdbc
	 * @param byTableName
	 * @return
	 */
	public static String getNewHisTableName(JDBC jdbc,String byTableName){
		if(byTableName == null || byTableName.equals(""))
			return null;
		try {
			//获得这种类型的历史数据表
			List<String> list = showAllTableName(jdbc, "this_"+byTableName);
			if(list.size() == 0)
				return "this_"+byTableName+"_1";
			int maxNo = 0;
			for(String tn: list){
				int curNo = Integer.parseInt(tn.substring(tn.lastIndexOf('_')+1));
				if(curNo>maxNo)
					maxNo = curNo;
			}
			return "this_"+byTableName+"_"+(maxNo+1);
		} catch (Exception e) {
			logger.error("JDBC.getNewHisTableName error: "+e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 根据某个表，创建一个新的历史数据表
	 * @param jdbc
	 * @param byTableName
	 * @return
	 */
	public static boolean createNewHisTable(JDBC jdbc,String byTableName,String newTableName){
		String hisTableName = newTableName;
		if(hisTableName == null)
			hisTableName = getNewHisTableName(jdbc,byTableName);
		if(hisTableName == null){
			logger.error("can not generate new history table name by "+byTableName);
			return false;
		}
		try {
			String sql = "desc "+byTableName;
			ResultSet rs = jdbc.executeQuery(sql);
			String createSql = "CREATE TABLE "+hisTableName+" (";
			boolean first = true;
			while(rs.next()){
				if(!first)
					createSql+=",";
				String field = rs.getString("Field");
				String type = rs.getString("Type");
				createSql += field+" "+type;
				first = false;
			}
			rs.close();
			createSql += ")";
			jdbc.executeUpdate(createSql);
			return true;
		} catch (Exception e) {
			logger.error("JDBC.createNewHisTable error: "+e.getMessage(), e);
			return false;
		}
	}
	
	/**
	 * 返回 select count(*) 类型的结果 long
	 * @param jdbc
	 * @param sql
	 * @return
	 */
	public static long totalOrMax(JDBC jdbc,String sql){
		long total = 0l;
		try {
			ResultSet rs = jdbc.executeQuery(sql);
			if(rs.next()){
				total = rs.getLong(1);
			}
			rs.close();
		} catch (Exception e) {
			logger.error("JDBC.totalOrMax error: "+e.getMessage(), e);
			total = 0l;
		}
		return total;
	}
	
	/**
	 * 以下是数据的一些基本操作，对象内的
	 * 连接数据库的驱动、路径、用户名、密码
	 */
	private String dbDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPass;
	/**
	 * 每个JDBC对象包含一个数据库的连接
	 */
	private Connection conn;
	/**
	 * 每次结果集的获得条数
	 */
	private int fetchSize = 100;
	
	public JDBC(){
		
	}

	public JDBC(String dbDriver, String dbUrl, String dbUser, String dbPass) {
		this();
		this.dbDriver = dbDriver;
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPass = dbPass;
	}
	
	/**
	 * 初始化数据库连接，获得Connection对象
	 */
	public void startConnection() throws Exception{
		Class.forName(this.dbDriver);
		this.conn = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPass);
	}
	/**
	 * 关闭连接
	 * @throws Exception
	 */
	public void stopConnection() throws Exception{
		if(this.conn != null){
			this.conn.close();
			this.conn = null;
		}
	}

	public Connection getConnection() throws Exception {
		if(conn == null){
			startConnection();
		}
		return conn;
	}
	
	/**
	 * 执行数据库查询操作，select
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	public ResultSet executeQuery(String sql) throws Exception {
		if(conn == null){
			startConnection();
		}
		Statement tmpstmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		if(fetchSize>0)
			tmpstmt.setFetchSize(fetchSize);
		return tmpstmt.executeQuery(sql);
	}
	
	/**
	 * 执行数据库的更新，insert、update、delete
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public boolean executeUpdate(String sql) throws Exception {
		if(conn == null){
			startConnection();
		}
		Statement tmpstmt = conn.createStatement();
		int rstCount = tmpstmt.executeUpdate(sql);
		return rstCount>0;
	}
	
	/**
	 * 开启事务操作
	 * @throws Exception 
	 */
	public void startTransaction() throws Exception {
		if(conn == null){
			startConnection();
		}
		this.conn.setAutoCommit(false);
	}
	
	/**
	 * 关闭事务，事务开启后，必须手工关闭
	 * @throws Exception
	 */
	public void stopTransaction() throws Exception {
		//事务如果没有开启，就直接返回
		if(this.conn == null || this.conn.getAutoCommit())
			return;
		this.conn.setAutoCommit(true);
	}
	
	public void commit() throws Exception {
		// 事务如果没有开启，就直接返回
		if (this.conn == null || this.conn.getAutoCommit())
			return;
		this.conn.commit();
	}
	
	public void rollback() throws Exception {
		// 事务如果没有开启，就直接返回
		if (this.conn == null || this.conn.getAutoCommit())
			return;
		this.conn.rollback();
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}
	
}
