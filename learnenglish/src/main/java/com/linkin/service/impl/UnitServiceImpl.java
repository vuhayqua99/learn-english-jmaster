package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkin.dao.UnitDao;
import com.linkin.entity.Unit;
import com.linkin.model.SearchUnitDTO;
import com.linkin.model.UnitDTO;
import com.linkin.service.UnitService;

@Service
@Transactional
public class UnitServiceImpl implements UnitService {

	@Autowired
	private UnitDao unitDao;

	@Override
	public void add(UnitDTO unitDTO) {
		Unit unit = new Unit();
		unit.setName(unitDTO.getName());
		unit.setImage(unitDTO.getImage());
		unitDao.add(unit);
		unitDTO.setId(unit.getId());

	}

	@Override
	public void delete(Long id) {
		unitDao.delete(id);

	}

	@Override
	public void edit(UnitDTO unitDTO) {
		Unit unit = unitDao.getById(unitDTO.getId());
		if (unit != null) {
			unit.setName(unitDTO.getName());
			if (StringUtils.isNotBlank(unitDTO.getImage())) {
				unit.setImage(unitDTO.getImage());
			}
		}
		unitDao.update(unit);
	}

	@Override
	public UnitDTO getById(Long id) {
		Unit unit = unitDao.getById(id);
		if (unit != null) {
			return convert(unit);
		}
		return null;
	}

	@Override
	public List<UnitDTO> find(SearchUnitDTO searchUnitDTO) {
		List<Unit> units = unitDao.find(searchUnitDTO);
		List<UnitDTO> unitDTOs = new ArrayList<UnitDTO>();
		units.forEach(unti -> {
			unitDTOs.add(convert(unti));
		});

		return unitDTOs;
	}

	@Override
	public Long count(SearchUnitDTO searchListenMusicDTO) {
		return unitDao.count(searchListenMusicDTO);
	}

	@Override
	public Long countTotal(SearchUnitDTO searchListenMusicDTO) {
		return unitDao.countTotal(searchListenMusicDTO);
	}

	private UnitDTO convert(Unit unit) {
		UnitDTO unitDTO = new UnitDTO();
		unitDTO.setId(unit.getId());
		unitDTO.setImage(unit.getImage());
		unitDTO.setName(unit.getName());
		return unitDTO;
	}

}
