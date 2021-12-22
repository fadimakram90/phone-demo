package com.demo.phone.model.dto;

import com.demo.phone.model.Country;
import com.demo.phone.model.PhoneValidation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
public class CustomerDto {
	
	private String name;
	
	private String phone;
	
	private Country country;
	
	private String code;
	
	private PhoneValidation phoneValidity;
	
}
