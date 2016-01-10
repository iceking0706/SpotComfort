package com.xie.spot.sys.utils;

import java.util.List;

import net.sf.json.JSONObject;

/**
 * json的返回结果，其实内部就是一个JsonObject
 * 
 * @author IcekingT420
 * 
 */
public class JsonResult {
	private JSONObject jsonObject;

	public JsonResult() {
		jsonObject = new JSONObject();
	}

	public JsonResult(Object obj) {
		jsonObject = JSONObject.fromObject(obj);
	}

	public JsonResult(boolean succ) {
		jsonObject = new JSONObject();
		put("succ", succ);
	}
	
	public JsonResult(boolean succ,String stmt) {
		this(succ);
		put("stmt", stmt);
	}
	
	/**
	 * 为强制删除数据做准备，强制删除的级别从扩展模块开始，不包括主控器
	 * @param succ
	 * @param stmt
	 * @param hasForeignKey
	 * @param entityName 当前要删除的实体名称
	 * @param entityId 当前要删除的实体主键，一般是long
	 */
	public JsonResult(boolean succ,String stmt,String entityName,String entityId){
		this(succ, stmt);
		//由这个构造的话，一定是有外键关联着了
		put("hasForeignKey", true);
		put("entityName", entityName);
		put("entityId", entityId);
	}
	
	public JsonResult(boolean succ,long total,List<?> rows){
		this(succ);
		putTotal(total);
		putRows(rows);
	}

	public void put(String key, Object value) {
		jsonObject.put(key, value);
	}

	public void remove(String key) {
		jsonObject.remove(key);
	}
	
	/**
	 * 针对分页的设置
	 */
	public void putTotal(long total){
		put("total", total);
	}
	
	public void putRows(List<?> rows){
		put("rows", rows);
	}

	@Override
	public String toString() {
		return jsonObject.toString();
	}
}
