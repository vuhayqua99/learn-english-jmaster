package com.linkin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UnitController {

	@GetMapping("/admin/unit/list")
	public String unit() {
		return "admin/unit/list-unit";
	}
}
