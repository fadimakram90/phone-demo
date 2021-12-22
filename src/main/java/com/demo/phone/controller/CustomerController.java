package com.demo.phone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.phone.common.Constants;
import com.demo.phone.model.Country;
import com.demo.phone.model.PhoneValidation;
import com.demo.phone.model.dto.CustomerDto;
import com.demo.phone.service.CustomerService;

@RestController
@CrossOrigin(origins = { "http://localhost:3000" })
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping
	public ResponseEntity<List<CustomerDto>> listAllContacts(@RequestParam(defaultValue = "0") Integer pageNum,
			@RequestParam(defaultValue = "25") Integer pageSize, @RequestParam(required = false) Country[] country,
			@RequestParam(required = false) PhoneValidation[] phoneValidation) {
		Map<String, Object> result = customerService.getAllCustomers(pageNum, pageSize, country, phoneValidation);
		List<CustomerDto> customerList = (List<CustomerDto>) result.get(Constants.DATA);
		Long totalItems = (Long) result.get(Constants.TOTAL_ITEMS);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(Constants.TOTAL_ITEMS, totalItems.toString());
		return new ResponseEntity<List<CustomerDto>>(customerList, responseHeaders, HttpStatus.OK);
	}

}
