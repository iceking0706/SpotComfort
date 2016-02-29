package com.xie.spot.repository.custom;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.xie.spot.entity.CameraAlert;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;


public class CameraAlertRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		
		//按类型查询
		if(mapCon.get("type") != null){
			Integer type = (Integer)mapCon.get("type");
			hsql += " and t.type="+type;
		}
		
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
		
		//是否处理
		if(mapCon.get("processed") != null){
			Integer processed = (Integer)mapCon.get("processed");
			if(processed.intValue() == 1){
				//未处理
				hsql += " and t.prsTime=0";
			}else {
				hsql += " and t.prsTime>0";
			}
		}
		
		return hsql;
	}
	
	public PageData<CameraAlert> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<CameraAlert> pageData = new PageData<CameraAlert>();
		//先找total
		String hsql = "select count(t) from CameraAlert t"+gnrSeachWhere(mapCon);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		if(pageData.getTotal() == 0l)
			return pageData;
		//再找本页内容
		hsql = "from CameraAlert t"+gnrSeachWhere(mapCon);
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
	
	public boolean meHasUnProcessedCmrAlert(String sn,Integer type){
		String hsql = "from CameraAlert t where t.sn=?1 and t.type=?2 and t.prsTime=0";
		Query query = em.createQuery(hsql);
		query.setParameter(1, sn);
		query.setParameter(2, type);
		//只查询一个即可
		query.setFirstResult(0);
		query.setMaxResults(1);
		List<?> list = query.getResultList();
		return !Utils.isEmpty(list);
	}
	
	public List<CameraAlert> meNeedToSendMail(int count){
		String hsql = "from CameraAlert t where t.prsTime=0 and t.mailTime=0 order by t.time";
		Query query = em.createQuery(hsql);
		query.setFirstResult(0);
		int curCC = count;
		if(count<0)
			curCC = 10;
		if(count>50)
			curCC = 50;
		query.setMaxResults(curCC);
		return query.getResultList();
	}
}
