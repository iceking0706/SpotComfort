package com.xie.spot.repository.custom;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.xie.spot.entity.SpotPassengerFlow;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public class SpotPassengerFlowRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		//景点名称优先
		if(mapCon.get("spotName") != null){
			String spotName = (String)mapCon.get("spotName");
			hsql += " and t.spot.name like '%"+spotName+"%'";
		}else{
			//按照城市查询
			if(mapCon.get("spotCity") != null){
				String spotCity = (String)mapCon.get("spotCity");
				hsql += " and t.spot.city.name='"+spotCity+"'";
			}
		}
		
		return hsql;
	}
	
	public PageData<SpotPassengerFlow> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<SpotPassengerFlow> pageData = new PageData<SpotPassengerFlow>();
		//先找total
		String hsql = "select count(t) from SpotPassengerFlow t"+gnrSeachWhere(mapCon);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		//再找本页内容
		hsql = "from SpotPassengerFlow t"+gnrSeachWhere(mapCon);
		hsql += " order by t.time desc";
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
