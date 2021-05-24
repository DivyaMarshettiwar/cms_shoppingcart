package com.cms.shoppingcart.models;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.shoppingcart.models.data.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer>{

	Admin findByUsername(String username);
}
