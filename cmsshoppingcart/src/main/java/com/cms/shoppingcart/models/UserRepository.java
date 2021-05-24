package com.cms.shoppingcart.models;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.shoppingcart.models.data.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);
}
