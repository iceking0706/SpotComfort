package com.xie.spot.sys.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import ssin.util.DateProcess;

/**
 * 针对Excel的一些优化方法
 * 
 * 行范围：1～65535
 * 列范围：A～IV
 * 在POI中，行和列都是从0开始，即A1=(0,0)
 * @author IcekingT420
 *
 */
public class ExcelHelper {
	/**
	 * 判断传入的单元格名称中列几位，1或者2
	 * @param cellCode
	 * @return
	 */
	public static int partColumnLen(String cellName){
		if(cellName == null || cellName.equals(""))
			return 0;
		String cc = cellName.toUpperCase();
		//列字符的长度，最小1，最大2
		int len = 0;
		char[] chars = cc.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int ci = (int)chars[i];
			if(ci >= (int)'A' && ci <= (int)'Z'){
				len++;
			}else{
				break;
			}
		}
		return len;
	}
	
	/**
	 * 从一个单元格编号中截取行号
	 * A1返回1
	 * @param cellCode
	 * @return
	 */
	public static String partRowString(String cellName){
		int colLen = partColumnLen(cellName);
		if(colLen<1 || colLen>2)
			return null;
		return cellName.substring(colLen);
	}
	
	/**
	 * 从单元格中截取列号
	 * @param cellName
	 * @return
	 */
	public static String partColumnString(String cellName){
		int colLen = partColumnLen(cellName);
		if(colLen<1 || colLen>2)
			return null;
		return cellName.substring(0, colLen).toUpperCase();
	}
	
	/**
	 * 根据单元格的编号得到行号, A1返回0
	 * @param cellCode
	 * @return
	 */
	public static int getRowIndex(String cellName){
		String rowPart = partRowString(cellName);
		if(rowPart == null)
			return -1;
		return Integer.parseInt(rowPart)-1;
	}
	
	/**
	 * 得到单元格的编号，从0开始
	 * @param cellName
	 * @return
	 */
	public static int getCellIndex(String cellName){
		String colPart = partColumnString(cellName);
		if(colPart == null)
			return -1;
		char[] chars = colPart.toCharArray();
		if(chars.length == 1){
			return (int)chars[0] - (int)'A';
		}else{
			return ((int)chars[0] - (int)'A'+1) * 26 + ((int)chars[1] - (int)'A');
		}
	}
	
	/**
	 * 判断数组是否为空
	 * @param array
	 * @return
	 */
	public static boolean isEmpty(String[] array){
		if(array == null || array.length == 0)
			return true;
		for (int i = 0; i < array.length; i++) {
			if(array[i] != null && !array[i].equals("")){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 判断double是否为整数
	 * @param d
	 * @return
	 */
	public static boolean isInteger(double d){
		return (d-(long)d)==0;
	}
	
	/**
	 * 根据桩号来得到编码，36900=K036+900
	 * @param pileNo
	 * @return
	 */
	public static String pileCode(double pileNo){
		//先得到整数部分
		long ll = (long)pileNo;
		String str1 = String.valueOf(ll / 1000);
		String str2 = String.valueOf(ll % 1000);
		//补足3位
		while(str1.length()<3){
			str1 = "0"+str1;
		}
		while(str2.length()<3){
			str2 = "0"+str2;
		}
		//小数部分
		String str3 = "";
		if(!isInteger(pileNo)){
			String ds = String.valueOf(pileNo);
			int index1 = ds.indexOf('.');
			str3 = ds.substring(index1, index1+2);
		}
		return "K"+str1+"+"+str2+str3;
	}
	
	/**
	 * 根据单元格类型，得到string的值，如果为null，则返回空
	 * @param cell
	 * @return
	 */
	public static String getCellStringValue(HSSFCell cell){
		if(cell == null)
			return "";
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_BLANK:
			return "";
		case HSSFCell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case HSSFCell.CELL_TYPE_ERROR:
			return String.valueOf(cell.getErrorCellValue());
		case HSSFCell.CELL_TYPE_FORMULA:
			try {
				return cell.getRichStringCellValue().getString().trim();
			} catch (Exception e) {
			}
		case HSSFCell.CELL_TYPE_NUMERIC:
			if(HSSFDateUtil.isCellDateFormatted(cell)){
				return DateProcess.toString(cell.getDateCellValue(), "yyyy-MM-dd");
			}
			double dv = cell.getNumericCellValue();
			if(isInteger(dv)){
				return String.valueOf((long)dv);
			}else{
				return String.valueOf(dv);
			}
		case HSSFCell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().getString().trim();
		default:
			return "";
		}
	}
	
	public static String getCellStringValue(HSSFSheet sheet,int rowIndex,int cellIndex){
		if(rowIndex<0 || cellIndex<0)
			return null;
		HSSFRow row = sheet.getRow(rowIndex);
		if(row == null)
			return null;
		HSSFCell cell = row.getCell(cellIndex);
		if(cell == null)
			return null;
		return getCellStringValue(cell);
	}
	
	/**
	 * 根据单元格名称得到字符型的值
	 * @param sheet
	 * @param cellName
	 * @return
	 */
	public static String getCellStringValue(HSSFSheet sheet,String cellName){
		int rowIndex = getRowIndex(cellName);
		if(rowIndex<0)
			return null;
		int cellIndex = getCellIndex(cellName);
		if(cellIndex<0)
			return null;
		return getCellStringValue(sheet,rowIndex,cellIndex);
	}
	
	/**
	 * 获取一个范围内的全部数据，如果整行都为空，那么取消
	 * @param sheet
	 * @param startRowIndex
	 * @param startCellIndex
	 * @param endRowIndex
	 * @param endCellIndex
	 * @return
	 */
	public static List<String[]> getCellsStringValue(HSSFSheet sheet,int startRowIndex,int startCellIndex,int endRowIndex,int endCellIndex){
		List<String[]> list = new ArrayList<String[]>();
		if(startRowIndex<0 || startCellIndex<0 || endRowIndex<0 || endCellIndex<0)
			return list;
		if(endRowIndex<startRowIndex)
			return list;
		if(endCellIndex<startCellIndex)
			return list;
		
		//行循环
		for(int i=startRowIndex;i<=endRowIndex;i++){
			HSSFRow row = sheet.getRow(i);
			if(row == null)
				break;
			//根据列循环
			List<String> tmpList = new ArrayList<String>();
			for(int j=startCellIndex;j<=endCellIndex;j++){
				HSSFCell cell = row.getCell(j);
				tmpList.add(getCellStringValue(cell));
			}
			Object[] arrayo = tmpList.toArray();
			String[] arrays = new String[arrayo.length];
			for(int k=0;k<arrays.length;k++){
				arrays[k] = (String)arrayo[k];
			}
			if(!isEmpty(arrays))
				list.add(arrays);
		}
		
		return list;
	}
	
	/**
	 * 获得一个范围内的值，得到一个二维字符数组
	 * @param sheet
	 * @param startCellName
	 * @param endCellName
	 * @return
	 */
	public static List<String[]> getCellsStringValue(HSSFSheet sheet,String startCellName,String endCellName){
		int startRowIndex = getRowIndex(startCellName);
		int startCellIndex = getCellIndex(startCellName);
		int endRowIndex = getRowIndex(endCellName);
		int endCellIndex = getCellIndex(endCellName);
		return getCellsStringValue(sheet, startRowIndex, startCellIndex, endRowIndex, endCellIndex);
	}
	
	/**
	 * 从一行开始，到全部数据，列的范围指定
	 * @param sheet
	 * @param startRowIndex
	 * @param startCellIndex
	 * @param endCellIndex
	 * @return
	 */
	public static List<String[]> getCellsStringValue(HSSFSheet sheet,int startRowIndex,int startCellIndex,int endCellIndex){
		//最后一行，就是全部行数-1
		int endRowIndex = sheet.getPhysicalNumberOfRows()-1;
		return getCellsStringValue(sheet, startRowIndex, startCellIndex, endRowIndex, endCellIndex);
	}
	
	/**
	 * 从一行开始，全部数据，指定列范围，使用列名称 A~IV
	 * @param sheet
	 * @param startRowIndex
	 * @param startCol
	 * @param endCol
	 * @return
	 */
	public static List<String[]> getCellsStringValue(HSSFSheet sheet,int startRowIndex,String startCol,String endCol){
		return getCellsStringValue(sheet, startRowIndex, getCellIndex(startCol), getCellIndex(endCol));
	}
}
