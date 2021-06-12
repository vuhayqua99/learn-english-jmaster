package com.linkin.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.linkin.exception.NotMatchPasswordException;
import com.linkin.model.GameOneDTO;
import com.linkin.model.GameTwoDTO;
import com.linkin.model.MusicDTO;
import com.linkin.model.SearchGameOneDTO;
import com.linkin.model.SearchGameTwoDTO;
import com.linkin.model.SearchMusicDTO;
import com.linkin.model.SearchVideoDTO;
import com.linkin.model.UserDTO;
import com.linkin.model.UserPrincipal;
import com.linkin.model.VideoDTO;
import com.linkin.service.ActiveCodeService;
import com.linkin.service.GameOneService;
import com.linkin.service.GameTwoService;
import com.linkin.service.MusicService;
import com.linkin.service.UnitService;
import com.linkin.service.UserService;
import com.linkin.service.VideoService;

@Controller
public class UserWebController extends BaseWebController {
	@Autowired
	private UserService userService;

	@Autowired
	private UnitService unitService;

	@Autowired
	private ActiveCodeService activeCodeService;

	@Autowired
	private VideoService videoService;

	@Autowired
	private MusicService musicService;
	
	@Autowired
	private GameOneService gameOneService;
	
	@Autowired
	private GameTwoService gameTwoService;

	@RequestMapping(value = "/dang-nhap")
	private String login(HttpSession httpSession, HttpServletRequest request,
			@RequestParam(required = false, name = "e") String error) {

		if (isLogin()) {
			return "redirect:/member/profile";
		}

		if (error != null) {
			Exception e = (Exception) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
			request.setAttribute("msg", getLoginFailMessage(e));
		}

		return getViewName(request, "client/user/login");
	}

	public String getLoginFailMessage(Exception e) {
		if (e instanceof UsernameNotFoundException) {
			return getMessage("user.not.found");
		}
		if (e instanceof DisabledException) {
			return getMessage("user.disabled");
		}
		if (e instanceof BadCredentialsException) {
			return getMessage("user.bad.password");
		}

		return getMessage("user.not.found");
	}

	@GetMapping(value = "/member/doi-mat-khau")
	private String changePassword(Model model) {
		model.addAttribute("userAccountDTO", new UserDTO());
		return "admin/userAccount/changePassword";
	}

	@PostMapping(value = "/member/doi-mat-khau")
	private String changePassword(Model model, @ModelAttribute(name = "userAccountDTO") UserDTO userDTO,
			BindingResult bindingResult) {
		validateUserPassword(userDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			return "admin/userAccount/changePassword";
		}
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		userDTO.setId(currentUser.getId());
		try {
			userService.changePassword(userDTO);
		} catch (NotMatchPasswordException ex) {
			bindingResult.rejectValue("password", "user.bad.password");
			return "admin/userAccount/changePassword";
		}
		return "redirect:/dang-xuat";
	}

	@GetMapping(value = "/member/profile")
	private String profile(Model model) {
		UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		UserDTO accountDTO = userService.getUserById(currentUser.getId());
		model.addAttribute("userAccountDTO", accountDTO);

		return "admin/userAccount/profile";
	}

