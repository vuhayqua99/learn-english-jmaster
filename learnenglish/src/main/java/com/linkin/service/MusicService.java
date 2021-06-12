package com.linkin.service;

import java.util.List;

import com.linkin.model.MusicDTO;
import com.linkin.model.SearchMusicDTO;

public interface MusicService {

	void add(MusicDTO musicDTO);

	void delete(Long id);

	void edit(MusicDTO musicDTO);

	MusicDTO getById(Long id);

	Long count(SearchMusicDTO searchMusicDTO);

	Long countTotal(SearchMusicDTO searchMusicDTO);

	List<MusicDTO> find(SearchMusicDTO searchMusicDTO);


}
