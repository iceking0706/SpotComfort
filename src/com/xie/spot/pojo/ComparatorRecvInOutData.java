package com.xie.spot.pojo;

import java.util.Comparator;

import com.suyou.singnalway.RecvInOutData;

/**
 * 对接收到的相机原始in out数据进行排序
 * @author IcekingT420
 *
 */
public class ComparatorRecvInOutData implements Comparator<RecvInOutData>{

	@Override
	public int compare(RecvInOutData o1, RecvInOutData o2) {
		return (int)(o1.getTimeMs()-o2.getTimeMs());
	}

}
