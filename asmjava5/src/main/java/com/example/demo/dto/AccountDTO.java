package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
	private Long id;
	private String username;
	private String password;
	private String fullName;
	private String email;
	private String image;
	private Boolean isAdmin;
}
