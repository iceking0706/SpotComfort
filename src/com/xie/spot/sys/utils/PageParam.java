package com.xie.spot.sys.utils;

/**
 * 从页面传过来的参数
 * 其它可以扩展这个
 * 
 * @author IcekingT420
 * 
 */
public class PageParam {
	/**
	 * JqueryUI中涉及到翻页的
	 */
	private int page;
	private int rows;
	
	public PageParam() {
	}

	public PageParam(int page, int rows) {
		this.page = page;
		this.rows = rows;
	}

	public boolean isPageValid(){
		if(page>=0 && rows>=0)
			return true;
		return false;
	}
	
	public int getFirst(){
		return page*rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page-1;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getSize() {
		return getRows();
	}
}
