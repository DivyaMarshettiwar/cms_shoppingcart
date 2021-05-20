package com.cms.shoppingcart.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cms.shoppingcart.models.CategoryRepository;
import com.cms.shoppingcart.models.data.Category;
import com.cms.shoppingcart.models.data.Page;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

	@Autowired
	private CategoryRepository categoryRepo;
	
	@GetMapping
	public String index(Model model) {
		List<Category> categories  = categoryRepo.findAll();
		model.addAttribute("categories", categories);
		return "admin/categories/index";
	}
	
	@GetMapping("/add")
	public String add(@ModelAttribute Category category) {
		return "admin/categories/add";
	}
	
	
	@PostMapping("/add")
	public String add(@Valid Category category, BindingResult bindingResult,
					RedirectAttributes redirectAttributes, Model model) {
		
		// if it contains error then it will return the error messages
		if(bindingResult.hasErrors()) {
			return "admin/categories/add";
		}
		
		// messages
		redirectAttributes.addFlashAttribute("message", "Category added");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		// to check the slug if there is no slug then generate a new
		String slug =  category.getName().toLowerCase().replace(" ", "-");
											 
		
		// to check if the slug already exists so that there should be no two slugs
		Category categoryExists = categoryRepo.findByName(category.getName());
		
		if(categoryExists != null) {
			redirectAttributes.addFlashAttribute("message", "Category exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("categoryInfo", category);
		}else {
			category.setSlug(slug);
			category.setSorting(100);
			
			categoryRepo.save(category);
		}
		
		return "redirect:/admin/categories/add";
	}
	
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable int id, Model model) {
		Category category = categoryRepo.getOne(id);
		model.addAttribute("category", category);
		return "admin/categories/edit";
	}
	
	
	@PostMapping("/edit")
	public String edit(@Valid Category category, BindingResult bindingResult,
					RedirectAttributes redirectAttributes, Model model) {
		
		Category categoryCurrent = categoryRepo.getOne(category.getId());
		
		// if it contains error then it will return the error messages
		if(bindingResult.hasErrors()) {
			model.addAttribute("categoryName", categoryCurrent.getName());
			return "admin/categories/edit";
		}
		
		// messages
		redirectAttributes.addFlashAttribute("message", "Category edited");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		// to check the slug if there is no slug then generate a new
		String slug =  category.getName().toLowerCase().replace(" ", "-");
													 
				
		// to check if the slug already exists so that there should be no two slugs
		Category categoryExists = categoryRepo.findByName(category.getName());
				
		if(categoryExists != null) {
			redirectAttributes.addFlashAttribute("message", "Category exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
		}else {
			category.setSlug(slug);
					
			categoryRepo.save(category);
		}
		
		return "redirect:/admin/categories/edit/" + category.getId();
	}
	
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
		categoryRepo.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "Category deleted");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		return "redirect:/admin/categories";
	}
}
