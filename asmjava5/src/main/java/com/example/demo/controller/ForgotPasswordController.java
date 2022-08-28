
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.config.AES;
import com.example.demo.domain.Account;
import com.example.demo.dto.AccountDTO;
import com.example.demo.service.AccountService;

@Controller
@RequestMapping("forgot")
public class ForgotPasswordController {
	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
	AccountService accountService;
	
	@GetMapping("")
	public String pageforgot() {
		return "loginRegister/forgotPass";
	}
	
	@PostMapping("forgot")
	public String forgot(@RequestParam("email") String mail) {
		List<Account> list = accountService.findAll();
		System.out.println(list);
		
		AccountDTO dto = new AccountDTO();
		
		for(int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i).getEmail());
			if(list.get(i).getEmail().equals(mail)) {
				BeanUtils.copyProperties(list.get(i), dto);
			}
		}
		
		if(dto.getFullName() == null) {
			return "loginRegister/forgotPass";
		}
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(dto.getEmail());
		msg.setSubject(dto.getFullName());
		msg.setText("mật khẩu: " + AES.decrypt(dto.getPassword(), "12345"));
		javaMailSender.send(msg);
		
		return "redirect:/login";
	}
}
