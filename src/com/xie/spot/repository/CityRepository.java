package com.xie.spot.repository;

import java.util.List;
import java.util.Map;

import com.xie.spot.entity.City;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CityRepository extends JpaRepository<City, Long>{
	/**
	 * 根据名字找到城市
	 * @param name
	 * @return
	 */
	public List<City> findByName(String name);
	
	/**
	 * 根据省份和城市名称查询，一个省份下不能存在同名的城市
	 * @param name
	 * @param province
	 * @return
	 */
	public City findByNameAndProvince(String name,String province);
	
	/**
	 * 找到省份下的所有城市
	 * @param province
	 * @return
	 */
	public List<City> findByProvinceOrderByNameAsc(String province);
	
	/**
	 * 根据wcode找到城市，一般只有一个
	 * @param wcode
	 * @param page
	 * @return
	 */
	public Page<City> findByWcode(String wcode,Pageable page);
	
	/**
	 * 翻页查询，查询条件放在map中，可以扩展
	 * @param mapSearch
	 * @param pageParam
	 * @return
	 */
	public PageData<City> searchBy(Map<String, Object> mapCon,PageParam pageParam);
	
	/**
	 * 判断是否有外键关系
	 * @param city
	 * @return
	 */
	public boolean canDelete(City city);
	
	/**
	 * 所有的省份，distinct
	 * @return
	 */
	@Query(value="select distinct province from City order by province")
	public List<String> listAllProvince();
	
	/**
	 * 得到所有的天气代码
	 * @return
	 */
	@Query(value="select distinct wcode from City")
	public List<String> listAllWcode();
}
