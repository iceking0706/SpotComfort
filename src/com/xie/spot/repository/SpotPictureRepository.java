package com.xie.spot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xie.spot.entity.SpotBasic;
import com.xie.spot.entity.SpotPicture;

public interface SpotPictureRepository extends JpaRepository<SpotPicture, Long>{
	/**
	 * 查询某个景点的图片
	 * @param spot
	 * @param page
	 * @return
	 */
	@Query(value="from SpotPicture t where t.spot=?1 order by t.mainRcmd desc,t.time desc")
	public Page<SpotPicture> findBySpotOrderByMainRcmdDescAndTimeDesc(SpotBasic spot,Pageable page);
	
	/**
	 * 某个景点当前的图片数量
	 * @param spot
	 * @return
	 */
	@Query(value="select count(t) from SpotPicture t where t.spot=?1")
	public long totalPicCountOfSpot(SpotBasic spot);
}
