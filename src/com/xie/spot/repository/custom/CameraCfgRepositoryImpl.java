package com.xie.spot.repository.custom;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.xie.spot.entity.CameraCfg;
import com.xie.spot.sys.Utils;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public class CameraCfgRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		
		//按照sn模糊匹配
		if(mapCon.get("sn") != null){
			String sn = (String)mapCon.get("sn");
			hsql += " and t.sn like '%"+sn+"%'";
		}
		
		//按照在线查询
		if(mapCon.get("online") != null){
			Integer online = (Integer)mapCon.get("online");
			hsql += " and t.online="+online;
		}
		
		//按照备注查询
		if(mapCon.get("mark") != null){
			String mark = (String)mapCon.get("mark");
			hsql += " and t.mark like '%"+mark+"%'";
		}
		
		//仅仅启用
		if(mapCon.get("inUse") != null){
			Integer inUse = (Integer)mapCon.get("inUse");
			hsql += " and t.inUse="+inUse.intValue();
		}
		
		//场景
		if(mapCon.get("scene") != null){
			String scene = (String)mapCon.get("scene");
			hsql += " and t.scene like '%"+scene+"%'";
		}
		
		return hsql;
	}
	
	public PageData<CameraCfg> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<CameraCfg> pageData = new PageData<CameraCfg>();
		//先找total
		String hsql = "select count(t) from CameraCfg t"+gnrSeachWhere(mapCon);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		if(pageData.getTotal() == 0l)
			return pageData;
		//再找本页内容
		hsql = "from CameraCfg t"+gnrSeachWhere(mapCon);
		hsql += " order by t.sn";
		query = em.createQuery(hsql);
		if(pageParam != null && pageParam.isPageValid()){
			query.setFirstResult(pageParam.getFirst());
			query.setMaxResults(pageParam.getSize());
			pageData.setPageParam(pageParam);
		}
		pageData.setContent(query.getResultList());
		return pageData;
	}
	
	public boolean meIsSnExist(String sn,Long exceptId){
		String hsql = "select count(t) from CameraCfg t where t.sn='"+sn+"'";
		if(!Utils.isEmptyId(exceptId)){
			hsql += " and t.id<>"+exceptId;
		}
		long total = 0;
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		return total>0;
	}
	
	public boolean canDelete(CameraCfg cfg){
		return true;
	}
}
