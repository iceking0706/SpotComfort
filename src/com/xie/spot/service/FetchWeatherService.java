package com.xie.spot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.eg.intf3.security.ByteUtils;
import com.xie.spot.entity.City;
import com.xie.spot.entity.CodeWeather;
import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotComfort;
import com.xie.spot.entity.SpotPassengerFlow;
import com.xie.spot.pojo.PjSpotComfortCalcuResult;
import com.xie.spot.repository.CityRepository;
import com.xie.spot.repository.CodeWeatherRepository;
import com.xie.spot.repository.SpotBasicRepository;
import com.xie.spot.repository.SpotComfortRepository;
import com.xie.spot.repository.SpotPassengerFlowRepository;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.weatherinfo.CurWeatherInfo;

/**
 * 获取天气信息
 * @author IcekingT420
 *
 */
@Service
public class FetchWeatherService {
	private static final Logger logger = LoggerFactory.getLogger(FetchWeatherService.class);
	
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private SpotBasicRepository spotBasicRepository;
	@Autowired
	private CodeWeatherRepository codeWeatherRepository;
	@Autowired
	private SpotPassengerFlowRepository spotPassengerFlowRepository;
	@Autowired
	private SpotComfortRepository spotComfortRepository;
	
	/**
	 * 每个半小时获取天气信息
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	//@Scheduled(fixedDelay=1800000)
	public void fetchWeatherInfo(){
		logger.debug("start to fetch weather code...");
		//收集全部的天气代码，包括城市和景点的
		List<String> wcodeList = new ArrayList<String>();
		//城市的天气代码
		List<String> tmpList = cityRepository.listAllWcode();
		if(!Utils.isEmpty(tmpList)){
			while(!tmpList.isEmpty()){
				String strTmp = tmpList.remove(0);
				if(Utils.isEmpty(strTmp) || strTmp.equals("null"))
					continue;
				if(!wcodeList.contains(strTmp))
					wcodeList.add(strTmp);
			}
		}
		//景点特有的天气代码
		tmpList = spotBasicRepository.listAllWcode();
		if(!Utils.isEmpty(tmpList)){
			while(!tmpList.isEmpty()){
				String strTmp = tmpList.remove(0);
				if(Utils.isEmpty(strTmp) || strTmp.equals("null"))
					continue;
				if(!wcodeList.contains(strTmp))
					wcodeList.add(strTmp);
			}
		}
		
		if(Utils.isEmpty(wcodeList)){
			logger.debug("no weather code need to fetch");
			return;
		}
		
		for(String wcode: wcodeList){
			try {
				doWithOne(wcode);
			} catch (Exception e) {
				logger.error("doWithOne error, wcode="+wcode+", error="+e.getMessage(),e);
			}
		}
		
		wcodeList.clear();
		
		logger.debug("fetch weather code finished.");
		
		calAllSpotComfortInfo();
	}
	
	/**
	 * 计算全部景点的舒适度指数，存入到数据库表中去
	 * 目前放在天气计算的后面，每隔半小时一次
	 */
	public void calAllSpotComfortInfo(){
		logger.debug("start to calculate spot comfort degree index ...");
		//先找到全部的城市，已城市为单位进行统计
		List<City> cityList = cityRepository.findAll();
		if(Utils.isEmpty(cityList)){
			logger.debug("no city found.");
			return;
		}
		for(City city: cityList){
			//查询每个城市下面的景点
			List<SpotBasic> spotList = spotBasicRepository.findByCity(city);
			if(Utils.isEmpty(spotList))
				continue;
			for(SpotBasic spot: spotList){
				try {
					doWithOneSpot(spot);
				} catch (Exception e) {
					logger.error("doWithOneSpot error, spot="+spot.showFullName()+", error="+e.getMessage(),e);
				}
			}
			spotList.clear();
		}
		cityList.clear();
		logger.debug("calculate spot comfort degree index finished.");
	}
	
	/**
	 * 针对每个景点计算舒适度，并存入数据库
	 * @param spot
	 */
	public void doWithOneSpot(SpotBasic spot){
		//如果该景点的最大承载量未设置，则返回
		if(Utils.isEmpty(spot.getMaxCapacity()))
			return;
		//计算得到指数结果
		PjSpotComfortCalcuResult result = calculateSpotComfort(spot);
		//如果该结果的客流量没有，也退出
		if(result.getPsgrFlow()==0)
			return;
		//记录数据库
		SpotComfort entity = new SpotComfort();
		entity.setSpot(spot);
		entity.setComfortDegree(result.getComfortDegree());
		entity.setPsgrFlow(result.getPsgrFlow());
		entity.setPsgrScore(result.getPsgrScore());
		entity.setViewScore(result.getViewScore());
		entity.setWeatherScore(result.getWeatherScore());
		
		spotComfortRepository.save(entity);
	}
	
