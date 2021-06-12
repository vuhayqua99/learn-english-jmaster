package com.linkin.dao;

import java.util.List;

import com.linkin.entity.Video;
import com.linkin.model.SearchVideoDTO;

public interface VideoDao {
	
	void add(Video video);
	
	void delete(Video video);
	
	void update(Video video);
	
	Video getById(Long id);
	
	List<Video> find(SearchVideoDTO searchVideoDTO);
	
	Long count(SearchVideoDTO searchVideoDTO);
	
	Long coutTotal(SearchVideoDTO searchVideoDTO);

}
