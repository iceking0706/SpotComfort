package com.xie.spot.repository.custom;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.xie.spot.entity.CodeWeather;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public class CodeWeatherRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		if(mapCon.get("wcode") != null){
			String wcode = (String)mapCon.get("wcode");
			hsql += " and t.wcode like '%"+wcode+"%'";
		}
		return hsql;
	}
	
	public PageData<CodeWeather> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<CodeWeather> pageData = new PageData<CodeWeather>();
		//先找total
		String hsql = "select count(t) from CodeWeather t"+gnrSeachWhere(mapCon);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		//再找本页内容
		hsql = "from CodeWeather t"+gnrSeachWhere(mapCon);
		hsql += " order by t.jsonTime desc";
		query = em.createQuery(hsql);
		if(pageParam != null && pageParam.isPageValid()){
			query.setFirstResult(pageParam.getFirst());
			query.setMaxResults(pageParam.getSize());
			pageData.setPageParam(pageParam);
		}
		pageData.setContent(query.getResultList());
		return pageData;
	}
}
