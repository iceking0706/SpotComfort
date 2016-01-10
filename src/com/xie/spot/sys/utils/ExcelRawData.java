package com.xie.spot.sys.utils;

/**
 * Excel中的一行，原始数据
 * @author iceking
 *
 */
public class ExcelRawData {
	private int rowNo;
	private String[] data;

	public ExcelRawData(int rowNo, String[] data) {
		super();
		this.rowNo = rowNo;
		this.data = data;
	}
	
	/**
	 * 判断是否全部为空，无效数据
	 * @return
	 */
	public boolean isAllBlank(){
		if(data==null || data.length == 0)
			return true;
		boolean allblank = true;
		for(String str: data){
			if(str!=null && !str.equals("")){
				allblank = false;
				break;
			}
		}
		return allblank;
	}

	public ExcelRawData() {
		super();
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}
}
