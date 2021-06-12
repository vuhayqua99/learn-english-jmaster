package com.linkin.api.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkin.model.ActiveCodeDTO;
import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchUnitDTO;
import com.linkin.model.UnitDTO;
import com.linkin.model.UserPrincipal;
import com.linkin.service.ActiveCodeService;
import com.linkin.service.UnitService;
import com.linkin.utils.FileStore;
import com.linkin.utils.RoleEnum;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = -1)
public class UnitAPIController {

	@Autowired
	private UnitService unitService;

	@Autowired
	private ActiveCodeService activeCodeService;

	@PostMapping(value = "/member/unit/list")
	public ResponseDTO<UnitDTO> find(@RequestBody SearchUnitDTO searchUnitDTO) {
		ResponseDTO<UnitDTO> responseDTO = new ResponseDTO<UnitDTO>();
		searchUnitDTO.setLength(100);
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (currentUser.getRoleId() == RoleEnum.MEMBER.getRoleId()) {
			ActiveCodeDTO activeCodeDTO = activeCodeService.get(currentUser.getUsername());
			if (activeCodeDTO != null && CollectionUtils.isNotEmpty(activeCodeDTO.getUnits())) {
				searchUnitDTO.setUnitIds(new HashSet<>(activeCodeDTO.getUnits()));
				
			} else {
				responseDTO.setData(new ArrayList<UnitDTO>());
				responseDTO.setRecordsFiltered(0);
				responseDTO.setRecordsTotal(0);
				return responseDTO;
			}
		}
		/// neu co ma unit
		responseDTO.setData(unitService.find(searchUnitDTO));
		responseDTO.setRecordsFiltered(unitService.count(searchUnitDTO));
		responseDTO.setRecordsTotal(unitService.countTotal(searchUnitDTO));
		return responseDTO;
	}

	@PostMapping(value = "/unit/list")
	public ResponseDTO<UnitDTO> findAll(@RequestBody SearchUnitDTO searchUnitDTO) {
		ResponseDTO<UnitDTO> responseDTO = new ResponseDTO<UnitDTO>();

		/// neu co ma unit
		responseDTO.setData(unitService.find(searchUnitDTO));
		responseDTO.setRecordsFiltered(unitService.count(searchUnitDTO));
		responseDTO.setRecordsTotal(unitService.countTotal(searchUnitDTO));
		return responseDTO;
	}

	@PostMapping("/admin/unit/add")
	public UnitDTO add(@ModelAttribute UnitDTO unitDTO) {
		unitDTO.setImage(FileStore.getFilePath(unitDTO.getImageFile(), "unit-"));
		unitService.add(unitDTO);
		return unitDTO;
	}

	@DeleteMapping("/admin/unit/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		unitService.delete(id);
	}

	@DeleteMapping(value = "/admin/unit/delete-multi/{ids}")
	public void del(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				unitService.delete(id);
			} catch (Exception e) {
			}
		}
	}

	@PostMapping("/admin/unit/update")
	public void update(@ModelAttribute UnitDTO unitDTO) {
		unitDTO.setImage(FileStore.getFilePath(unitDTO.getImageFile(), "unit-"));
		unitService.edit(unitDTO);
	}
}
