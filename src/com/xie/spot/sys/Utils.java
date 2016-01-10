package com.xie.spot.sys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xie.spot.entity.CodeWeather;

import ssin.util.DateProcess;

public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * tomcat的主目录
	 */
	private static File tomcatHome;
	
	/**
	 * tomcat的项目工程根目录
	 * 登入后可以获得
	 */
	private static File webAppRootDir = null;
	
	//上传数据临时存放的目录
	private static File tmpDir = null;
	
	//一些Excel模板的目录
	private static File templateDir = null;
	
	//mysql5数据库的备份目录，备份工具在tools下面
	private static File mysqlBackupDir = null;
	
	//上传的景点图片
	private static File spotPicsDir = null;
	
	//摄像头图片的存放路径
	private static File cameraPicsDir = null;
	
	//mysql5数据库的备份工具
	private static Mysql5Dump mysql5Dump = null;
	
	public static String db_ip;
	public static int db_port;
	public static String db_username;
	public static String db_password;
	
	/**
	 * 从系统的配置信息中得到数据库配置
	 */
	private static void getDbInfoFromProp(){
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream(new File(getWebAppRootDir(),"WEB-INF/spring/database.properties")));
			db_ip = prop.getProperty("mysql5.ip", "127.0.0.1");
			db_port = Integer.parseInt(prop.getProperty("mysql5.port", "3306"));
			db_username = prop.getProperty("database.username", "root");
			db_password = prop.getProperty("database.password", "suyou360");
			logger.info("db_ip="+db_ip);
			logger.info("db_port="+db_port);
			logger.info("db_username="+db_username);
			logger.info("db_password="+db_password);
		} catch (Exception e) {
			logger.error("getDbInfoFromProp error: "+e.getMessage(), e);
		}
	}
	
	
	public static File getWebAppRootDir() {
		return webAppRootDir;
	}
	

	public static File getTmpDir() {
		return tmpDir;
	}


	public static File getTemplateDir() {
		return templateDir;
	}

	public static File getMysqlBackupDir() {
		return mysqlBackupDir;
	}

	public static File getSpotPicsDir() {
		return spotPicsDir;
	}


	public static File getCameraPicsDir() {
		return cameraPicsDir;
	}


	/**
	 * 获得tomcat的主目录
	 * 在tomcat运行时，默认的 user.dir 是在bin目录下的
	 * @return
	 */
	public static File getTomcatHome() {
		if(tomcatHome == null){
//			String apcheTomcat6035 = "apache-tomcat-6.0.35";
//			int indexOfAT6 = tomcatHome.indexOf(apcheTomcat6035);
//			if(indexOfAT6 != -1){
//				tomcatHome = tomcatHome.substring(0, tomcatHome.length()-3);
//				tomcatHome = tomcatHome.replace('\\', '/');
//			}else {
//				tomcatHome = Utils.class.getResource("/").getPath();
//				System.out.println("resource.path="+tomcatHome);
//				tomcatHome = tomcatHome.substring(1,tomcatHome.indexOf(apcheTomcat6035)+apcheTomcat6035.length());
//			}
//			System.out.println("tomcatHome="+tomcatHome);
			
			//直接使用class的路径
			File clsRoot = new File(Utils.class.getResource("/").getPath());
			webAppRootDir = clsRoot.getParentFile().getParentFile();
			tomcatHome = webAppRootDir.getParentFile().getParentFile();
			
		}
		return tomcatHome;
	}
	
	/**
	 * 设置项目中常用的几个文件夹
	 */
	public static void setProjectCommonDir(){
		if(getTomcatHome()!=null && getTomcatHome().exists()){
			logger.info("Tomcat Dir: "+getTomcatHome().getPath());
			logger.info("Web project Dir: "+webAppRootDir.getPath());
			
			File dir2 = new File(webAppRootDir,"uploadfiles/tmpDir");
			if(dir2.exists() && dir2.isDirectory()){
				logger.info("Tmp file Dir: "+dir2.getPath());
				tmpDir = dir2;
			}
			File dir4 = new File(webAppRootDir,"uploadfiles/templateDir");
			if(dir4.exists() && dir4.isDirectory()){
				logger.info("Template file Dir: "+dir4.getPath());
				templateDir = dir4;
			}
			
			//mysql5数据库的备份目录
			File dir5 = new File(webAppRootDir,"uploadfiles/mysqlBackup");
			if(dir5.exists() && dir5.isDirectory()){
				logger.info("MysqlBackupDir file Dir: "+dir5.getPath());
				mysqlBackupDir = dir5;
			}
			
			//上传的景点图片的保存路径
			File dir6 = new File(webAppRootDir,"uploadfiles/spotPics");
			if(dir6.exists() && dir6.isDirectory()){
				logger.info("Spot Pictures file Dir: "+dir6.getPath());
				spotPicsDir = dir6;
			}
			
			//摄像头图片的保存路径
			File dir7 = new File(webAppRootDir,"uploadfiles/cameraPics");
			if(dir7.exists() && dir7.isDirectory()){
				logger.info("Camera Pictures file Dir: "+dir7.getPath());
				cameraPicsDir = dir7;
			}
			
			//读取配置文件信息
			getDbInfoFromProp();
		}
	}
	
	/**
	 * 本系统中数据库备份的对象，单例模式
	 * @return
	 */
	public static Mysql5Dump getMysql5Dump() {
		if(mysql5Dump == null){
			mysql5Dump = new Mysql5Dump(new File(mysqlBackupDir,"tools/mysqldump.exe"), mysqlBackupDir);
		}
		return mysql5Dump;
	}
	
	/**
	 * 执行数据库备份操作
	 * @return
	 */
	public static File doMysqlDump(){
		return getMysql5Dump().dump("spotcomfort");
	}


	/**
	 * 判断一下时间的格式
	 * @param time
	 * @param format
	 * @return
	 */
	public static boolean isTimeFormat(String timeStr,String format){
		if(timeStr == null || timeStr.equals(""))
			return false;
		Date date = null;
		if(format.startsWith("yyyy")){
			date = DateProcess.toDate(timeStr, format);
		}else if(format.startsWith("HH")){
			date = DateProcess.toDate("2014-01-01 "+timeStr, "yyyy-MM-dd "+format);
		}
		return date!=null;
	}
	
	public static boolean isEmpty(String str){
		if(str == null || str.equals(""))
			return true;
		return false;
	}
	
	public static boolean isEmpty(Object[] array){
		if(array==null || array.length==0)
			return true;
		return false;
	}
	
	public static boolean isEmpty(List<?> list){
		if(list == null || list.size() == 0)
			return true;
		return false;
	}
	
	public static boolean isEmpty(Map<?, ?> map){
		if(map==null || map.isEmpty())
			return true;
		return false;
	}
	
	public static boolean isEmptyId(Long id){
		if(id == null || id<=0)
			return true;
		return false;
	}
	
	public static boolean isEmpty(Integer ii){
		if(ii == null || ii<=0)
			return true;
		return false;
	}
	

	
	/**
	 * 读取文件的内容字节
	 * @param file
	 * @return
	 */
	public static byte[] readFileData(File file){
		if(!file.exists() || !file.isFile())
			return null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileInputStream fis = new FileInputStream(file);
			byte[] b = new byte[1024];
			int len = 0;
			while((len = fis.read(b)) != -1){
				baos.write(b, 0, len);
			}
			fis.close();
			byte[] fileData = baos.toByteArray();
			baos.close();
			return fileData;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 从request中获得参数，字符
	 * @param request
	 * @param paramName
	 * @return
	 */
	public static String getParamValue(HttpServletRequest request,String paramName){
		return request.getParameter(paramName);
	}
	
	public static Integer getParamValueInt(HttpServletRequest request,String paramName){
		String value = getParamValue(request,paramName);
		if(value == null)
			return null;
		try {
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Long getParamValueLong(HttpServletRequest request,String paramName){
		String value = getParamValue(request,paramName);
		if(value == null)
			return null;
		try {
			return Long.parseLong(value.trim());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Boolean getParamValueBoolean(HttpServletRequest request,String paramName){
		String value = getParamValue(request,paramName);
		if(value == null)
			return null;
		try {
			return Boolean.parseBoolean(value.trim());
		} catch (Exception e) {
			return null;
		}
	}
	
	
	
	/**
	 * 字符转整数，默认值
	 * @param str
	 * @param deftInt
	 * @return
	 */
	public static int parseInt(String str,int deftInt){
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return deftInt;
		}
	}
	
	/**
	 * 字符转整数，默认值=0
	 * @param str
	 * @return
	 */
	public static int parseInt(String str){
		return parseInt(str,0);
	}
	
	/**
	 * 字符转Long，默认值
	 * @param str
	 * @param deftLong
	 * @return
	 */
	public static long parseLong(String str,long deftLong){
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return deftLong;
		}
	}
	
	/**
	 * 字符转Long，默认0
	 * @param str
	 * @return
	 */
	public static long parseLong(String str){
		return parseLong(str, 0l);
	}
	
	public static double parseDouble(String str,double deftDouble){
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return deftDouble;
		}
	}
	
	public static double parseDouble(String str){
		return parseDouble(str,0d);
	}
	
	/**
	 * 获得boolean值，默认false
	 * @param str
	 * @return
	 */
	public static boolean parseBoolean(String str){
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 根据景点的流量来计算拥挤度
	 * @param flow
	 * @param maxCapacity
	 * @return
	 */
	public static int calculateCrowdDegreeByFlow(int flow,int maxCapacity){
		if(maxCapacity == 0)
			return 0;
		double f1 = Double.parseDouble(flow+"");
		double m1 = Double.parseDouble(maxCapacity+"");
		int crowdDegree = (int)((f1/(m1/12))*100);
		if(crowdDegree>100)
			crowdDegree = 100;
		return crowdDegree;
	}
	
	/**
	 * 通过景观等级计算得到景观分值
	 * @param viewLevel
	 * @return
	 */
	public static int calculateViewScore(Integer viewLevel){
		if(viewLevel==null)
			return 0;
		if(viewLevel.intValue() == 1)
			return 100;
		if(viewLevel.intValue() == 2)
			return 80;
		if(viewLevel.intValue() == 3)
			return 60;
		return 0;
	}
	
	/**
	 * 根据天气数据，计算当前的气象分值
	 * @param codeWeather
	 * @return
	 */
	public static int calculateWeatherScore(CodeWeather codeWeather){
		int score = 100;
		//不是晴天，扣10分
		if(!codeWeather.getWeather().equals("晴"))
			score = score-10;
		//温度和20度的差值每2度，扣5分
		int sub = codeWeather.getTemperature().intValue()-20;
		if(sub<0)
			sub = 0-sub;
		return score-(sub/2)*5;
	}
	
	/**
	 * 根据天气的中文，去匹配icons/icon128下面的图片
	 * @param weather
	 * @return
	 */
	public static String matchWeatherPic(String weather){
		String path = "icons/icon128/";
		String pic = "cloudy5.png";
		if(isEmpty(weather))
			return path + pic;
		if(weather.equals("晴")){
			pic = "sunny.png";
		}else if (weather.equals("多云")) {
			pic = "cloudy4.png";
		}else if (weather.equals("阴")) {
			pic = "overcast.png";
		}else if (weather.equals("阵雨")) {
			pic = "png-1343.png";
		}else if (weather.equals("霾")) {
			pic = "mist.png";
		}else if (weather.equals("大雨")) {
			pic = "png-1342.png";
		}else if (weather.equals("中雨")) {
			pic = "png-1341.png";
		}else if (weather.equals("雾")) {
			pic = "fog.png";
		}else if (weather.equals("小雨")) {
			pic = "png-1341.png";
		}else if (weather.equals("雷阵雨")) {
			pic = "png-1344.png";
		}else{
			pic = "cloudy5.png";
		}
		
		return path + pic;
	}
	
	/**
	 * 获得一个区间内的随机数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int gnrRandom1(int min,int max){
		Random random = new Random();
		return random.nextInt(max-min+1)+min;
	}
	
}
