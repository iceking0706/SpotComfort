package com.xie.spot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotComfortCorrect;

public interface SpotComfortCorrectRepository extends JpaRepository<SpotComfortCorrect, Long>{
	
	/**
	 * 查询某个景点的修正因子
	 * @param spot
	 * @param page
	 * @return
	 */
	public Page<SpotComfortCorrect> findBySpot(SpotBasic spot,Pageable page);
	
	/**
	 * 某个景点当前的修正因子数量
	 * @param spot
	 * @return
	 */
	@Query(value="select count(t) from SpotComfortCorrect t where t.spot=?1")
	public long totalCcCountOfSpot(SpotBasic spot);
}
