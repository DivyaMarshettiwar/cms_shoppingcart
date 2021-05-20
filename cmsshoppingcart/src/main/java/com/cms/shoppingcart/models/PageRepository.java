package com.cms.shoppingcart.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.shoppingcart.models.data.Page;

public interface PageRepository extends JpaRepository<Page, Integer>{

	List<Page> findAll();
}
