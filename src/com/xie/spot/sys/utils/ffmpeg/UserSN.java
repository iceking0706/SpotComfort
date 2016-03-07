package com.xie.spot.sys.utils.ffmpeg;

import java.util.List;

/**
 * 直播用户可以访问的相机的匹配
 * 
 * @author iceking
 *
 */
public class UserSN {
	/**
	 * 用户名、密码、说明
	 */
	private String user;
	private String pass;
	private String mark;

	/**
	 * 是否全部可以访问
	 */
	private boolean all;

	/**
	 * 该用户允许的sn
	 */
	private List<String> sns;
	
	/**
	 * 判断这个用户是sn的访问权限
	 * @param sn
	 * @return
	 */
	public boolean containsSN(String sn){
		if(all)
			return true;
		//到list中去判断
		if(sns==null || sns.isEmpty())
			return false;
		return sns.contains(sn);
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public List<String> getSns() {
		return sns;
	}

	public void setSns(List<String> sns) {
		this.sns = sns;
	}
	
	@Override
	public String toString() {
		String str = "";
		str += "user: "+user;
		str += ", pass: "+pass;
		str += ", mark: "+mark;
		str += ", all: "+all;
		if(sns!=null && !sns.isEmpty()){
			str += ", sns: ";
			for(int i=0;i<sns.size();i++){
				if(i>0)
					str += ",";
				str += sns.get(i);
			}
		}
		return str;
	}

}
