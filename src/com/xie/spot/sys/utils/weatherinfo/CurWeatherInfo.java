package com.xie.spot.sys.utils.weatherinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import ssin.util.DateProcess;

import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.HttpCall;

/**
 * 获取当前的天气信息，
 * 目前通过小米的连接
 * String url = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=101210101";
 * @author IcekingT420
 *
 */
public class CurWeatherInfo {
	/**
	 * 天气代码，一般是城市的
	 */
	private String wcode;
	
	/**
	 * 请求得到的结果，一般是json格式的字符
	 */
	private String result;
	
	/**
	 * 今天的日期
	 */
	private String today;
	
	/**
	 * 以下几项是解析过来的
	 */
	private WRealTime realTime;
	
	private WAirQuality airQuality;
	
	private WOneday onedayToday;
	
	private WOneday onedayYestoday;
	
	private List<WForecastDay> forecastDays;

	public CurWeatherInfo(String wcode) {
		this.wcode = wcode;
		today = DateProcess.toString(new Date(), "yyyy-MM-dd");
	}
	
	/**
	 * 远程获取
	 */
	public void fetch(){
		String url = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId="+wcode;
		HttpCall httpCall = new HttpCall(url);
		result = httpCall.call();
		parseResult();
	}
	
	/**
	 * 直接通过一个json字符来解析
	 * @param result
	 */
	public void fromResult(String result){
		this.result = result;
		parseResult();
	}
	
	public boolean isValid(){
		if(realTime == null || airQuality == null)
			return false;
		if(realTime.getTimeStamp()==0)
			return false;
		return true;
	}
	
