package com.xie.spot.repository;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xie.spot.entity.CameraData;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public interface CameraDataRepository extends JpaRepository<CameraData, Long>{
	public PageData<CameraData> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 得到相机最新到图片数据
	 * @param sn
	 * @return
	 */
	public CameraData getLatestPic(String sn);
}
