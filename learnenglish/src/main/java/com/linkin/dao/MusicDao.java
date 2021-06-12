package com.linkin.dao;

import java.util.List;

import com.linkin.entity.Music;
import com.linkin.model.SearchMusicDTO;

public interface MusicDao {

	void add(Music music);

	void update(Music music);

	void delete(Music music);

	Music getById(Long id);

	List<Music> find(SearchMusicDTO searchMusicDTO);

	Long count(SearchMusicDTO searchMusicDTO);

	Long countTotal(SearchMusicDTO searchMusicDTO);

}
