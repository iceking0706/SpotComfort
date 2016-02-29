package com.xie.spot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 用户表，目前主要是系统管理员用户
 * @author IcekingT420
 *
 */
@Entity
@Table(name="TUser")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 身份证号码 工号、编号
	 */
	@Column(length = 50)
	private String idNo;
	/**
	 * 姓名
	 */
	@Column(length = 50)
	private String name;
	
	/**
	 * 电话号码/联系电话
	 */
	@Column(length = 50)
	private String tellNo;
	
	/**
	 * 邮箱
	 */
	@Column(length = 50)
	private String email;
	
	/**
	 * 登入名，如果是普通用户，登入名和密码不一定需要指定
	 */
	@Column(length = 50)
	private String username;
	/**
	 * 密码，MD5，默认123456
	 */
	@Column(length = 50)
	private String password = "E10ADC3949BA59ABBE56E057F20F883E";
	
	/**
	 * 用户的类型
	 * 1：普通用户
	 * 2：一般管理员
	 * ...
	 * 99：超级管理员
	 */
	private Integer type;
	
	/**
	 * 判断是否系统管理员，包括超级管理员
	 * @return
	 */
	@Transient
	public boolean isSuperAdmin(){
		if(type.equals(99))
			return true;
		return false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTellNo() {
		return tellNo;
	}

	public void setTellNo(String tellNo) {
		this.tellNo = tellNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
