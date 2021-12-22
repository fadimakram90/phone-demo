package com.demo.phone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.demo.phone.data.repo.CustomerRepo;
import com.demo.phone.model.Country;
import com.demo.phone.model.Customer;
import com.demo.phone.model.PhoneValidation;
import com.demo.phone.service.CustomerUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PhoneDemoApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CustomerRepo repo;

	@Autowired
	private CustomerUtil customerUtil;

	@Test
	void contextLoads() {
		assertThat(repo).isNotNull();
	}

	@Test
	void testGivenCustomer_whenGetCustomer_thenStatus200() throws Exception {
		Customer c = new Customer();
		c.setName("Andrew Herbert");
		c.setPhone("(256) 714660221");
		repo.save(c);
		mvc.perform(get("/customer"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is(c.getName())))
				.andExpect(jsonPath("$[0].phone", is(c.getPhone())))
				.andExpect(jsonPath("$[0].country", is(Country.Uganda.toString())))
				.andExpect(jsonPath("$[0].code", is("256")))
				.andExpect(jsonPath("$[0].phoneValidity", is(PhoneValidation.VALID.toString())));
		repo.deleteAll();
	}

	@Test
	void test_validPhone() throws Exception {
		Customer c = new Customer();
		final String[] validPhones = new String[] { "(258) 258473658", "(237) 69485746", "(251) 985746385",
				"(212) 749302947", "(256) 123456789" };
		for (String phone : validPhones) {
			c.setPhone(phone);
			assertEquals(customerUtil.getDto(c).getPhoneValidity(), PhoneValidation.VALID);
		}
	}

	@Test
	void test_invalidPhone() throws Exception {
		Customer c = new Customer();
		final String[] invalidPhones = new String[] { "(237) 4546545369", "(251) 812345678", "(212) 64859604",
				"(258) 658493049", "(256) 85749590" };
		for (String phone : invalidPhones) {
			c.setPhone(phone);
			assertEquals(customerUtil.getDto(c).getPhoneValidity(), PhoneValidation.INVALID);
		}
	}

	@Test
	void test_countryMapping() throws Exception {
		Customer c = new Customer();
		c.setPhone("(258) 823747618");
		assertEquals(customerUtil.getDto(c).getCountry(), Country.Mozambique);
		c.setPhone("(256) 704244430");
		assertEquals(customerUtil.getDto(c).getCountry(), Country.Uganda);
		c.setPhone("(237) 697151594");
		assertEquals(customerUtil.getDto(c).getCountry(), Country.Cameroon);
		c.setPhone("(212) 654642448");
		assertEquals(customerUtil.getDto(c).getCountry(), Country.Morocco);
		c.setPhone("(251) 914701723");
		assertEquals(customerUtil.getDto(c).getCountry(), Country.Ethiopia);
		c.setPhone("(242) 914701723");
		assertNull(customerUtil.getDto(c).getCountry());
	}
	
	@Test
	void test_Pagination() throws Exception {
		List<Customer> entities = getListOfCustomers();
		int totalItems = entities.size();
		int pageSize = 6;
		int completePages = totalItems / pageSize;
		int numOfLastPageItems = totalItems % pageSize;
		try {
			repo.saveAll(entities);
			int pageCount = 0;
			for (; pageCount < completePages; pageCount++) {
				mvc.perform(get("/customer").param("pageNum", "" + pageCount).param("pageSize", "" + pageSize))
						.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(pageSize)));
			}
			if (numOfLastPageItems > 0) {
				mvc.perform(get("/customer").param("pageNum", "" + pageCount).param("pageSize", "" + pageSize))
						.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(numOfLastPageItems)));
			}
		} finally {
			repo.deleteAll();
		}
	}
	
	@Test
	void test_CountryAndStateSearch() throws Exception {
		List<Customer> entities = getListOfCustomers();
		int pageSize = entities.size();
		try {
			repo.saveAll(entities);
			
			assertOneCountrySearch(pageSize, Country.Cameroon.toString(), 2);
			assertOneCountrySearch(pageSize, Country.Mozambique.toString(), 3);
			assertOneCountrySearch(pageSize, Country.Uganda.toString(), 5);
			assertOneCountrySearch(pageSize, Country.Morocco.toString(), 5);
			assertOneCountrySearch(pageSize, Country.Ethiopia.toString(), 5);
			
			assertMultipleCountrySearch(pageSize,
					new String[] { Country.Morocco.toString(), Country.Mozambique.toString() }, 8);
			assertMultipleCountrySearch(pageSize,
					new String[] { Country.Cameroon.toString(), Country.Ethiopia.toString() }, 7);
			assertMultipleCountrySearch(pageSize,
					new String[] { Country.Morocco.toString(), Country.Uganda.toString(), Country.Cameroon.toString() }, 12);
			
			assertMultipleCountryAndStatusSearch(pageSize,
					new String[] { Country.Cameroon.toString()}, 1, PhoneValidation.VALID);
			assertMultipleCountryAndStatusSearch(pageSize,
					new String[] { Country.Morocco.toString(), Country.Uganda.toString() }, 5, PhoneValidation.VALID);
			assertMultipleCountryAndStatusSearch(pageSize,
					new String[] { Country.Ethiopia.toString(), Country.Mozambique.toString() }, 3, PhoneValidation.INVALID);
		} finally {
			repo.deleteAll();
		}
	}
	
	void assertOneCountrySearch(int pageSize, String country, int expectedSize) throws Exception {
		mvc.perform(get("/customer").param("pageSize", "" + pageSize).param("country",
				country)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(expectedSize)))
				.andExpect(jsonPath("$[*].country", everyItem(is(country))));
	}
	
	void assertMultipleCountrySearch(int pageSize, String[] country, int expectedSize) throws Exception {
		mvc.perform(get("/customer").param("pageSize", "" + pageSize).param("country",
				country)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(expectedSize)))
				.andExpect(jsonPath("$[*].country", hasItems(country)));
	}
	
	void assertMultipleCountryAndStatusSearch(int pageSize, String[] country, int expectedSize,
			PhoneValidation validationStatus) throws Exception {
		mvc.perform(get("/customer").param("pageSize", "" + pageSize).param("country", country).param("phoneValidation",
				validationStatus.toString())).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(expectedSize)))
				.andExpect(jsonPath("$[*].country", hasItems(country)))
				.andExpect(jsonPath("$[*].phoneValidity", everyItem(is(validationStatus.toString()))));
	}
	
	List<Customer> getListOfCustomers() {
		List<Customer> customers = new ArrayList<Customer>();
		customers.add(new Customer("Customer01_Mozambique_VALID", "(258) 258473658"));
		customers.add(new Customer("Customer02_Cameroon_VALID", "(237) 69485746"));
		customers.add(new Customer("Customer03_Ethiopia_VALID", "(251) 985746385"));
		customers.add(new Customer("Customer04_Morocco_VALID", "(212) 749302947"));
		customers.add(new Customer("Customer05_Uganda_VALID", "(256) 123456789"));
		customers.add(new Customer("Customer06_Cameroon_INVALID", "(237) 4546545369"));
		customers.add(new Customer("Customer07_Ethiopia_INVALID", "(251) 812345678"));
		customers.add(new Customer("Customer08_Morocco_INVALID", "(212) 64859604"));
		customers.add(new Customer("Customer09_Mozambique_INVALID", "(258) 658493049"));
		customers.add(new Customer("Customer10_Uganda_INVALID", "(256) 85749590"));
		customers.add(new Customer("Customer11_Morocco_INVALID", "(212) 6546545369"));
		customers.add(new Customer("Customer12_Morocco_VALID", "(212) 691933626"));
		customers.add(new Customer("Customer13_Morocco_VALID", "(212) 633963130"));
		customers.add(new Customer("Customer14_Mozambique_INVALID", "(258) 84330678235"));
		customers.add(new Customer("Customer15_Uganda_INVALID", "(256) 7503O6263"));
		customers.add(new Customer("Customer16_Uganda_INVALID", "(256) 7771031454"));
		customers.add(new Customer("Customer17_Uganda_VALID", "(256) 714660221"));
		customers.add(new Customer("Customer18_Ethiopia_VALID", "(251) 911168450"));
		customers.add(new Customer("Customer19_Ethiopia_VALID", "(251) 924418461"));
		customers.add(new Customer("Customer20_Ethiopia_VALID", "(251) 988200000"));
		return customers;
	}

}
