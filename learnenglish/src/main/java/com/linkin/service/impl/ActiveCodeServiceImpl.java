package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import com.linkin.dao.ActiveCodeDao;
import com.linkin.dao.UserDao;
import com.linkin.entity.ActiveCode;
import com.linkin.entity.Role;
import com.linkin.entity.User;
import com.linkin.model.ActiveCodeDTO;
import com.linkin.model.SearchActiveCodeDTO;
import com.linkin.model.SearchUnitDTO;
import com.linkin.service.ActiveCodeService;
import com.linkin.service.UnitService;
import com.linkin.utils.PasswordGenerator;
import com.linkin.utils.RoleEnum;

@Transactional
@Service
public class ActiveCodeServiceImpl implements ActiveCodeService {

	@Autowired
	private ActiveCodeDao activeCodeDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UnitService unitService;

	@Override
	public void add(ActiveCodeDTO activeCodeDTO) {
		User user = new User();
		user.setPhone(activeCodeDTO.getCode());
		user.setEnabled(true);
		user.setRole(new Role((long) RoleEnum.MEMBER.getRoleId()));
		user.setPassword(PasswordGenerator.getHashString("1"));

		userDao.add(user);

		ActiveCode activeCode = new ActiveCode();
		activeCode.setUser(user);
		activeCode.setNumberOfUsers(activeCodeDTO.getNumberOfUsers());
		if (activeCodeDTO.getUnits() != null) {
			activeCode.setUnits(new HashSet<Long>(activeCodeDTO.getUnits()));
		}
		activeCodeDao.add(activeCode);

		activeCodeDTO.setId(user.getId());
	}

	@Override
	public void update(ActiveCodeDTO activeCodeDTO) {
		User user = userDao.getById(activeCodeDTO.getId());
		if (user != null) {
			user.setPhone(activeCodeDTO.getCode());
			if (CollectionUtils.isNotEmpty(activeCodeDTO.getUnits())) {
				user.getActiveCode().setUnits(new HashSet<Long>(activeCodeDTO.getUnits()));
			} else {
				user.getActiveCode().setUnits(null);
			}
			userDao.update(user);
		}
	}

	@Override
	public void downCounter(long userId) {
		User user = userDao.getById(userId);
		if (user != null && user.getActiveCode() != null) {
			user.getActiveCode().setNumberOfUsers(user.getActiveCode().getNumberOfUsers() - 1);
			userDao.update(user);
		}
	}

	@Override
	public void upCounter(long userId) {
		User user = userDao.getById(userId);
		if (user != null && user.getActiveCode() != null) {
			user.getActiveCode().setNumberOfUsers(user.getActiveCode().getNumberOfUsers() + 1);
			if (user.getActiveCode().getNumberOfUsers() > 2) {
				throw new DisabledException("exceed try");
			}
			userDao.update(user);
		}
	}

	@Override
	public void delete(Long id) {
		User user = userDao.getById(id);
		if (user != null) {
			userDao.delete(user);
		}
	}

	@Override
	public List<ActiveCodeDTO> find(SearchActiveCodeDTO searchActiveCodeDTO) {
		List<ActiveCode> activeCodes = activeCodeDao.find(searchActiveCodeDTO);
		List<ActiveCodeDTO> activeCodeDTOs = new ArrayList<ActiveCodeDTO>();
		activeCodes.forEach(activeCode -> {
			activeCodeDTOs.add(conver(activeCode));
		});
		return activeCodeDTOs;
	}
	
	@Override
	public ActiveCodeDTO get(String phone) {
		User user = userDao.getByPhone(phone);
		if (user != null && user.getActiveCode() != null) {
			return conver(user.getActiveCode());
		}
		
		return null;
	}

	@Override
	public Long count(SearchActiveCodeDTO searchActiveCodeDTO) {
		return activeCodeDao.count(searchActiveCodeDTO);
	}

	@Override
	public Long countToTal(SearchActiveCodeDTO searchActiveCodeDTO) {
		return activeCodeDao.countTotal(searchActiveCodeDTO);
	}

	private ActiveCodeDTO conver(ActiveCode activeCode) {
		ActiveCodeDTO activeCodeDTO = new ActiveCodeDTO();
		activeCodeDTO.setId(activeCode.getUser().getId());
		activeCodeDTO.setCode(activeCode.getUser().getPhone());
		activeCodeDTO.setNumberOfUsers(activeCode.getNumberOfUsers());
		activeCodeDTO.setUnits(new ArrayList<>(activeCode.getUnits()));
		
		SearchUnitDTO searchUnitDTO = new SearchUnitDTO();
		searchUnitDTO.setStart(null);
		searchUnitDTO.setUnitIds(activeCode.getUnits());
		if (CollectionUtils.isNotEmpty(activeCode.getUnits())) {
			activeCodeDTO.setUnitDTOs(unitService.find(searchUnitDTO));
		} else {
			activeCodeDTO.setUnitDTOs(new ArrayList<>());
		}
		return activeCodeDTO;
	}

}
