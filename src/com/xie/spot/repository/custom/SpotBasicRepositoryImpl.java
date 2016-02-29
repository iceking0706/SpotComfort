package com.xie.spot.repository.custom;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.xie.spot.entity.SpotBasic;
import com.xie.spot.sys.utils.PageData;
import com.xie.spot.sys.utils.PageParam;

public class SpotBasicRepositoryImpl {
	@PersistenceContext
	private EntityManager em;
	
	private String gnrSeachWhere(Map<String, Object> mapCon){
		String hsql = "";
		if(mapCon==null || mapCon.keySet().size()==0)
			return hsql;
		hsql = " where 1=1";
		
		//如果是直接keyword进来的话，微信接口，就直接三者都进行匹配的
		if(mapCon.get("keyword") != null){
			String keyword = (String)mapCon.get("keyword");
			hsql += " and (t.name like '%"+keyword+"%' or t.city.name='"+keyword+"' or t.city.province='"+keyword+"')";
		} else {
			// 如果景观名称有的话，就按照景点名称来找
			if (mapCon.get("name") != null) {
				String name = (String) mapCon.get("name");
				hsql += " and t.name like '%" + name + "%'";
			}
			// 按省份名称查询
			if (mapCon.get("province") != null) {
				String province = (String) mapCon.get("province");
				hsql += " and t.city.province='" + province + "'";
			}
			// 按城市名称
			if (mapCon.get("city") != null) {
				String city = (String) mapCon.get("city");
				hsql += " and t.city.name='" + city + "'";
			}
			// 按景观等级
			if (mapCon.get("viewLevel") != null) {
				Integer viewLevel = (Integer) mapCon.get("viewLevel");
				hsql += " and t.viewLevel=" + viewLevel;
			}
			//仅仅显示推荐的
			if(mapCon.get("justRcmd") != null){
				hsql += " and t.mainRcmd>0";
			}

		}
		
		return hsql;
	}
	
	public PageData<SpotBasic> searchBy(Map<String, Object> mapCon,PageParam pageParam){
		PageData<SpotBasic> pageData = new PageData<SpotBasic>();
		//先找total
		String hsql = "select count(t) from SpotBasic t"+gnrSeachWhere(mapCon);
//		System.out.println(hsql);
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			pageData.setTotal(((Long)result).longValue());
		}
		//再找本页内容
		hsql = "from SpotBasic t"+gnrSeachWhere(mapCon);
		hsql += " order by t.mainRcmd desc,t.viewLevel,t.name";
		query = em.createQuery(hsql);
		if(pageParam != null && pageParam.isPageValid()){
			query.setFirstResult(pageParam.getFirst());
			query.setMaxResults(pageParam.getSize());
			pageData.setPageParam(pageParam);
		}
		pageData.setContent(query.getResultList());
		return pageData;
	}
	
	public boolean canDelete(SpotBasic spot){
		//判断1
		long total = 0l;
		String hsql = "select count(t) from SpotComfort t where t.spot=?1";
		Query query = em.createQuery(hsql);
		query.setParameter(1, spot);
		Object result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		if(total>0)
			return false;
		
		//判断2
		total = 0l;
		hsql = "select count(t) from SpotComfortCorrect t where t.spot=?1";
		query = em.createQuery(hsql);
		query.setParameter(1, spot);
		result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		if(total>0)
			return false;
		
		//判断3
		total = 0l;
		hsql = "select count(t) from SpotPassengerFlow t where t.spot=?1";
		query = em.createQuery(hsql);
		query.setParameter(1, spot);
		result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		if(total>0)
			return false;
		
		//判断4
		total = 0l;
		hsql = "select count(t) from SpotPicture t where t.spot=?1";
		query = em.createQuery(hsql);
		query.setParameter(1, spot);
		result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		if(total>0)
			return false;
		
		//判断5
		total = 0l;
		hsql = "select count(t) from SpotWeather t where t.spot=?1";
		query = em.createQuery(hsql);
		query.setParameter(1, spot);
		result = query.getSingleResult();
		if(result != null && result instanceof Long){
			total = ((Long)result).longValue();
		}
		if(total>0)
			return false;
		
		return true;
	}
}
