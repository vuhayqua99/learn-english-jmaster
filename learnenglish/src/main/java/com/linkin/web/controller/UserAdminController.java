package com.linkin.web.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.linkin.model.SearchUserDTO;
import com.linkin.model.UserDTO;
import com.linkin.service.UserService;

@Controller
@RequestMapping(value = "/admin")
public class UserAdminController extends BaseWebController {

	@Autowired
	private UserService userService;

	@GetMapping("/accounts")
	public String listUser(Model model) {
		return "admin/userAccount/listUser";
	}

	@GetMapping("/account/reset-password/{id}")
	public String resetPassword(Model model, @PathVariable(name = "id") Long id) {
		UserDTO userDTO = userService.getUserById(id);
		model.addAttribute("userAccountDTO", userDTO);
		return "admin/userAccount/resetPassword";
	}

	@PostMapping("/account/reset-password")
	public String resetPassword(@ModelAttribute(name = "userAccountDTO") UserDTO userDTO, BindingResult bindingResult) {
		validateUserPassword(userDTO, bindingResult);

		if (bindingResult.hasErrors()) {
			return "admin/userAccount/resetPassword";
		}
		// all user - check pass code while change password
		userService.setupUserPassword(userDTO);
		return "redirect:/admin/accounts";
	}

	@PostMapping("/admin/user/excel/export")
	public @ResponseBody String exportExcel(@RequestBody SearchUserDTO searchUserDTO) {
		searchUserDTO.setStart(null);
		List<UserDTO> userDTOs = userService.find(searchUserDTO);

		// add date to name of exel file
		String fileName = "nguoiDung.xlsx";

		System.out.println("Create file excel");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("nguoiDung");

		int rowNum = 0;
		Row firstRow = sheet.createRow(rowNum++);
		Cell c0 = firstRow.createCell(0);
		c0.setCellValue("id");
		Cell c1 = firstRow.createCell(1);
		c1.setCellValue("Tên");
		Cell c2 = firstRow.createCell(2);
		c2.setCellValue("số điện thoại");
		Cell c3 = firstRow.createCell(3);
		c3.setCellValue("địa chỉ");
		Cell c4 = firstRow.createCell(4);
		c4.setCellValue("vai trò");

		for (UserDTO userDTO : userDTOs) {
			Row row = sheet.createRow(rowNum++);
			Cell cell0 = row.createCell(0);
			cell0.setCellValue(userDTO.getId());
			Cell cell1 = row.createCell(1);
			cell1.setCellValue(userDTO.getName());
			Cell cell2 = row.createCell(2);
			cell2.setCellValue(userDTO.getPhone());
			Cell cell3 = row.createCell(3);
			cell3.setCellValue(userDTO.getAddress());
			Cell cell4 = row.createCell(4);
			cell4.setCellValue(getStatus(userDTO.getRoleId()));
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(fileName);
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}

	public String getStatus(long status) {
		if (status == 1) {
			return "ADMIN";
		} else if (status == 2) {
			return "Nhan Vien";
		} else {
			return "Khach hang";
		}
	}

	private void validateUserPassword(Object object, Errors errors) {
		UserDTO accountDTO = (UserDTO) object;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.msg.empty.account.password");
		if (accountDTO.getPassword().length() < 6 && StringUtils.isNotBlank(accountDTO.getPassword())) {
			errors.rejectValue("password", "error.msg.empty.account.password");
		}
	}

}
