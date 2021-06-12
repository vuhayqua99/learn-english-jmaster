package com.linkin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActiveCodeController {

	@GetMapping("/admin/active-code/list")
	public String activeCodeList() {
		return "admin/active-code/active-code-list";
	}

}