	@PostMapping(value = "/member/profile")
	private String profile(Model model, @ModelAttribute(name = "userAccountDTO") UserDTO userDTO,
			BindingResult bindingResult) {
		ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "name", "error.msg.empty.account.name");
		if (bindingResult.hasErrors()) {
			return "admin/userAccount/profile";
		}
		// save database
		userService.updateProfile(userDTO);
		return "redirect:/member/profile";
	}

	private void validateUserPassword(Object object, Errors errors) {
		UserDTO accountDTO = (UserDTO) object;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.msg.empty.account.password");
		if (accountDTO.getPassword().length() < 6 && StringUtils.isNotBlank(accountDTO.getPassword())) {
			errors.rejectValue("password", "error.msg.empty.account.password");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "repassword", "error.msg.empty.account.password");
		if (accountDTO.getRepassword().length() < 6 && StringUtils.isNotBlank(accountDTO.getRepassword())) {
			errors.rejectValue("repassword", "error.msg.empty.account.password");
		}
	}

	@GetMapping("/member/unit/list")
	public String unit() {
		return "client/userUnit/units";
	}

	@GetMapping(value = "/member/video/{id}")
	private String unitsVideo(HttpServletRequest request, @PathVariable(name = "id") Long idUnit,
			@RequestParam(value = "page", required = false) Integer page) {
		SearchVideoDTO searchVideoDTO = new SearchVideoDTO();
		searchVideoDTO.setUnitId(idUnit);

		Integer PAGE_SIZE = 3;
		page = page == null ? 1 : page;

		searchVideoDTO.setStart((page - 1) * PAGE_SIZE);
		searchVideoDTO.setLength(PAGE_SIZE);

		Long totalPage = videoService.count(searchVideoDTO);
		Long pageCount = (totalPage % PAGE_SIZE == 0) ? totalPage / PAGE_SIZE : totalPage / PAGE_SIZE + 1;

		List<Integer> listCount = new ArrayList<Integer>();
		for (int i = 1; i <= pageCount; i++) {
			listCount.add(i);
		}

		List<VideoDTO> videoDTOs = videoService.find(searchVideoDTO);
		request.setAttribute("videoDTOs", videoDTOs);
		request.setAttribute("id", idUnit);
		request.setAttribute("page", page);
		request.setAttribute("listCount", listCount);
		return "client/userUnit/unitVideos";
	}

	@GetMapping(value = "/member/audio/{id}")
	private String unitsAudio(HttpServletRequest request, @PathVariable(name = "id") Long idUnit,
			@RequestParam(value = "page", required = false) Integer page) {

		SearchMusicDTO searchMusicDTO = new SearchMusicDTO();
		searchMusicDTO.setUnitId(idUnit);

		Integer PAGE_SIZE = 3;
		page = page == null ? 1 : page;

		searchMusicDTO.setStart((page - 1) * PAGE_SIZE);
		searchMusicDTO.setLength(PAGE_SIZE);
		List<MusicDTO> musicDTOs = musicService.find(searchMusicDTO);
		request.setAttribute("musicDTOs", musicDTOs);

		Long totalPage = musicService.count(searchMusicDTO);
		Long pageCount = (totalPage % PAGE_SIZE == 0) ? totalPage / PAGE_SIZE : totalPage / PAGE_SIZE + 1;

		List<Integer> listCount = new ArrayList<Integer>();
		for (int i = 1; i <= pageCount; i++) {
			listCount.add(i);
		}
		request.setAttribute("id", idUnit);
		request.setAttribute("page", page);
		request.setAttribute("listCount", listCount);

		return "client/userUnit/unitMusics";
	}
	
	@GetMapping(value = "/member/game-one/{id}")
	private String unitsGameOne(HttpServletRequest request, @PathVariable(name = "id") Long idUnit,
			@RequestParam(value = "page", required = false) Integer page) {

		SearchGameOneDTO searchGameOneDTO = new SearchGameOneDTO();
		searchGameOneDTO.setUnitId(idUnit);

		Integer PAGE_SIZE = 1;
		page = page == null ? 1 : page;

		searchGameOneDTO.setStart((page - 1) * PAGE_SIZE);
		searchGameOneDTO.setLength(PAGE_SIZE);
		List<GameOneDTO> gameOneDTOs = gameOneService.find(searchGameOneDTO);
		request.setAttribute("gameOneDTOs", gameOneDTOs);

		Long totalPage = gameOneService.count(searchGameOneDTO);
		Long pageCount = (totalPage % PAGE_SIZE == 0) ? totalPage / PAGE_SIZE : totalPage / PAGE_SIZE + 1;

		List<Integer> listCount = new ArrayList<Integer>();
		for (int i = 1; i <= pageCount; i++) {
			listCount.add(i);
		}
		request.setAttribute("id", idUnit);
		request.setAttribute("page", page);
		request.setAttribute("listCount", listCount);

		return "client/userUnit/unitGameOnes";
	}
	
	@GetMapping(value = "/member/game-two/{id}")
	private String unitsGameTwo(HttpServletRequest request, @PathVariable(name = "id") Long idUnit,
			@RequestParam(value = "page", required = false) Integer page) {

		SearchGameTwoDTO searchGameTwoDTO = new SearchGameTwoDTO();
		searchGameTwoDTO.setUnitId(idUnit);

		Integer PAGE_SIZE = 1;
		page = page == null ? 1 : page;

		searchGameTwoDTO.setStart((page - 1) * PAGE_SIZE);
		searchGameTwoDTO.setLength(PAGE_SIZE);
		List<GameTwoDTO> gameTwoDTOs = gameTwoService.find(searchGameTwoDTO);
		request.setAttribute("gameTwoDTOs", gameTwoDTOs);

		Long totalPage = gameTwoService.count(searchGameTwoDTO);
		Long pageCount = (totalPage % PAGE_SIZE == 0) ? totalPage / PAGE_SIZE : totalPage / PAGE_SIZE + 1;

		List<Integer> listCount = new ArrayList<Integer>();
		for (int i = 1; i <= pageCount; i++) {
			listCount.add(i);
		}
		request.setAttribute("id", idUnit);
		request.setAttribute("page", page);
		request.setAttribute("listCount", listCount);

		return "client/userUnit/unitGameTwos";
	}

}
