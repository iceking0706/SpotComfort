package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.xie.spot.sys.utils.CurIpLocation;
import com.xie.spot.sys.utils.HttpCall;

public class TestHttp {
	public static void test1(){
		try {
			//String url = "http://www.weather.com.cn/data/sk/101210101.html";
			//String url = "http://weather.123.duba.net/static/weather_info/101210101.html";
			//小米
			//String url = "http://weatherapi.market.xiaomi.com/wtr-v2/weather?cityId=101210101";
			//中央天气预报
			//String url = "http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode=101210101&weatherType=1";
			
			//中华万年历
			//String url = "http://wthrcdn.etouch.cn/weather_mini?citykey=101210101";
			
			//查询ip地址归属地
			String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=jsonp";
			
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				String cntString = convertStreamToString(entity.getContent());
				System.out.println(cntString);
				//get.abort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void testWeixin(){
		try {
			String url = "http://115.159.46.95:8080/sc/wxcity";
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("city", "杭州"));
			post.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				String cntString = convertStreamToString(entity.getContent());
				System.out.println(cntString);
				//get.abort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String convertStreamToString(InputStream is) {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
        StringBuilder sb = new StringBuilder();      
       
        String line = null;      
        try {      
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");      
            }      
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               e.printStackTrace();      
            }      
        }      
        return sb.toString();      
    }  
	
	public static void main(String[] args) {
//		HttpCall call = new HttpCall("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json");
//		System.out.println(call.call());
//		CurIpLocation curIpLocation = new CurIpLocation();
//		curIpLocation.fetch();
//		System.out.println(curIpLocation.showFullName());
		
		testWeixin();
	}
}
