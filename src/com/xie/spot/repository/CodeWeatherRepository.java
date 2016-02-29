package com.xie.spot.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xie.spot.entity.CodeWeather;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public interface CodeWeatherRepository extends JpaRepository<CodeWeather, Long>{
	/**
	 * 判断某个wcode某个时间点的是否已经存在
	 * @param wcode
	 * @param jsonTime
	 * @return
	 */
	public CodeWeather findByWcodeAndJsonTime(String wcode,Long jsonTime);
	
	public PageData<CodeWeather> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 根据天气代码找到景点最近一条的天气
	 * @param wcode
	 * @param page
	 * @return
	 */
	public Page<CodeWeather> findByWcodeOrderByJsonTimeDesc(String wcode,Pageable page);
}
