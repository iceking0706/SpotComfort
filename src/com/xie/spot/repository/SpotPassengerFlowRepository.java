package com.xie.spot.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotPassengerFlow;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public interface SpotPassengerFlowRepository extends JpaRepository<SpotPassengerFlow, Long>{
	/**
	 * 多条件查询
	 * @param mapCon
	 * @param pageParam
	 * @return
	 */
	public PageData<SpotPassengerFlow> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 找到某个景点的客流量数据，一般一条
	 * @param spot
	 * @param page
	 * @return
	 */
	public Page<SpotPassengerFlow> findBySpotOrderByTimeDesc(SpotBasic spot,Pageable page);
}
