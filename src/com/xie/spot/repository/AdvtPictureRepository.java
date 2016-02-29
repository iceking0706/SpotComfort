package com.xie.spot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xie.spot.entity.AdvtPicture;

public interface AdvtPictureRepository extends JpaRepository<AdvtPicture, Long>{
	@Query(value="from AdvtPicture t order by t.mainRcmd desc,t.time desc")
	public Page<AdvtPicture> searchAll(Pageable page);
}
