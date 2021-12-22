package com.demo.phone.model;

import java.util.regex.Pattern;

public enum Country {

	Cameroon("237", Pattern.compile("\\(237\\)\\ ?[2368]\\d{7,8}$")),
	Ethiopia("251", Pattern.compile("\\(251\\)\\ ?[1-59]\\d{8}$")),
	Morocco("212", Pattern.compile("\\(212\\)\\ ?[5-9]\\d{8}$")),
	Mozambique("258", Pattern.compile("\\(258\\)\\ ?[28]\\d{7,8}$")),
	Uganda("256", Pattern.compile("\\(256\\)\\ ?\\d{9}$"));

	public final String code;

	public final Pattern phonePattern;

	private Country(String code, Pattern phonePattern) {
		this.code = code;
		this.phonePattern = phonePattern;
	}
}
