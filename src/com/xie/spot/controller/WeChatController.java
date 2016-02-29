package com.xie.spot.controller;

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

import com.xie.spot.entity.City;
import com.xie.spot.entity.CodeWeather;
import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotComfort;
import com.xie.spot.entity.SpotPicture;
import com.xie.spot.pojo.wechat.PjCitySpotC;
import com.xie.spot.pojo.wechat.PjSpotComfort;
import com.xie.spot.pojo.wechat.PjWxResult;
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
 * 和信产蒋涛的微信对接接口
 * 
 * 主要两个：
 * 1、发送省份名字过来，返回该省份下所有的城市（建立了舒适度指数的城市）
 * 2、发送城市名称过来，返回城市的最新天气和城市中全部景点的舒适度信息
 * 3、发送景点名称过来，返回景点所在城市的天气，和这个景点的舒适度信息
 * @author IcekingT420
 *
 */
@Controller
public class WeChatController {
	@Autowired
	private SpotBasicRepository spotBasicRepository;
	@Autowired
	private CodeWeatherRepository codeWeatherRepository;
	@Autowired
	private SpotComfortRepository spotComfortRepository;
	@Autowired
	private SpotPictureRepository spotPictureRepository;
	@Autowired
	private FetchWeatherService fetchWeatherService;
	@Autowired
	private CityRepository cityRepository;
	
	/**
	 * 通过微信发送过来的请求，三种类型：
	 * 1、通过省份查城市列表
	 * province=浙江, json={succ:true,total:3,city:["杭州","金华"]}
	 * 
	 * 2、景点名称查询这个景点的信息，景点名称匹配到的也可能是多个的
	 * spot=断桥, json={succ:true,total=1,start=0,limit=1,spots:[{}]}
	 * 失败，json={succ:fase,error:'spot not exist'}
	 * 
	 * 3、城市名称查询城市中所有的景点，默认返回10个，结构和上面的有一个类似的
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/wechat",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String wechat(HttpServletRequest request){
		//优先级：景点>城市>省份
		String keyword = Utils.getParamValue(request, "keyword");
		String city = Utils.getParamValue(request, "city");
		//根据传入的参数查询景点
		int page = 0;
		if(Utils.getParamValue(request, "page") != null){
			page = Utils.parseInt(Utils.getParamValue(request, "page"));
		}
		int size = 10;
		if(Utils.getParamValue(request, "size") != null){
			size = Utils.parseInt(Utils.getParamValue(request, "size"));
		}
		
		Map<String, Object> mapCon = new HashMap<String, Object>();
		if(!Utils.isEmpty(city)){
			mapCon.put("city", city);
			if(!Utils.isEmpty(keyword)){
				mapCon.put("name", keyword);
			}
		}else{
			if(!Utils.isEmpty(keyword)){
				mapCon.put("keyword", keyword);
			}else {
				mapCon.put("keyword", "unKnow");
			}
		}
		
		
		//返回结果
		PjWxResult result = new PjWxResult();
		result.setPage(page);
		result.setSize(size);
		//根据条件查询景点集合
		PageData<SpotBasic> pageData = spotBasicRepository.searchBy(mapCon, new PageParam(page, size));
		if(pageData.getTotal()==0l || Utils.isEmpty(pageData.getContent())){
			result.setError("No result found.");
			return JSONObject.fromObject(result).toString();
		}
		result.setSuccess(true);
		result.setTotal(pageData.getTotal());
		//循环每个景点
		for(SpotBasic po: pageData.getContent()){
			PjCitySpotC pjCity = result.findById(po.getCity().getId());
			if(pjCity == null){
				//没有城市，添加城市的天气信息
				pjCity = new PjCitySpotC();
				pjCity.setSuccess(true);
				pjCity.setCityId(po.getCity().getId());
				pjCity.setCity(po.getCity().getName());
				pjCity.setProvince(po.getCity().getProvince());
				pjCity.setCityPinyin(po.getCity().getPinyin());
				pjCity.setCityPyszm(po.getCity().getPinyszm());
				pjCity.setCityPicUrl(po.getCity().getPicUrl());
				if(po.getCity().getRcmd()!=null)
					pjCity.setCityRcmd(po.getCity().getRcmd().intValue());
				pjCity.setWcode(po.getCity().getWcode());
				if(!Utils.isEmpty(pjCity.getWcode())){
					//天气代码存在，则去找最近一条天气
					Page<CodeWeather> pageCW = codeWeatherRepository.findByWcodeOrderByJsonTimeDesc(pjCity.getWcode(), new PageRequest(0, 1));
					if(pageCW!=null && !Utils.isEmpty(pageCW.getContent())){
						CodeWeather cw = pageCW.getContent().get(0);
						pjCity.setWeather(cw.getWeather());
						pjCity.setTemp(cw.getTemperature());
						pjCity.setHumi(cw.getHumidity());
						pjCity.setPm25(cw.getPm25());
						pjCity.setAqi(cw.getAqi());
						pjCity.setAqiT(cw.getAqiShow());
						pjCity.setTime(cw.getJsonTimeShow());
					}
				}
				//加入到结果集中
				result.addCity(pjCity);
			}
			//找到城市对象后，将景点对象放入进去
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
			pjCity.addSpot(pjSpot);
		}
		
		return JSONObject.fromObject(result).toString();
	}
	
	/**
	 * 返回所有的城市信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/wxcity",produces="application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String wxcity(HttpServletRequest request){
		PageData<City> page = cityRepository.searchBy(null, null);
		return new JsonResult(true, page.getTotal(), page.getContent()).toString();
	}
}
