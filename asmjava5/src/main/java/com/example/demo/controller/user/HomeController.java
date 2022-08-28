package com.example.demo.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.Account;
import com.example.demo.domain.Cart;
import com.example.demo.domain.CartDetail;
import com.example.demo.domain.Category;
import com.example.demo.domain.Product;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.AccountService;
import com.example.demo.service.CartDetailService;
import com.example.demo.service.CartSevice;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;

@Controller
@RequestMapping("home")
public class HomeController {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	CartSevice cartSevice;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	CartDetailService cartDetailService;
	
	
	@ModelAttribute("categories")
	public List<CategoryDTO> getCategories(){
		return categoryService.findAll().stream().map(item ->{
			CategoryDTO dto = new CategoryDTO();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	

	
	@GetMapping("")
	public String home(Model model,HttpSession session) {
		List<Product> list = productService.findAll();
		List<Cart> listcart = cartSevice.findAll(); 
		List<Account> listaccount = accountService.findAll();
		Cart cart = new Cart();
		
		model.addAttribute("products", list);
		int isdemo = 0;
				
		Account account = new Account();
		
		for(int i = 0; i < listaccount.size(); i++) {
			if(listaccount.get(i).getId().equals(session.getAttribute("userId"))) {
				account = listaccount.get(i);
				 isdemo = 100;
			}
		}
		
		for(int i = 0; i < listcart.size(); i++) {
			if(listcart.get(i).getAccount().getId().equals(session.getAttribute("userId")) && listcart.get(i).getIsPresent().equals(true) ) {
				cart = listcart.get(i);
				session.setAttribute("cartId", listcart.get(i).getId());
			}
		}
		
		if(cart.getIsPresent() == null && isdemo == 100) {
			cart.setIsPresent(true);
			cart.setAccount(account);
			cartSevice.save(cart);
		}
		return "user/home";
	}
	
	@GetMapping("viewDetail/{id}")
	public String viewDetail(Model model, @PathVariable("id") Long id) {
		
		Optional<Product> opt = productService.findById(id);
		
		ProductDTO dto = new ProductDTO();
		
		if(opt.isPresent()) {
			Product entity = opt.get();
			BeanUtils.copyProperties(entity, dto);
			
			model.addAttribute("product", dto);
			
			return "user/viewdetail";
		}
		
		return "user/viewdetail";
	}
	
	@GetMapping("getProductByCategory/{id}")
	public String getProductByCategory(Model model, @PathVariable("id") Long categoryid) {
		List<Product> list = productService.findAll();
		List<Product> getProductId = new ArrayList<Product>(); 
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getCategory().getCategory_id() == categoryid) {
				getProductId.add(list.get(i));
				model.addAttribute("products", getProductId);
			}
		}
		
		return "user/home";
	}
	
	@GetMapping("addCart/{product_id}")
	public String addCart(HttpSession session, @PathVariable("product_id") Long product_id) {
		Product product = productService.getById(product_id);
		List<Cart> listcart = cartSevice.findAll(); 
		List<CartDetail> listcartdetail = cartDetailService.findAll();
		

		session.getAttribute("userId");
		session.getAttribute("username");

		
		Cart cart = new Cart();
		
		for(int i = 0; i < listcart.size(); i++) {
			if(listcart.get(i).getAccount().getId().equals(session.getAttribute("userId")) && listcart.get(i).getIsPresent().equals(true) ) {
				cart = listcart.get(i);
			}
		}
		
		CartDetail cartDetail = new CartDetail();
		
		for(int i = 0; i< listcartdetail.size(); i++) {
			if(listcartdetail.get(i).getCart().getIsPresent() == true && listcartdetail.get(i).getProduct().getId().equals(product_id)) {
				
				cartDetail.setId(listcartdetail.get(i).getId());
				cartDetail.setProduct(product);
				cartDetail.setCart(listcartdetail.get(i).getCart());
				cartDetail.setQuantity(listcartdetail.get(i).getQuantity() + 1);
				
				cartDetailService.save(cartDetail);
				return "redirect:/home";
				
			} 
		}
		cartDetail.setCart(cart);
		cartDetail.setProduct(product);
		cartDetail.setQuantity((long) 1);
		
		cartDetailService.save(cartDetail);
		
		
		return "redirect:/home";
	}
	
	@PostMapping("search")
	public String search(@RequestParam("name") String name, Model model) {
		List<Product> list = productService.findAll();
		List<Product> newList = new ArrayList<>();
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getName().equalsIgnoreCase(name)) {
				newList.add(list.get(i));
			}
		}
		
		model.addAttribute("products", newList);
		
		return "user/home";
	}
}
