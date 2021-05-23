package com.cms.shoppingcart.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cms.shoppingcart.models.CategoryRepository;
import com.cms.shoppingcart.models.ProductRepository;
import com.cms.shoppingcart.models.data.Category;

import com.cms.shoppingcart.models.data.Product;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	@GetMapping
	public String index(Model model, @RequestParam(value="page", required=false) Integer p) {
		
		int perPage = 6;
		int page = (p != null) ? p : 0;
		
		Pageable pageable = PageRequest.of(page, perPage);
		
		Page<Product> products = productRepo.findAll(pageable);
		List<Category> categories = categoryRepo.findAll();
		
		HashMap<Integer, String> cats = new HashMap<>();
		for(Category cat : categories) {
			cats.put(cat.getId(), cat.getName());
		}
		
		model.addAttribute("products", products);
		model.addAttribute("cats", cats);
		
		// for pagination
		long count = productRepo.count();
		double pageCount = Math.ceil((double)count / (double)perPage);
		
		model.addAttribute("pageCount", (int)pageCount);
		model.addAttribute("perPage", perPage);
		model.addAttribute("count", count);
		model.addAttribute("page", page);
		
		return "admin/products/index";
	}
	
	@GetMapping("/add")
	public String add(Product product, Model model) {
		List<Category> categories = categoryRepo.findAll();
		model.addAttribute("categories", categories);
		return "admin/products/add";
	}
	
	
	@PostMapping("/add")
	public String add(@Valid Product product, BindingResult bindingResult, MultipartFile file,
					RedirectAttributes redirectAttributes, Model model) throws IOException {
		
		List<Category> categories = categoryRepo.findAll();
		
		// if it contains error then it will return the error messages
		if(bindingResult.hasErrors()) {
			model.addAttribute("categories", categories);
			return "admin/products/add";
		}
		
		boolean fileOK = false;
		byte[] bytes = file.getBytes();
		String filename = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/" + filename);
		
		// to check whether the file is jpg or png
		if(filename.endsWith("jpg") || filename.endsWith("png")) {
			fileOK = true;
		}
		
		// messages
		redirectAttributes.addFlashAttribute("message", "Product added");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		
		// to check the slug if there is no slug then generate a new
		String slug = product.getName().toLowerCase().replace(" ", "-");								
		
		// to check if the slug already exists so that there should be no two slugs
		Product productExists = productRepo.findBySlug(slug);
		
		if(!fileOK) {
			redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png file");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}
		else if(productExists != null) {
			redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}else {
			product.setSlug(slug);
			product.setImage(filename);
			
			productRepo.save(product);
			Files.write(path, bytes);
		}
		
		return "redirect:/admin/products/add";
	}
	
	
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable int id, Model model) {
		
		Product product = productRepo.getOne(id);
		
		List<Category> categories = categoryRepo.findAll();
		
		model.addAttribute("product", product);
		model.addAttribute("categories", categories);
		
		return "admin/products/edit";
	}
	
	
	@PostMapping("/edit")
	public String edit(@Valid Product product, BindingResult bindingResult, MultipartFile file,
					RedirectAttributes redirectAttributes, Model model) throws IOException {
		
		Product currentProduct = productRepo.getOne(product.getId());
		
		List<Category> categories = categoryRepo.findAll();
		
		// if it contains error then it will return the error messages
		if(bindingResult.hasErrors()) {
			model.addAttribute("productName", currentProduct.getName());
			model.addAttribute("categories", categories);
			return "admin/products/edit";
		}
		
		boolean fileOK = false;
		byte[] bytes = file.getBytes();
		String filename = file.getOriginalFilename();
		Path path = Paths.get("src/main/resources/static/media/" + filename);
		
		// to check  is there is an image that has to be changed
		if(!file.isEmpty()) {
			// to check whether the file is jpg or png
			if(filename.endsWith("jpg") || filename.endsWith("png")) {
				fileOK = true;
			}
		}else {
			fileOK = true;
		}
		
		
		// messages
		redirectAttributes.addFlashAttribute("message", "Product edited");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		
		// to check the slug if there is no slug then generate a new
		String slug = product.getName().toLowerCase().replace(" ", "-");								
		
		// to check if the slug already exists so that there should be no two slugs
		Product productExists = productRepo.findBySlugAndIdNot(slug, product.getId());
		
		if(!fileOK) {
			redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png file");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}
		else if(productExists != null) {
			redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
			redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
			redirectAttributes.addFlashAttribute("product", product);
		}else {
			product.setSlug(slug);
			
			// to check if image is uploaded
			if(!file.isEmpty()) {
				Path path2 = Paths.get("src/main/resources/static/media/" + currentProduct.getImage());
				
				// to delete the previous image when new file is uploaded
				Files.delete(path2);
				
				// to set the new image
				product.setImage(filename);
				
				// to write that new image file as uploaded
				Files.write(path, bytes);
			}else {
				product.setImage(currentProduct.getImage());
			}
					
			productRepo.save(product);
			
		}
		
		return "redirect:/admin/products/edit/" + product.getId();
	}
	
	
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) throws IOException {
		
		Product product = productRepo.getOne(id);
		Product currentProduct = productRepo.getOne(product.getId());
		
		//Path path2 = Paths.get("src/main/resources/static/media/" + currentProduct.getImage());
		//Files.delete(path2);
		
		productRepo.deleteById(id);
		
		redirectAttributes.addFlashAttribute("message", "Product deleted");
		redirectAttributes.addFlashAttribute("alertClass", "alert-success");
		
		return "redirect:/admin/products";
	}
}
