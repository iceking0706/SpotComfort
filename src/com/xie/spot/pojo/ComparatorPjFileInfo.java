package com.xie.spot.pojo;

import java.util.Comparator;

public class ComparatorPjFileInfo implements Comparator<PjFileInfo>{

	@Override
	public int compare(PjFileInfo o1, PjFileInfo o2) {
		int sub = 0;
		//先根据名称中的时间比较
		if(o1.getTimeInName()>0 && o2.getTimeInName()>0)
			sub = o2.getTimeInName()-o1.getTimeInName();
		//再根据文件的时间比较
		if(sub == 0){
			sub = (int)(o2.getLastModify()-o1.getLastModify());
			//最后根据文件大小进行比较
			if(sub == 0){
				sub = (int)(o2.getLength()-o1.getLength());
			}
		}
		return sub;
	}

}
