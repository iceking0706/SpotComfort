package com.xie.spot.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xie.spot.entity.City;
import com.xie.spot.entity.SpotBasic;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public interface SpotBasicRepository extends JpaRepository<SpotBasic, Long>{
	/**
	 * 查询某个城市下面的所有景点
	 * @param city
	 * @return
	 */
	public List<SpotBasic> findByCity(City city);
	
	/**
	 * 找到某个城市下面某个名字的景点
	 * @param name
	 * @param city
	 * @return
	 */
	public SpotBasic findByNameAndCity(String name,City city);
	
	/**
	 * 根据wcode找到景点，一般没有
	 * @param wcode
	 * @param page
	 * @return
	 */
	public Page<SpotBasic> findByWcode(String wcode,Pageable page);
	
	/**
	 * 多条件查询
	 * @param mapCon
	 * @param pageParam
	 * @return
	 */
	public PageData<SpotBasic> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 判断记录是否可以删除
	 * @param spot
	 * @return
	 */
	public boolean canDelete(SpotBasic spot);
	
	/**
	 * 得到所有的天气代码
	 * @return
	 */
	@Query(value="select distinct wcode from SpotBasic")
	public List<String> listAllWcode();
}
