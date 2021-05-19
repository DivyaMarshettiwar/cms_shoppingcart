package com.cms.shoppingcart.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	
	// this is not going to do anything in the project
	@GetMapping("/someRandomPage")
	public String home() {
		return "home";
	}
}
