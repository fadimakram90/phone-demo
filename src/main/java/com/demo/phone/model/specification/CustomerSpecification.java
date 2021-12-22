package com.demo.phone.model.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.demo.phone.model.Customer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomerSpecification implements Specification<Customer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1019993995676520830L;

	private String code;

	@Override
	public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return criteriaBuilder.like(root.<String>get("phone"), "(" + code + ")%");
	}

}
