package com.linkin.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.linkin.model.ActiveCodeDTO;
import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchActiveCodeDTO;
import com.linkin.service.ActiveCodeService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = -1)
public class ActiveCodeAPIController {

	@Autowired
	private ActiveCodeService activeCodeService;

	@PostMapping(value = "/admin/active-code/list")
	public ResponseDTO<ActiveCodeDTO> find(@RequestBody SearchActiveCodeDTO searchActiveCodeDTO) {
		ResponseDTO<ActiveCodeDTO> responseDTO = new ResponseDTO<ActiveCodeDTO>();
		responseDTO.setData(activeCodeService.find(searchActiveCodeDTO));
		responseDTO.setRecordsFiltered(activeCodeService.count(searchActiveCodeDTO));
		responseDTO.setRecordsTotal(activeCodeService.countToTal(searchActiveCodeDTO));
		return responseDTO;
	}

	@PostMapping("/admin/active-code/add")
	public @ResponseBody ActiveCodeDTO add(@RequestBody ActiveCodeDTO activeCodeDTO) {
		activeCodeService.add(activeCodeDTO);
		return activeCodeDTO;
	}

	@DeleteMapping("/admin/active-code/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		activeCodeService.delete(id);
	}

	@DeleteMapping(value = "/admin/active-code/delete-multi/{ids}")
	public void del(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				activeCodeService.delete(id);
			} catch (Exception e) {
			}
		}
	}

	@PutMapping("/admin/active-code/update")
	public void updates(@RequestBody ActiveCodeDTO activeCodeDTO) {
		activeCodeService.update(activeCodeDTO);
	}
	
}
