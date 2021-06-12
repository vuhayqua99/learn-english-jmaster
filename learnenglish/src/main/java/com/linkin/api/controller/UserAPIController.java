package com.linkin.api.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchUserDTO;
import com.linkin.model.UserDTO;
import com.linkin.model.UserPrincipal;
import com.linkin.service.ActiveCodeService;
import com.linkin.service.UserService;
import com.linkin.utils.RoleEnum;

@CrossOrigin(origins = "*", maxAge = -1)
@RestController
@RequestMapping("/api")
public class UserAPIController {

	@Autowired
	private UserService userService;

	@Autowired
	private ActiveCodeService activeCodeService;

	// login me
	@PostMapping(value = "/member/me")
	private UserDTO me(@RequestParam(name = "token", required = false) String token) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (token != null) {
			UserDTO userDTO = new UserDTO();
			userDTO.setId(currentUser.getId());
			userDTO.setDeviceId(token);
			userService.updateToken(userDTO);
		}
		activeCodeService.upCounter(currentUser.getId());
		return userService.getUserById(currentUser.getId());
	}

	@PostMapping("/user/register")
	public UserDTO register(@RequestBody UserDTO userDTO) {
		userDTO.setEnabled(true);
		userDTO.setRoleId((long) RoleEnum.MEMBER.getRoleId());
		userService.addUser(userDTO);
		// send email
		return userDTO;
	}

	@PostMapping("/staff/user/add")
	public UserDTO addStaffUser(@RequestBody UserDTO userDTO) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();

		if (currentUser.getRoleId() == 2) {
			userDTO.setRoleId((long) RoleEnum.MEMBER.getRoleId());
		}
		userDTO.setEnabled(true);
		userService.addUser(userDTO);
		// send email
		return userDTO;
	}

	@PostMapping(value = "/staff/user/list")
	public ResponseDTO<UserDTO> staffUserList(@RequestBody SearchUserDTO searchUserDTO) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (currentUser.getRoleId() == 2) {
			searchUserDTO.setRoleList(Arrays.asList(3));// customer
			searchUserDTO.setCreatedById(currentUser.getId());
		}

		ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
		responseDTO.setData(userService.find(searchUserDTO));
		responseDTO.setRecordsFiltered(userService.count(searchUserDTO));
		responseDTO.setRecordsTotal(userService.countTotal(searchUserDTO));
		return responseDTO;
	}

	@PutMapping(value = "/admin/user/update")
	public void updateUser(@RequestBody UserDTO userDTO) {
		userService.updateUser(userDTO);
	}

	@DeleteMapping(value = "/admin/user/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		userService.deleteUser(id);
	}

	@DeleteMapping(value = "/admin/user/delete-multi/{ids}")
	public void delete(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				userService.deleteUser(id);
			} catch (Exception e) {

			}
		}
	}

	@PutMapping("/admin/user/change-lock/{id}")
	public void changeLockedUserStatus(@PathVariable(name = "id") Long id) {
		userService.changeAccountLock(id);
	}

	@PutMapping("/member/profile")
	public void profile(@RequestBody UserDTO userDTO) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		userDTO.setId(currentUser.getId());
		userService.updateProfile(userDTO);
	}

	@PutMapping("/member/password")
	public void changePassword(@RequestBody UserDTO userDTO) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		userDTO.setId(currentUser.getId());
		userService.changePassword(userDTO);
	}

	@PutMapping(value = "/member/logout")
	public void logout(@RequestParam(name = "token", required = false) String token) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (token != null) {
			UserDTO userDTO = new UserDTO(currentUser.getId());
			userDTO.setDeviceId(token);
			userService.deleteToken(userDTO);
		}
		activeCodeService.downCounter(currentUser.getId());
	}

}
