package com.xie.spot.pojo;

/**
 * 给combobox的值使用的
 * 
 * @author IcekingT420
 * 
 */
public class PjValueText {
	private String value;
	private String text;

	public PjValueText() {
	}

	public PjValueText(String value, String text) {
		this.value = value;
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
