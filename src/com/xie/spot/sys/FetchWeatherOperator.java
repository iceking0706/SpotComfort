package com.xie.spot.sys;

import com.xie.spot.sys.utils.weatherinfo.CurWeatherInfo;

/**
 * 定期去获得
 * @author IcekingT420
 *
 */
public class FetchWeatherOperator{
	public void doIt(){
		try {
			CurWeatherInfo weatherInfo = new CurWeatherInfo("101210101");
			weatherInfo.fetch();
			if(weatherInfo.isValid()){
				System.out.println(weatherInfo.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
