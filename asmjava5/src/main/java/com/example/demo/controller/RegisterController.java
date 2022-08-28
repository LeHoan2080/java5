package com.example.demo.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.config.AES;
import com.example.demo.domain.Account;
import com.example.demo.dto.AccountDTO;
import com.example.demo.service.AccountService;

@Controller
@RequestMapping("register")
public class RegisterController {
	@Autowired
	AccountService accountService;
	
	@GetMapping("")
	public String register(Model model){
		model.addAttribute("account", new AccountDTO());
		return "loginRegister/register";
	}
	
	@PostMapping("register")
	public String saveRegister(Model model, AccountDTO dto) {
		Account entity = new Account();
		dto.setIsAdmin(false);
		dto.setPassword(AES.encrypt(dto.getPassword(), "12345"));
		BeanUtils.copyProperties(dto, entity);
		
		accountService.save(entity);
		return "redirect:/login";
	}
	}
