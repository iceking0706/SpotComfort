package com.xie.spot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotComfort;

public interface SpotComfortRepository extends JpaRepository<SpotComfort, Long>{
	/**
	 * 找到某个景点最新的一条舒适度记录
	 * @param spot
	 * @param page
	 * @return
	 */
	public Page<SpotComfort> findBySpotOrderByTimeDesc(SpotBasic spot,Pageable page);
}
