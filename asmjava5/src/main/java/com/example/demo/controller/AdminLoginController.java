package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.config.AES;
import com.example.demo.domain.Account;
import com.example.demo.dto.LoginDTO;
import com.example.demo.service.AccountService;

@Controller
@RequestMapping("login")
public class AdminLoginController {
	@Autowired
	AccountService accountService;
	
	@GetMapping("")
	public String LoginPage(Model model) {
		model.addAttribute("account", new LoginDTO());
		
		return "loginRegister/login";
	}
	
	@PostMapping("login")
	public String Login(Model model, @Valid @ModelAttribute("account") LoginDTO dto, BindingResult result, HttpSession session) {
		if(result.hasErrors()) {
			return "redirect:/login";
		}
		
		Account check = accountService.checkLogin(dto.getUsername(), AES.encrypt(dto.getPassword(), "12345"));
		if(check == null) {
			return "redirect:/login";
		}else {
			session.setAttribute("username", check.getUsername());
			session.setAttribute("isAdmin", check.getIsAdmin());
			session.setAttribute("userId", check.getId());
			Object ruri = session.getAttribute("redirect-uri");
			if(ruri != null) {
				session.removeAttribute("redirect-uri");
				if(check.getIsAdmin() == false) {
					return "redirect:/home";
				}else {
					return ("redirect:" + ruri);
				}
				
			}else {	
				if(check.getIsAdmin() == false) {
					return "redirect:/home";
				}else {
					return "redirect:/admin/products";
				}
			}
		}
	}
	
}
