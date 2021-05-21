package com.cms.shoppingcart.models;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.shoppingcart.models.data.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Product findBySlug(String slug);

	Product findBySlugAndIdNot(String slug, int id);

}
