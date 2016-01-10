package test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.data.domain.Page;

import ssin.util.DateProcess;
import ssin.util.MyConverter;
import ssin.util.MyStringUtil;

import com.eg.intf3.security.ByteUtils;
import com.suyou.singnalway.RecvInOutData;
import com.xie.spot.entity.CameraCfg;
import com.xie.spot.entity.City;
import com.xie.spot.entity.CodeWeather;
import com.xie.spot.pojo.PjNameValue;
import com.xie.spot.pojo.PjSpotComfortCalcuResult;
import com.xie.spot.pojo.wechat.PjCitySpotC;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JsonResult;
import com.xie.spot.sys.utils.weatherinfo.CurWeatherInfo;

public class Test1 {
	public static void toUtf8(){
		try {
			String str = "浙江";
			System.out.println(new String(new byte[]{0x6d,0x59,0x6c,0x5f},"utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void test33(){
		int startHour = 8;
		int endHour = 20;
		Date nowDate = new Date();
		System.out.println(nowDate.getHours());
	}
	
	public static void test44(){
		String str1 = "20150724130000";
		String str2 = "20150724133000";
		long startTime = DateProcess.toDate(str1, "yyyyMMddHHmmss").getTime();
		long endTime = DateProcess.toDate(str2, "yyyyMMddHHmmss").getTime();
		System.out.println(startTime);
		System.out.println(endTime);
		
		//阿里云上填补时间的url，必须登入以后的
		String sn = "VECAM-D01-LS14024933";
		String url = "http://120.26.108.57:8888/sc/tianbuCmrOnedayLostIO?sn="+sn+"&startTime="+startTime+"&endTime="+endTime;
		System.out.println(url);
		
	}
	
	/**
	 * 测试蒋涛接口，/cmr/dvsdata
	 */
	public static void test55(){
		String str1 = "20150724130000";
		String str2 = "20150724133000";
		long startTime = DateProcess.toDate(str1, "yyyyMMddHHmmss").getTime();
		long endTime = DateProcess.toDate(str2, "yyyyMMddHHmmss").getTime();
		String sn = "VECAM-D01-LS14024933";
		String url = "http://120.26.108.57:8888/sc/cmr/dvsdata?sn="+sn+"&timeS="+startTime+"&timeE="+endTime+"&count=100";
		System.out.println(url);
	}
	
	public static void main(String[] args) {
		String picOri = "uploadfiles/cameraPics/7_1451901028954_75582_1920_1080.jpg";
		String picFileName = picOri.substring(picOri.lastIndexOf('/')+1,picOri.lastIndexOf('.'));
		System.out.println(picFileName);
	}
	
	
	
	
}