	private void parseResult(){
		JSONObject jsonObject = JSONObject.fromObject(result);
		if(jsonObject == null)
			return;
		if(jsonObject.get("error") != null)
			return;
		JSONObject jsonTmp = jsonObject.getJSONObject("realtime");
		if(jsonTmp != null){
			String cityid = jsonTmp.getString("cityid");
			if(Utils.isEmpty(cityid) || !cityid.equals(wcode))
				return;
			realTime = new WRealTime();
			String str = jsonTmp.getString("SD");
			if(str!=null){
				realTime.setSd(Utils.parseInt(str.substring(0, str.length()-1)));
			}
			realTime.setWd(jsonTmp.getString("WD"));
			realTime.setWs(jsonTmp.getString("WS"));
			str = jsonTmp.getString("temp");
			if(str != null){
				realTime.setTemp(Utils.parseInt(str));
			}
			str = jsonTmp.getString("time");
			if(str != null){
				realTime.setTime(str);
				Date tmpDate = DateProcess.toDate(today+" "+str, "yyyy-MM-dd HH:mm");
				if(tmpDate != null)
					realTime.setTimeStamp(tmpDate.getTime());
			}
			realTime.setWeather(jsonTmp.getString("weather"));
		}
		
		jsonTmp = jsonObject.getJSONObject("aqi");
		if(jsonTmp != null){
			airQuality = new WAirQuality();
			airQuality.setPubTime(jsonTmp.getString("pub_time"));
			String str = jsonTmp.getString("aqi");
			if(str != null){
				airQuality.setAqi(Utils.parseInt(str));
			}
			str = jsonTmp.getString("pm25");
			if(str != null){
				airQuality.setPm25(Utils.parseInt(str));
			}
			str = jsonTmp.getString("pm10");
			if(str != null){
				airQuality.setPm10(Utils.parseInt(str));
			}
			str = jsonTmp.getString("so2");
			if(str != null){
				airQuality.setSo2(Utils.parseInt(str));
			}
			str = jsonTmp.getString("no2");
			if(str != null){
				airQuality.setNo2(Utils.parseInt(str));
			}
		}
		
		jsonTmp = jsonObject.getJSONObject("today");
		if(jsonTmp != null){
			onedayToday = new WOneday();
			onedayToday.setDate(jsonTmp.getString("date"));
			String str = jsonTmp.getString("humidityMax");
			if(str != null){
				onedayToday.setHumidityMax(Utils.parseInt(str));
			}
			str = jsonTmp.getString("humidityMin");
			if(str != null){
				onedayToday.setHumidityMin(Utils.parseInt(str));
			}
			str = jsonTmp.getString("tempMax");
			if(str != null){
				onedayToday.setTempMax(Utils.parseInt(str));
			}
			str = jsonTmp.getString("tempMin");
			if(str != null){
				onedayToday.setTempMin(Utils.parseInt(str));
			}
			onedayToday.setWeatherEnd(jsonTmp.getString("weatherEnd"));
			onedayToday.setWeatherStart(jsonTmp.getString("weatherStart"));
			onedayToday.setWindDirectionEnd(jsonTmp.getString("windDirectionEnd"));
			onedayToday.setWindDirectionStart(jsonTmp.getString("windDirectionStart"));
			str = jsonTmp.getString("windMax");
			if(str != null){
				onedayToday.setWindMax(Utils.parseInt(str));
			}
			str = jsonTmp.getString("windMin");
			if(str != null){
				onedayToday.setWindMin(Utils.parseInt(str));
			}
		}
		
		jsonTmp = jsonObject.getJSONObject("yestoday");
		if(jsonTmp != null){
			onedayYestoday = new WOneday();
			onedayYestoday.setDate(jsonTmp.getString("date"));
			String str = jsonTmp.getString("humidityMax");
			if(str != null){
				onedayYestoday.setHumidityMax(Utils.parseInt(str));
			}
			str = jsonTmp.getString("humidityMin");
			if(str != null){
				onedayYestoday.setHumidityMin(Utils.parseInt(str));
			}
			str = jsonTmp.getString("tempMax");
			if(str != null){
				onedayYestoday.setTempMax(Utils.parseInt(str));
			}
			str = jsonTmp.getString("tempMin");
			if(str != null){
				onedayYestoday.setTempMin(Utils.parseInt(str));
			}
			onedayYestoday.setWeatherEnd(jsonTmp.getString("weatherEnd"));
			onedayYestoday.setWeatherStart(jsonTmp.getString("weatherStart"));
			onedayYestoday.setWindDirectionEnd(jsonTmp.getString("windDirectionEnd"));
			onedayYestoday.setWindDirectionStart(jsonTmp.getString("windDirectionStart"));
			str = jsonTmp.getString("windMax");
			if(str != null){
				onedayYestoday.setWindMax(Utils.parseInt(str));
			}
			str = jsonTmp.getString("windMin");
			if(str != null){
				onedayYestoday.setWindMin(Utils.parseInt(str));
			}
		}
		
		jsonTmp = jsonObject.getJSONObject("forecast");
		if(jsonTmp != null){
			forecastDays = new ArrayList<WForecastDay>();
			Date startDate = DateProcess.toDate(jsonTmp.getString("date_y"), "yyyy年MM月dd日");
			if(startDate == null)
				startDate = DateProcess.toDate(today, "yyyy-MM-dd");
			
			//预计6天的
			for(int i=1;i<=6;i++){
				String strtemp = jsonTmp.getString("temp"+i);
				String strweather = jsonTmp.getString("weather"+i);
				String strwind = jsonTmp.getString("wind"+i);
				//三者有一个就构造
				if(strtemp!=null || strweather!=null || strwind!=null){
					WForecastDay forecastDay = new WForecastDay();
					forecastDay.setDate(DateProcess.toString(startDate, "yyyy-MM-dd"));
					forecastDay.setTemp(strtemp);
					forecastDay.setWeather(strweather);
					forecastDay.setWind(strwind);
					
					forecastDays.add(forecastDay);
					
					//日期变成明天的
					startDate = DateProcess.getTommorrow(startDate);
				}
			}
		}
	}

	public String getWcode() {
		return wcode;
	}

	public String getResult() {
		return result;
	}

	public String getToday() {
		return today;
	}

	public WRealTime getRealTime() {
		return realTime;
	}

	public WAirQuality getAirQuality() {
		return airQuality;
	}

	public WOneday getOnedayToday() {
		return onedayToday;
	}

	public WOneday getOnedayYestoday() {
		return onedayYestoday;
	}

	public List<WForecastDay> getForecastDays() {
		return forecastDays;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("天气信息：[");
		sb.append("天气代码="+wcode);
		sb.append(", 查询日期="+today);
		if(realTime != null){
			sb.append(", "+realTime.toString());
		}
		if(airQuality != null){
			sb.append(", "+airQuality.toString());
		}
		if(onedayToday != null){
			sb.append(", "+onedayToday.toString());
		}
		if(onedayYestoday != null){
			sb.append(", "+onedayYestoday.toString());
		}
		if(!Utils.isEmpty(forecastDays)){
			sb.append(", 预计 "+forecastDays.size()+" 天天气：[");
			for(WForecastDay day: forecastDays){
				sb.append("  "+day.toString());
			}
			sb.append("]");
		}
		sb.append("]");
		return sb.toString();
	}
	
}
