package com.suyou.singnalway;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xie.spot.sys.Utils;

import ssin.util.DateProcess;
import ssin.util.MyStringUtil;

/**
 * 接收到In out 数据
 * 
 * @author IcekingT420
 * 
 */
public class RecvInOutData {
	private int inData;
	private int outData;
	private long timeMs = System.currentTimeMillis();
	
	public RecvInOutData(){
		
	}
	
	/**
	 * 通过callback方法中得到的字符串，去截取得到in和out的整数
	 * @param callbackString
	 */
	public RecvInOutData(String callbackString){
		int idx1 = callbackString.indexOf("in:");
		if(idx1 == -1)
			return;
		String inoutStr = callbackString.substring(idx1);
		idx1 = inoutStr.indexOf(',');
		if(idx1 == -1)
			return;
		String inStr = inoutStr.substring(0, idx1);
		String outStr = inoutStr.substring(idx1+2);
		//in数据
		idx1 = inStr.indexOf(':');
		if(idx1!=-1)
			inData = parseInt(inStr.substring(idx1+2));
		//out数据
		idx1 = outStr.indexOf(':');
		if(idx1!=-1)
			outData = parseInt(outStr.substring(idx1+2));
	}
	
	public static int parseInt(String str){
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public String getTimeShow() {
		if (timeMs == 0l)
			return "";
		return DateProcess.toString(new Date(timeMs),
				DateProcess.format_yyyy_MM_dd_HH_mm_ss);
	}

	public int getInData() {
		return inData;
	}

	public void setInData(int inData) {
		this.inData = inData;
	}

	public int getOutData() {
		return outData;
	}

	public void setOutData(int outData) {
		this.outData = outData;
	}

	public long getTimeMs() {
		return timeMs;
	}

	public void setTimeMs(long timeMs) {
		this.timeMs = timeMs;
	}
	
	/**
	 * [2014-06-01 00:00],in:3,out:4;...[2014-06-30 23:59],in:0,out:0;
	 * 把in后面的第一个分号;替换成逗号,
	 * @return
	 */
	public static String parse111(String info){
		//判断是否有;out的存在，不存在，则直接返回原来的字符
		if(info.indexOf(";out")==-1)
			return info;
		char[] array = info.toCharArray();
		for(int i=0;i<array.length;i++){
			if(array[i] == ';' && i != array.length-1){
				//如果是分号，看看后面三个是否是out
				String out = new String(new char[]{array[i+1],array[i+2],array[i+3]});
				if(out.equals("out")){
					array[i] = ',';
				}
			}
		}
		return new String(array);
	}
	
	/**
	 * 根据一段时间的in、out情况，解析成对象
	 * [2014-06-01 00:00],in:3,out:4;...[2014-06-30 23:59],in:0,out:0;
	 * @param info
	 * @return
	 */
	public static List<RecvInOutData> parsePCSFlowInfo(String info){
		List<RecvInOutData> list = new ArrayList<RecvInOutData>();
		if(info == null || info.equals(""))
			return list;
		String[] strArray = MyStringUtil.getArrayFromStrByChar(info, ";");
		if(strArray == null || strArray.length == 0)
			return list;
		for(String line: strArray){
			String[] lineArray = MyStringUtil.getArrayFromStrByChar(line, ",");
			if(lineArray==null || lineArray.length!=3)
				continue;
			if(lineArray[0].indexOf('[') == -1 || lineArray[0].indexOf(']') ==-1)
				continue;
			Date tmpDate = DateProcess.toDate(lineArray[0].substring(lineArray[0].indexOf('[')+1, lineArray[0].indexOf(']')), "yyyy-MM-dd HH:mm");
			if(tmpDate == null)
				continue;
			if(lineArray[1].indexOf("in:") == -1)
				continue;
			int inData = parseInt(lineArray[1].substring(lineArray[1].indexOf("in:")+3));
			if(lineArray[2].indexOf("out:") == -1)
				continue;
			int outData = parseInt(lineArray[2].substring(lineArray[2].indexOf("out:")+4));
			RecvInOutData data = new RecvInOutData();
			data.setInData(inData);
			data.setOutData(outData);
			data.setTimeMs(tmpDate.getTime());
			
			list.add(data);
		}
		
		return list;
	}
	
	/**
	 * 将列表恢复到原始数据样式
	 * @param list
	 * @return
	 */
	public static String parseBackToRawString(List<RecvInOutData> list){
		String str = "";
		if(Utils.isEmpty(list))
			return str;
		for(int i=0;i<list.size();i++){
			RecvInOutData data = list.get(i);
			str += "["+DateProcess.toString(new Date(data.getTimeMs()), "yyyy-MM-dd HH:mm")+"]";
			str += ",in:"+data.getInData()+",out:"+data.getOutData()+";";
		}
		return str;
	}
}
