package com.demo.phone.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.phone.model.Country;
import com.demo.phone.model.Customer;
import com.demo.phone.model.PhoneValidation;
import com.demo.phone.model.dto.CustomerDto;

@Service
public class CustomerUtil {
	
	@Autowired
	private CountryCodeMapper mapper;
	
	public CustomerDto getDto(Customer customer) {
		CustomerDto dto = new CustomerDto();
		dto.setName(customer.getName());
		dto.setPhone(customer.getPhone());
		String code = getCode(customer.getPhone());
		dto.setCode(code);
		Country country = mapper.getCountry(code);
		dto.setCountry(country);
		if (country == null) {
			dto.setPhoneValidity(PhoneValidation.INVALID);
		} else {
			dto.setPhoneValidity(country.phonePattern.matcher(customer.getPhone()).find() ? PhoneValidation.VALID
					: PhoneValidation.INVALID);
		}
		return dto;
	}

	private String getCode(String phone) {
		int start = phone.indexOf("(");
		int end = phone.indexOf(")");
		if (start == 0 && end > -1) {
			return phone.substring(start + 1, end);
		}
		return "";
	}
	
	public List<CustomerDto> getSubListByPage(List<CustomerDto> list, int pageNumber, int pageSize) {
		int start = pageSize * (pageNumber - 1);
		int	end = start + pageSize;
		if (list.size() < end + 1) {
			end = list.size();
		}
		end = end < 0 ? 0 : end;
		List<CustomerDto> subList = list.subList(start, end);
		return subList;
	}

}
