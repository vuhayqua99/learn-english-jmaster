package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linkin.dao.GameTwoDao;
import com.linkin.entity.GameTwo;
import com.linkin.entity.Unit;
import com.linkin.model.GameTwoDTO;
import com.linkin.model.SearchGameTwoDTO;
import com.linkin.model.UnitDTO;
import com.linkin.service.GameTwoService;

@Transactional
@Service
public class GameTwoServiceImpl implements GameTwoService {

	@Autowired
	private GameTwoDao gameTwoDao;

	@Override
	public void add(GameTwoDTO gameTwoDTO) {
		GameTwo gameTwo = new GameTwo();
		BeanUtils.copyProperties(gameTwoDTO, gameTwo, "id");

		gameTwo.setUnit(new Unit(gameTwoDTO.getUnitDTO().getId()));

		gameTwoDao.add(gameTwo);

	}

	@Override
	public void delete(Long id) {
		GameTwo categoryUnitGame = gameTwoDao.getById(id);
		if (categoryUnitGame != null) {
			gameTwoDao.delete(id);
		}
	}

	@Override
	public void edit(GameTwoDTO gameTwoDTO) {
		GameTwo gameTwo = gameTwoDao.getById(gameTwoDTO.getId());
		if (gameTwo != null) {
			if (StringUtils.isNotBlank(gameTwoDTO.getAudio())) {
				gameTwo.setAudio(gameTwoDTO.getAudio());
			}
			if (StringUtils.isNotBlank(gameTwoDTO.getImage1())) {
				gameTwo.setImage1(gameTwoDTO.getImage1());
			}
			if (StringUtils.isNotBlank(gameTwoDTO.getImage2())) {
				gameTwo.setImage2(gameTwoDTO.getImage2());
			}
			if (StringUtils.isNotBlank(gameTwoDTO.getImage3())) {
				gameTwo.setImage3(gameTwoDTO.getImage3());
			}
			if (StringUtils.isNotBlank(gameTwoDTO.getImage4())) {
				gameTwo.setImage4(gameTwoDTO.getImage4());
			}
			gameTwo.setCorrect(gameTwoDTO.getCorrect());
			gameTwo.setName(gameTwoDTO.getName());
			gameTwo.setUnit(new Unit(gameTwoDTO.getUnitDTO().getId()));
			gameTwoDao.update(gameTwo);
		}

	}

	@Override
	public GameTwoDTO getById(Long id) {
		GameTwo categoryUnitGame = gameTwoDao.getById(id);
		if (categoryUnitGame != null) {
			return convert(categoryUnitGame);
		}
		return null;
	}

	@Override
	public List<GameTwoDTO> find(SearchGameTwoDTO searchGameTwoDTO) {
		List<GameTwo> gameTwos = gameTwoDao.find(searchGameTwoDTO);
		List<GameTwoDTO> gameTwoDTOs = new ArrayList<GameTwoDTO>();

		gameTwos.forEach(gameTwo -> {
			gameTwoDTOs.add(convert(gameTwo));
		});

		return gameTwoDTOs;
	}

	@Override
	public Long count(SearchGameTwoDTO searchGameTwoDTO) {
		return gameTwoDao.count(searchGameTwoDTO);
	}

	@Override
	public Long countTotal(SearchGameTwoDTO searchGameTwoDTO) {
		return gameTwoDao.countTotal(searchGameTwoDTO);
	}

	private GameTwoDTO convert(GameTwo gameTwo) {
		GameTwoDTO gameTwoDTO = new GameTwoDTO();
		BeanUtils.copyProperties(gameTwo, gameTwoDTO);

		gameTwoDTO.setUnitDTO(new UnitDTO());
		BeanUtils.copyProperties(gameTwo.getUnit(), gameTwoDTO.getUnitDTO());

		return gameTwoDTO;

	}

}
