package com.cms.shoppingcart.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cms.shoppingcart.models.PageRepository;
import com.cms.shoppingcart.models.data.Page;

@Controller
@RequestMapping("/admin/pages")
public class AdminPagesController {
	
	@Autowired
	private PageRepository pageRepo;
	
	/*
	 * public AdminPagesController(PageRepository pageRepo) { this.pageRepo =
	 * pageRepo; }
	 */

	@GetMapping
	public String index(Model model) {
		
		List<Page> pages = pageRepo.findAll();
		
		model.addAttribute("pages", pages);
		
		return "admin/pages/index";
	}
}
