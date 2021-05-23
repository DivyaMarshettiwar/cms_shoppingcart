package com.cms.shoppingcart.models;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.shoppingcart.models.data.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer>{

	Category findByName(String name);

	Category findBySlug(String slug);
}
