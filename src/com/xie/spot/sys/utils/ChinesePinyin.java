package com.xie.spot.sys.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class ChinesePinyin {
	
	/**
	 * 得到单个汉字的拼音
	 * @param word
	 * @return
	 */
	public static String getPinyin1(String hanyu){
		try {
			if(hanyu == null || hanyu.equals(""))
				return "";
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			format.setVCharType(HanyuPinyinVCharType.WITH_V);
			
			char[] array = hanyu.toCharArray();
			String str = "";
			for(int i=0;i<array.length;i++){
				String[] strings = PinyinHelper.toHanyuPinyinStringArray(array[i], format);
				if(strings==null || strings.length == 0)
					continue;
				//首字母大写
				char[] tmpcc = strings[0].toCharArray();
				tmpcc[0] -= 32;
				str += String.valueOf(tmpcc);
			}
			
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 针对pinyin1的结果，至找出其中的首字母
	 * @param pinyin1
	 * @return
	 */
	public static String getSzm(String pinyin1){
		if(pinyin1 == null || pinyin1.equals(""))
			return "";
		char[] cc = pinyin1.toCharArray();
		String str = "";
		for(int i=0;i<cc.length;i++){
			if(cc[i]>='A' && cc[i]<='Z')
				str += String.valueOf(cc[i]);
		}
		return str;
	}
	
}
