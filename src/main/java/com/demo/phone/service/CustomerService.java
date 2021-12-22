package com.demo.phone.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.demo.phone.common.Constants;
import com.demo.phone.data.repo.CustomerRepo;
import com.demo.phone.model.Country;
import com.demo.phone.model.Customer;
import com.demo.phone.model.PhoneValidation;
import com.demo.phone.model.dto.CustomerDto;
import com.demo.phone.model.specification.CustomerSpecification;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private CustomerUtil util;

	public Map<String, Object> getAllCustomers(int pageNum, int pageSize, Country[] countries,
			PhoneValidation[] phoneValidation) {
		if (phoneValidation == null) {
			return getCountryFilteredCustomers(pageNum, pageSize, countries);
		} else {
			return getCountryAndStatusFilteredCustomers(pageNum, pageSize, countries, phoneValidation);
		}
	}

	private Map<String, Object> getCountryFilteredCustomers(int pageNum, int pageSize, Country[] country) {
		Pageable paging = PageRequest.of(pageNum, pageSize);
		Specification<Customer> spec = null;
		if (country != null && country.length > 0) {
			spec = new CustomerSpecification(country[0].code);
			if (country.length > 1) {
				for (int i = 1; i < country.length; i++) {
					spec = spec.or(new CustomerSpecification(country[i].code));
				}
			}
		}
		Page<Customer> pagedCustomers = spec == null ? customerRepo.findAll(paging)
				: customerRepo.findAll(spec, paging);
		Map<String, Object> result = new HashMap<String, Object>();
		List<CustomerDto> customersList = null;
		long totalItems = 0;
		if (pagedCustomers.hasContent()) {
			customersList = pagedCustomers.getContent().stream().map(customer -> util.getDto(customer))
					.collect(Collectors.toList());
			totalItems = pagedCustomers.getTotalElements();
		}
		result.put(Constants.DATA, customersList);
		result.put(Constants.TOTAL_ITEMS, totalItems);
		return result;
	}

	private Map<String, Object> getCountryAndStatusFilteredCustomers(int pageNum, int pageSize, Country[] countries,
			PhoneValidation[] phoneValidation) {
		Map<String, Object> result = new HashMap<String, Object>();
		long totalItems = 0;
		List<Customer> allCustomers = (List<Customer>) customerRepo.findAll();
		List<PhoneValidation> phList = Arrays.asList(phoneValidation);
		List<CustomerDto> filteredList = allCustomers.stream().map(customer -> util.getDto(customer))
				.filter(customer -> phList.contains(customer.getPhoneValidity())).collect(Collectors.toList());
		if (countries != null) {
			List<Country> countryList = Arrays.asList(countries);
			filteredList = filteredList.stream().filter(customer -> countryList.contains(customer.getCountry()))
					.collect(Collectors.toList());
		}
		totalItems = filteredList.size();
		result.put(Constants.DATA, util.getSubListByPage(filteredList, pageNum + 1, pageSize));
		result.put(Constants.TOTAL_ITEMS, totalItems);
		return result;
	}

}
