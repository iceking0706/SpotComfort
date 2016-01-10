package com.xie.spot.repository.custom;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import com.xie.spot.entity.CameraData;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public class CameraDataRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		
		//按照sn精确查询
		if(mapCon.get("snfull") != null){
			String snfull = (String)mapCon.get("snfull");
			hsql += " and t.sn='"+snfull+"'";
		}
		
		//按照sn模糊匹配
		if(mapCon.get("sn") != null){
			String sn = (String)mapCon.get("sn");
			hsql += " and t.sn like '%"+sn+"%'";
		}
		
		//开始时间
		if(mapCon.get("startTime") != null){
			Long startTime = (Long)mapCon.get("startTime");
			hsql += " and t.time>="+startTime;
		}
		//结束时间
		if(mapCon.get("endTime") != null){
			Long endTime = (Long)mapCon.get("endTime");
			hsql += " and t.time<="+endTime;
		}
		
		return hsql;
	}
	
	public PageData<CameraData> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<CameraData> pageData = new PageData<CameraData>();
		//先找total
		String hsql = "select count(t) from CameraData t"+gnrSeachWhere(mapCon);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		if(pageData.getTotal() == 0l)
			return pageData;
		//再找本页内容
		hsql = "from CameraData t"+gnrSeachWhere(mapCon);
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
	
	public CameraData getLatestPic(String sn){
		String hsql = "from CameraData t where t.sn='"+sn+"' and t.picUrl is not null order by t.time desc";
		Query query = em.createQuery(hsql);
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<CameraData> list = query.getResultList();
		if(list!=null && !list.isEmpty())
			return list.get(0);
		return null;
	}
}
