package com.example.demo.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.config.AES;
import com.example.demo.domain.Account;
import com.example.demo.domain.Category;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.service.AccountService;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	AccountService accountService;
	
	@GetMapping("add")
	public String add(Model model) {
		
		model.addAttribute("account", new AccountDTO());
		
		return "admin/accounts/addEdit";
	}
	
	@GetMapping("edit/{id}")
	public String edit(Model model, @PathVariable("id") Long id) {
		Optional<Account> opt = accountService.findById(id);
		AccountDTO dto = new AccountDTO();
		
		if(opt.isPresent()) {
			Account entity = opt.get();
			entity.setPassword(AES.decrypt(entity.getPassword(), "12345"));
			BeanUtils.copyProperties(entity, dto);
			
			model.addAttribute("account", dto);
			
			return "admin/accounts/addEdit";
		}
		return "admin/accounts/addEdit";
	}
	
	@PostMapping("saveUpdate")
	public String saveUpdate(Model model, AccountDTO dto) {
		Account entity = new Account();
		dto.setPassword(AES.encrypt(dto.getPassword(), "12345"));
		BeanUtils.copyProperties(dto, entity);
		
		accountService.save(entity);
		return "redirect:/admin/accounts";
	}
	
	@GetMapping("delete/{id}")
	public String delete(@PathVariable("id") Long id) {
		accountService.deleteById(id);
		return "redirect:/admin/accounts";
	}
		
	@GetMapping("")
	public String list(Model model,@RequestParam("p") Optional<Integer> p) {
		Pageable pageable = PageRequest.of(p.orElse(0), 5);
		Page<Account> page = accountService.findAll(pageable);
		List<Account> list = accountService.findAll();
		
		model.addAttribute("accounts",page);
		
		return "admin/accounts/list";
	}
	
}
