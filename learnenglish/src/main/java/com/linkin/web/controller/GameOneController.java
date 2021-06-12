package com.linkin.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.linkin.model.SearchUnitDTO;
import com.linkin.model.UnitDTO;
import com.linkin.service.UnitService;
@Controller
public class GameOneController {
	@Autowired
	private UnitService unitService;

	@GetMapping("/admin/game-one/list")
	private String music(Model model) {
		SearchUnitDTO searchUnitDTO = new SearchUnitDTO();
		searchUnitDTO.setStart(null);
		List<UnitDTO> units = unitService.find(searchUnitDTO);
		model.addAttribute("listUnit", units);
		return "admin/gameOne/gameOne-list";
	}
}
