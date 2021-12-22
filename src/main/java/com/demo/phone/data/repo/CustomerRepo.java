package com.demo.phone.data.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.demo.phone.model.Customer;

@Repository
public interface CustomerRepo extends PagingAndSortingRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

}
