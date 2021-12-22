package com.demo.phone.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.demo.phone.model.Country;

@Service
public class CountryCodeMapper {
	
	private Map<String, Country> codeToCountry = new HashMap<String, Country>();
	
	@PostConstruct
	private void postConstruct() {
		for (Country country : Country.values()) {
			codeToCountry.put(country.code, country);
		}
	}
	
	public Country getCountry(String code) {
		return codeToCountry.get(code);
	}

}
