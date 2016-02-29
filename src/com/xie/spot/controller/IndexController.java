package com.xie.spot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xie.spot.entity.AdvtPicture;
import com.xie.spot.entity.City;
import com.xie.spot.entity.CodeWeather;
import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotComfort;
import com.xie.spot.entity.SpotPicture;
import com.xie.spot.pojo.wechat.PjCitySpotC;
import com.xie.spot.pojo.wechat.PjSpotComfort;
import com.xie.spot.repository.AdvtPictureRepository;
import com.xie.spot.repository.CityRepository;
import com.xie.spot.repository.CodeWeatherRepository;
import com.xie.spot.repository.SpotBasicRepository;
import com.xie.spot.repository.SpotComfortRepository;
import com.xie.spot.repository.SpotPictureRepository;
import com.xie.spot.service.FetchWeatherService;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.JsonResult;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

/**
 * 首页上面的几个请求
 * @author IcekingT420
 *
 */
@Controller
public class IndexController {
	
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private CodeWeatherRepository codeWeatherRepository;
	@Autowired
	private SpotBasicRepository spotBasicRepository;
	@Autowired
	private SpotComfortRepository spotComfortRepository;
	@Autowired
	private SpotPictureRepository spotPictureRepository;
	@Autowired
	private FetchWeatherService fetchWeatherService;
	@Autowired
	private AdvtPictureRepository advtPictureRepository;
	
	@RequestMapping(value="/home")
	@Transactional
	public String index(HttpServletRequest request){
		//首页推荐的5个图片广告，必须是5个
		List<AdvtPicture> advtPicList = new ArrayList<AdvtPicture>();
		Page<AdvtPicture> pageData = advtPictureRepository.searchAll(new PageRequest(0, 5));
		if(!Utils.isEmpty(pageData.getContent())){
			for(AdvtPicture po: pageData.getContent()){
				advtPicList.add(po);
			}
		}
		if(advtPicList.size()<5){
			//补足的补充默认图片
			for(int i=0;i<5-advtPicList.size();i++){
				AdvtPicture po = new AdvtPicture();
				po.setUrl("images/1000x400.jpg");
				po.setLinkUrl("####");
				
				advtPicList.add(po);
			}
		}
		
		request.setAttribute("advtPicList", advtPicList);
		return "index";
	}
	
	/**
	 * 通过城市的名称来找到该城市的天气
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/weatherByCityName",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String weatherByCityName(HttpServletRequest request){
		String cityName = Utils.getParamValue(request, "cityName");
		if(Utils.isEmpty(cityName)){
			return JSONObject.fromObject(new PjCitySpotC()).toString();
		}
		
		//根据名字找到城市信息
		List<City> tmpList = cityRepository.findByName(cityName);
		if(Utils.isEmpty(tmpList)){
			return JSONObject.fromObject(new PjCitySpotC()).toString();
		}
		
		City city = tmpList.get(0);
		tmpList.clear();
		
		if(Utils.isEmpty(city.getWcode())){
			return JSONObject.fromObject(new PjCitySpotC()).toString();
		}
		
		//找到这个天气代码的最新一次的天气
		Page<CodeWeather> pageCW = codeWeatherRepository.findByWcodeOrderByJsonTimeDesc(city.getWcode(), new PageRequest(0, 1));
		if(pageCW==null || Utils.isEmpty(pageCW.getContent())){
			return JSONObject.fromObject(new PjCitySpotC()).toString();
		}
		
		PjCitySpotC pjCity = new PjCitySpotC();
		pjCity.setSuccess(true);
		pjCity.setCityId(city.getId());
		pjCity.setCity(city.getName());
		pjCity.setProvince(city.getProvince());
		pjCity.setWcode(city.getWcode());
		
		CodeWeather cw = pageCW.getContent().get(0);
		pjCity.setWeather(cw.getWeather());
		//pjCity.setWthPic(Utils.matchWeatherPic(cw.getWeather()));
		pjCity.setTemp(cw.getTemperature());
		pjCity.setHumi(cw.getHumidity());
		pjCity.setPm25(cw.getPm25());
		pjCity.setAqi(cw.getAqi());
		pjCity.setAqiT(cw.getAqiShow());
		pjCity.setTime(cw.getJsonTimeShow());
		
		return JSONObject.fromObject(pjCity).toString();
	}
	
	/**
	 * 主界面上，根据城市名称和景点关键字来找
	 * 一开始的话，只按照推荐的来找
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/spotComfortByCityAndName",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String spotComfortByCityAndName(HttpServletRequest request,PageParam pageParam){
		Map<String, Object> mapCon = new HashMap<String, Object>();
		if(request.getParameter("justRcmd") != null){
			mapCon.put("justRcmd", "true");
		}else{
			String cityName = Utils.getParamValue(request, "cityName");
			if(!Utils.isEmpty(cityName))
				mapCon.put("city", cityName);
			String spotName = Utils.getParamValue(request, "spotName");
			if(!Utils.isEmpty(spotName))
				mapCon.put("name", spotName);
		}
		
		//根据条件找到景点
		PageData<SpotBasic> pageData = spotBasicRepository.searchBy(mapCon, pageParam);
		if(pageData == null || Utils.isEmpty(pageData.getContent())){
			return new JsonResult(false, "no spot found.").toString();
		}
		
		//组装PjSpotComfort的列表，根据SpotBasic
		List<PjSpotComfort> listPJSC = new ArrayList<PjSpotComfort>();
		for(SpotBasic po: pageData.getContent()){
			PjSpotComfort pjSpot = new PjSpotComfort();
			pjSpot.setSpotId(po.getId());
			pjSpot.setName(po.getName());
			pjSpot.setViewL(po.getViewLevel());
			Page<SpotComfort> pageSC = spotComfortRepository.findBySpotOrderByTimeDesc(po, new PageRequest(0, 1));
			SpotComfort sc = null;
			if(pageSC!=null && !Utils.isEmpty(pageSC.getContent())){
				sc = pageSC.getContent().get(0);
			}else {
				//模拟的舒适度
				sc = fetchWeatherService.simulateSpotComfort(po);
			}
			pjSpot.setTime(sc.getTimeShow());
			pjSpot.setCdIndex(sc.getComfortDegree());
			pjSpot.setPsgrFlow(sc.getPsgrFlow());
			pjSpot.setPsgr(sc.getPsgrScoreShow());
			pjSpot.setView(sc.getViewScoreShow());
			//找景点最新一张图片
			Page<SpotPicture> pageSP = spotPictureRepository.findBySpotOrderByMainRcmdDescAndTimeDesc(po, new PageRequest(0, 1));
			if(pageSP!=null && !Utils.isEmpty(pageSP.getContent())){
				pjSpot.setSpotPic(pageSP.getContent().get(0).getUrl());
			}
			
			listPJSC.add(pjSpot);
		}
		
		JsonResult jsonResult = new JsonResult(true, pageData.getTotal(), listPJSC);
		jsonResult.put("page", pageParam.getPage());
		jsonResult.put("size", pageParam.getSize());
		
		return jsonResult.toString();
	}
}
