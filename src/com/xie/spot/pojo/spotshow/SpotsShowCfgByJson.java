package com.xie.spot.pojo.spotshow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * 从json中获取得到的景点的配置信息
 * @author iceking
 *
 */
public class SpotsShowCfgByJson {
	private static SpotsShowCfgByJson instance = null;
	
	private SpotsShowCfgByJson(){
		
	}
	
	private OneSpotCfg[] spotsCfg;

	public OneSpotCfg[] getSpotsCfg() {
		return spotsCfg;
	}

	public void setSpotsCfg(OneSpotCfg[] spotsCfg) {
		this.spotsCfg = spotsCfg;
	}
	
	/**
	 * 根据序号得到一个景区的配置信息
	 * @param no
	 * @return
	 */
	public OneSpotCfg getOneByNo(int no){
		if(spotsCfg==null || spotsCfg.length==0)
			return null;
		for(OneSpotCfg one: spotsCfg){
			if(one.getSpotNo() == no)
				return one;
		}
		return null;
	}
	
	
	/**
	 * 从json解析过来，json的位置是固定的
	 * @return
	 */
	public static SpotsShowCfgByJson getInstance(){
		if(instance == null){
			try {
				//读取文件，到字符
				InputStream is = SpotsShowCfgByJson.class.getResourceAsStream("/com/xie/spot/pojo/spotshow/SpotsShowCfg.json");
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				//完整的json字符串
				String json = "";
				String line = null;
				while((line=br.readLine())!=null){
					json += line;
				}
				br.close();
				isr.close();
				is.close();
				
				
				//将json转为jsonobject
				JSONObject jsonObject = JSONObject.fromObject(json);
				if(jsonObject==null || jsonObject.isNullObject())
					return null;
				
				//转换为对象, 需要几个属性类的转换定义
				Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
				classMap.put("spotsCfg", OneSpotCfg.class);
				
				Object obj = JSONObject.toBean(jsonObject, SpotsShowCfgByJson.class, classMap);
				if(obj == null || !(obj instanceof SpotsShowCfgByJson))
					return null;
				
				instance = (SpotsShowCfgByJson)obj;
				
			} catch (Exception e) {
				e.printStackTrace();
				instance = null;
			}
		}
		return instance;
	}
	
	@Override
	public String toString() {
		int size = spotsCfg!=null?spotsCfg.length:0;
		StringBuilder sb = new StringBuilder();
		sb.append("===============景区相机配置，数量: "+size+"===============");
		if(size>0){
			for(int i=0;i<size;i++){
				sb.append("\n===============景区: "+(i+1));
				sb.append("\n"+spotsCfg[i].toString());
			}
			
		}
		return sb.toString();
	}
}
