package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkin.dao.MusicDao;
import com.linkin.entity.Music;
import com.linkin.entity.Unit;
import com.linkin.model.MusicDTO;
import com.linkin.model.SearchMusicDTO;
import com.linkin.service.MusicService;

@Service
@Transactional
public class MusicServiceImpl implements MusicService {

	@Autowired
	private MusicDao musicDao;

	@Override
	public void add(MusicDTO musicDTO) {
		Music music = new Music();
		music.setFileName(musicDTO.getFileName());
		music.setName(musicDTO.getName());
		music.setUnit(new Unit(musicDTO.getUnitId()));

		musicDao.add(music);
	}

	@Override
	public void edit(MusicDTO musicDTO) {
		Music music = musicDao.getById(musicDTO.getId());
		if (music != null) {
			if (StringUtils.isNotBlank(musicDTO.getFileName())) {
				music.setFileName(musicDTO.getFileName());
			}
			music.setName(musicDTO.getName());
			music.setUnit(new Unit(musicDTO.getUnitId()));
			musicDao.update(music);
		}
	}

	@Override
	public void delete(Long id) {
		Music music = musicDao.getById(id);
		if (music != null) {
			musicDao.delete(music);
		}
	}

	@Override
	public MusicDTO getById(Long id) {
		Music music = musicDao.getById(id);
		if (music != null) {
			return convent(music);
		}
		return null;
	}

	@Override
	public Long count(SearchMusicDTO searchMusicDTO) {
		return musicDao.count(searchMusicDTO);
	}

	@Override
	public Long countTotal(SearchMusicDTO searchMusicDTO) {
		return musicDao.countTotal(searchMusicDTO);
	}

	private MusicDTO convent(Music music) {
		MusicDTO musicDTO = new MusicDTO();
		musicDTO.setId(music.getId());
		musicDTO.setFileName(music.getFileName());
		musicDTO.setName(music.getName());
		musicDTO.setUnitId(music.getUnit().getId());
		musicDTO.setUnitName(music.getUnit().getName());

		return musicDTO;

	}

	@Override
	public List<MusicDTO> find(SearchMusicDTO searchMusicDTO) {
		List<Music> musics = musicDao.find(searchMusicDTO);
		List<MusicDTO> musicDTOs = new ArrayList<MusicDTO>();
		musics.forEach(music -> {
			musicDTOs.add(convent(music));
		});

		return musicDTOs;
	}

}
