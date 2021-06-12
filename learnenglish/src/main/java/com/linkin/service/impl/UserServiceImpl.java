package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.linkin.dao.ActiveCodeDao;
import com.linkin.dao.UserDao;
import com.linkin.entity.ActiveCode;
import com.linkin.entity.Role;
import com.linkin.entity.User;
import com.linkin.exception.NotMatchPasswordException;
import com.linkin.model.SearchUserDTO;
import com.linkin.model.UserDTO;
import com.linkin.model.UserPrincipal;
import com.linkin.service.UserService;
import com.linkin.utils.DateTimeUtils;
import com.linkin.utils.PasswordGenerator;
import com.linkin.utils.RoleEnum;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Autowired
	private ActiveCodeDao activeCodeDao;

	@Override
	public void addUser(UserDTO userDTO) {
		User user = new User();
		user.setName(userDTO.getName());
		user.setPhone(userDTO.getPhone());
		user.setPassword(PasswordGenerator.getHashString(userDTO.getPassword()));
		user.setEnabled(userDTO.getEnabled());
		user.setRole(new Role(userDTO.getRoleId()));
		user.setAddress(userDTO.getAddress());

		userDao.add(user);

		if (userDTO.getRoleId() == RoleEnum.MEMBER.getRoleId()) {
			ActiveCode activeCode = new ActiveCode();
			activeCode.setUser(user);
			activeCode.setNumberOfUsers(0);
			activeCodeDao.add(activeCode);
		}

		userDTO.setId(user.getId());
	}

	@Override
	public void updateUser(UserDTO userDTO) {
		User user = userDao.getById(userDTO.getId());
		if (user != null) {
			user.setName(userDTO.getName());
			user.setPhone(userDTO.getPhone());
			user.setRole(new Role(userDTO.getRoleId()));
			user.setAddress(userDTO.getAddress());
			userDao.update(user);
			expireUserSessions(user.getPhone());
		}
	}

	@Override
	public void updateProfile(UserDTO userDTO) {
		User user = userDao.getById(userDTO.getId());
		if (user != null) {
			user.setName(userDTO.getName());
			user.setAddress(userDTO.getAddress());
			userDao.update(user);
		}
	}

	@Override
	public void deleteUser(Long id) {
		User user = userDao.getById(id);
		if (user != null) {
			userDao.delete(user);
			expireUserSessions(user.getPhone());
		}
	}

	@Override
	public List<UserDTO> find(SearchUserDTO searchUserDTO) {
		List<User> users = userDao.find(searchUserDTO);
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		users.forEach(user -> {
			userDTOs.add(convertToDTO(user));
		});

		return userDTOs;
	}

	@Override
	public long count(SearchUserDTO searchUserDTO) {
		return userDao.count(searchUserDTO);
	}

	@Override
	public long countTotal(SearchUserDTO searchUserDTO) {
		return userDao.countTotal(searchUserDTO);
	}

	@Override
	public UserDTO getUserById(Long id) {
		User user = userDao.getById(id);
		if (user != null) {
			return convertToDTO(user);
		}
		return null;
	}

	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setName(user.getName());
		userDTO.setPhone(user.getPhone());
		userDTO.setRoleId(user.getRole().getId());
		userDTO.setAddress(user.getAddress());
		userDTO.setEnabled(user.getEnabled());
		userDTO.setCreatedDate(DateTimeUtils.formatDate(user.getCreatedDate(), DateTimeUtils.DD_MM_YYYY_HH_MM));

		return userDTO;
	}

	@Override
	public void changeAccountLock(long id) {
		
		User user = userDao.getById(id);
		if (user != null) {
			user.setEnabled(!user.getEnabled());
			userDao.update(user);
			expireUserSessions(user.getPhone());
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.getByPhone(username.trim());

		if (user == null) {
			throw new UsernameNotFoundException("not found");
		}

		if (user.getActiveCode() != null) {
			if (user.getActiveCode().getNumberOfUsers() > 2) {
				throw new DisabledException("exceed try");
			}
		}

		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

		UserPrincipal userPrincipal = new UserPrincipal(user.getPhone(), user.getPassword(), user.getEnabled(), true,
				true, true, authorities);
		userPrincipal.setId(user.getId());
		userPrincipal.setName(user.getName());
		userPrincipal.setRoleId(user.getRole().getId());

		// update couter
		return userPrincipal;
	}

	@Override
	public void changePassword(UserDTO userDTO) {
		User user = userDao.getById(userDTO.getId());
		if (user != null && PasswordGenerator.checkHashStrings(userDTO.getPassword(), user.getPassword())) {
			user.setPassword(PasswordGenerator.getHashString(userDTO.getRepassword()));
			userDao.update(user);
			expireUserSessions(user.getPhone());
		} else {
			throw new NotMatchPasswordException("wrong password");
		}
	}

	@Override
	public void resetPassword(UserDTO userDTO) {
		User user = userDao.getByPhone(userDTO.getPhone());
		if (user != null) {
			String password = PasswordGenerator.generateRandomPassword();
			user.setPassword(PasswordGenerator.getHashString(password));
			userDao.update(user);

			expireUserSessions(user.getPhone());
		}
	}

	@Override
	public void setupUserPassword(UserDTO userDTO) {
		User user = userDao.getById(userDTO.getId());
		if (user != null) {
			user.setPassword(PasswordGenerator.getHashString(userDTO.getPassword()));
			userDao.update(user);

			expireUserSessions(user.getPhone());
		}
	}

	private void expireUserSessions(String username) {
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			if (principal instanceof UserPrincipal) {
				UserPrincipal userDetails = (UserPrincipal) principal;
				if (userDetails.getUsername().equals(username)) {
					for (SessionInformation information : sessionRegistry.getAllSessions(userDetails, true)) {
						information.expireNow();
					}
				}
			}
		}
	}

	@Override
	public void updateToken(UserDTO userDTO) {
		User user = userDao.getById(userDTO.getId());
		if (user != null && userDTO.getDeviceId() != null) {
			String playerIds = user.getDeviceId();
			if (StringUtils.isNotBlank(playerIds)) {
				if (!playerIds.contains(userDTO.getDeviceId())) {
					playerIds += ";" + userDTO.getDeviceId();
					user.setDeviceId(playerIds);
				}
			} else {
				user.setDeviceId(userDTO.getDeviceId());
			}
			userDao.update(user);
		}
	}

	@Override
	public void deleteToken(UserDTO userDTO) {
		User user = userDao.getById(userDTO.getId());
		if (user != null && userDTO.getDeviceId() != null) {
			String playerIds = user.getDeviceId();
			if (playerIds != null) {
				if (playerIds.contains(";" + userDTO.getDeviceId())) {
					playerIds = playerIds.replace(";" + userDTO.getDeviceId(), "");
					user.setDeviceId(playerIds);
				} else if (playerIds.contains(userDTO.getDeviceId())) {
					playerIds = playerIds.replace(userDTO.getDeviceId(), "");
					user.setDeviceId(playerIds);
				}
				userDao.update(user);
			}
		}

	}
}