	/**
	 * 根据一个天气代码来获得
	 * @param wcode
	 */
	private void doWithOne(String wcode){
		logger.debug("start to fetch: "+wcode);
		CurWeatherInfo info = new CurWeatherInfo(wcode);
		info.fetch();
		if(!info.isValid()){
			logger.debug("fetch result is not valid. "+wcode);
			return;
		}
		//得到有效的数据
		//判断这个时间点是否已经获取过了
		CodeWeather entity = codeWeatherRepository.findByWcodeAndJsonTime(wcode, info.getRealTime().getTimeStamp());
		if(entity != null){
			logger.debug("already had weather data on timeStamp: "+info.getToday()+" "+info.getRealTime().getTime()+" of wcode: "+wcode);
			return;
		}
		
		//新数据，存放数据库
		entity = new CodeWeather();
		entity.setWcode(wcode);
		entity.setWeather(info.getRealTime().getWeather());
		entity.setTemperature(info.getRealTime().getTemp());
		entity.setHumidity(info.getRealTime().getSd());
		entity.setPm25(info.getAirQuality().getPm25());
		entity.setAqi(info.getAirQuality().getAqi());
		entity.setJsonTime(info.getRealTime().getTimeStamp());
		//原始的json数据使用16进制的封装
		String str16hex = ByteUtils.byteArrayToHex(info.getResult().getBytes());
		entity.setJsonData(str16hex);
		
		codeWeatherRepository.save(entity);
	}
	
	/**
	 * 计算一个景点当前的舒适度指数
	 * @param spot
	 * @return
	 */
	@Transactional
	public PjSpotComfortCalcuResult calculateSpotComfort(SpotBasic spot){
		PjSpotComfortCalcuResult result = new PjSpotComfortCalcuResult();
		result.setSpot(spot);
		
		//找到最近一条的景点客流拥挤度
		result.setSpotFullName(spot.showFullName());
		
		//获得客流评分
		Page<SpotPassengerFlow> pageSPF = spotPassengerFlowRepository.findBySpotOrderByTimeDesc(spot, new PageRequest(0, 1));
		if(pageSPF!=null && !Utils.isEmpty(pageSPF.getContent())){
			result.setPsgrFlow(pageSPF.getContent().get(0).getFlow());
			result.setPsgrScore(100-pageSPF.getContent().get(0).getCrowdDegree());
		}
		
		//获得景观评分
		result.setViewScore(Utils.calculateViewScore(spot.getViewLevel()));
		
		//获得气象评分
		Page<CodeWeather> pageCW = codeWeatherRepository.findByWcodeOrderByJsonTimeDesc(spot.getWeatherCode(), new PageRequest(0, 1));
		if(pageCW!=null && !Utils.isEmpty(pageCW.getContent())){
			result.setWeatherScore(Utils.calculateWeatherScore(pageCW.getContent().get(0)));
		}
		
		return result;
	}
	
	/**
	 * 景点模拟的舒适度，保存在map中
	 * 两个小时之内不改变
	 */
	private Map<Long, SpotComfort> simuSCMap = new HashMap<Long, SpotComfort>();
	
	/**
	 * 模拟出一个景点的舒适度指数
	 * 不存入数据库的
	 * @param spot
	 * @return
	 */
	public SpotComfort simulateSpotComfort(SpotBasic spot){
		SpotComfort entity = simuSCMap.get(spot.getId());
		//已经存在2小时之内的模拟数据，则直接返回
		if(entity != null && (System.currentTimeMillis()-entity.getTime()) < 7200000){
			return entity;
		}
		entity = new SpotComfort();
		entity.setSpot(spot);
		entity.setComfortDegree(Utils.gnrRandom1(60, 100));
		entity.setPsgrFlow(Utils.gnrRandom1(50, 300));
		entity.setPsgrScore(Utils.gnrRandom1(40, 100));
		entity.setViewScore(Utils.calculateViewScore(spot.getViewLevel()));
		entity.setWeatherScore(Utils.gnrRandom1(60, 100));
		simuSCMap.put(spot.getId(), entity);
		
		return entity;
	}
}
