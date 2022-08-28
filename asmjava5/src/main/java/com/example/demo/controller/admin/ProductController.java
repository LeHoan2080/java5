package com.example.demo.controller.admin;


import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.Account;
import com.example.demo.domain.Category;
import com.example.demo.domain.Product;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;

@Controller
@RequestMapping("admin/products")
public class ProductController {
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@ModelAttribute("categories")
	public List<CategoryDTO> getCategories(){
		return categoryService.findAll().stream().map(item ->{
			CategoryDTO dto = new CategoryDTO();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
	@GetMapping("add")
	public String add(Model model) {
		
		model.addAttribute("product", new ProductDTO());
		
		return "admin/products/addEdit";
	}
	
	@GetMapping("edit/{id}")
	public String edit(Model model, @PathVariable("id") Long id) {
		Optional<Product> opt = productService.findById(id);
		ProductDTO dto = new ProductDTO();
		
		if(opt.isPresent()) {
			Product entity = opt.get();
			BeanUtils.copyProperties(entity, dto);
			
			model.addAttribute("product", dto);
			
			return "admin/products/addEdit";
		}
		
		return "admin/product/addEdit";
	}
	
	@PostMapping("saveUpdate")
	public String saveUpdate(Model model,@Valid @ModelAttribute("product") ProductDTO dto, BindingResult result) {
		
		if(result.hasErrors()) {
			return "";
		}
		Product entity = new Product();
		BeanUtils.copyProperties(dto, entity);
		Category category = new Category();
		category.setCategory_id(dto.getCategory_id());
		
		entity.setCategory(category);

		System.out.println(entity);
		
		productService.save(entity);
		
		return "redirect:/admin/products";
	}
	
	@GetMapping("delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		productService.deleteById(id);
		return "redirect:/admin/products";
	}
	
	@GetMapping("")
	public String list(Model model, @RequestParam("p") Optional<Integer> p) {
		List<Product> list = productService.findAll();
		Pageable pageable = PageRequest.of(p.orElse(0), 5);
		Page<Product> page = productService.findAll(pageable);
		model.addAttribute("products", page);
		return "admin/products/list";
	}
}
