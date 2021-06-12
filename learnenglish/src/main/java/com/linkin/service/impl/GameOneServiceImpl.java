package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkin.dao.GameOneDao;
import com.linkin.entity.GameOne;
import com.linkin.entity.Unit;
import com.linkin.model.GameOneDTO;
import com.linkin.model.SearchGameOneDTO;
import com.linkin.service.GameOneService;

@Service
@Transactional
public class GameOneServiceImpl implements GameOneService {
	@Autowired
	private GameOneDao gameOneDao;

	@Override
	public void add(GameOneDTO gameOneDTO) {
		GameOne gameOne = new GameOne();
		gameOne.setAudio(gameOneDTO.getAudio());
		gameOne.setImage(gameOneDTO.getImage());
		gameOne.setName(gameOneDTO.getName());
		gameOne.setUnit(new Unit(gameOneDTO.getUnitId()));

		gameOneDao.add(gameOne);
	}

	@Override
	public void edit(GameOneDTO gameOneDTO) {
		GameOne gameOne = gameOneDao.getById(gameOneDTO.getId());
		if (gameOne != null) {
			if (StringUtils.isNotBlank(gameOneDTO.getAudio())) {
				gameOne.setAudio(gameOneDTO.getAudio());
			}
			if (StringUtils.isNotBlank(gameOneDTO.getImage())) {
				gameOne.setImage(gameOneDTO.getImage());
			}
			gameOne.setName(gameOneDTO.getName());
			gameOne.setUnit(new Unit(gameOneDTO.getUnitId()));
			gameOneDao.update(gameOne);
		}
	}

	@Override
	public void delete(Long id) {
		GameOne gameOne = gameOneDao.getById(id);
		if (gameOne != null) {
			gameOneDao.delete(gameOne);
		}
	}

	@Override
	public GameOneDTO getById(Long id) {
		GameOne GameOne = gameOneDao.getById(id);
		if (GameOne != null) {
			return convent(GameOne);
		}
		return null;
	}

	@Override
	public Long count(SearchGameOneDTO searchGameOneDTO) {
		return gameOneDao.count(searchGameOneDTO);
	}

	@Override
	public Long countTotal(SearchGameOneDTO searchGameOneDTO) {
		return gameOneDao.countTotal(searchGameOneDTO);
	}

	private GameOneDTO convent(GameOne gameOne) {
		GameOneDTO gameOneDTO = new GameOneDTO();
		gameOneDTO.setId(gameOne.getId());
		gameOneDTO.setAudio(gameOne.getAudio());
		gameOneDTO.setImage(gameOne.getImage());
		gameOneDTO.setName(gameOne.getName());
		gameOneDTO.setUnitId(gameOne.getUnit().getId());
		gameOneDTO.setUnitName(gameOne.getUnit().getName());

		return gameOneDTO;

	}

	@Override
	public List<GameOneDTO> find(SearchGameOneDTO searchGameOneDTO) {
		List<GameOne> gameOnes = gameOneDao.find(searchGameOneDTO);
		List<GameOneDTO> gameOneDTOs = new ArrayList<GameOneDTO>();
		gameOnes.forEach(gameOne -> {
			gameOneDTOs.add(convent(gameOne));
		});

		return gameOneDTOs;
	}

}
