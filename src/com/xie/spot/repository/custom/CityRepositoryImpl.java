package com.xie.spot.repository.custom;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.xie.spot.entity.City;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public class CityRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * 组装where语句
	 * @param mapCon
	 * @return
	 */
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		//按省份名称查询
		if(mapCon.get("province") != null){
			String province = (String)mapCon.get("province");
			hsql += " and t.province like '%"+province+"%'";
		}
		//按城市名称查询
		if(mapCon.get("city") != null){
			String name = (String)mapCon.get("city");
			hsql += " and t.name like '%"+name+"%'";
		}
		return hsql;
	}
	
	public PageData<City> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<City> pageData = new PageData<City>();
		//先找total
		String hsql = "select count(t) from City t"+gnrSeachWhere(mapCon);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		//再找本页内容
		hsql = "from City t"+gnrSeachWhere(mapCon);
		hsql += " order by t.pinyszm,t.pinyin";
		query = em.createQuery(hsql);
		if(pageParam != null && pageParam.isPageValid()){
			query.setFirstResult(pageParam.getFirst());
			query.setMaxResults(pageParam.getSize());
			pageData.setPageParam(pageParam);
		}
		pageData.setContent(query.getResultList());
		return pageData;
	}
	
	public boolean canDelete(City city){
		long total = 0l;
		String hsql = "select count(t) from SpotBasic t where t.city=?1";
		Query query = em.createQuery(hsql);
		query.setParameter(1, city);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		if(total>0)
			return false;
		
		return true;
	}
}
