package com.linkin.web.controller;

import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.linkin.model.ActiveCodeDTO;
import com.linkin.model.SearchUnitDTO;
import com.linkin.model.UnitDTO;
import com.linkin.model.UserPrincipal;
import com.linkin.service.ActiveCodeService;
import com.linkin.service.UnitService;
import com.linkin.utils.RoleEnum;

@Controller
public class CommonWebController extends BaseWebController {
	@Autowired
	private UnitService unitService;

	@Autowired
	private ActiveCodeService activeCodeService;

	@RequestMapping(value = "/")
	private String index(HttpServletRequest request) {
		return "redirect:/dang-nhap";
	}

	@RequestMapping(value = "/member/home")
	private String home(Model model, HttpServletRequest request) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		SearchUnitDTO searchUnitDTO = new SearchUnitDTO();
		searchUnitDTO.setLength(100);
		if (currentUser.getRoleId() == RoleEnum.MEMBER.getRoleId()) {
			ActiveCodeDTO activeCodeDTO = activeCodeService.get(currentUser.getUsername());
			if (activeCodeDTO != null && CollectionUtils.isNotEmpty(activeCodeDTO.getUnits())) {
				searchUnitDTO.setUnitIds(new HashSet<>(activeCodeDTO.getUnits()));
			}
		}
		List<UnitDTO> unitDTOs = unitService.find(searchUnitDTO);
		request.setAttribute("unitDTOs", unitDTOs);

		return "admin/index";
	}

	@RequestMapping(value = { "/access-deny" })
	private String deny(HttpServletRequest request) {
		return getViewName(request, "admin/error/deny");
	}

}
