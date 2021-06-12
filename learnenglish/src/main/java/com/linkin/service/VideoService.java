package com.linkin.service;

import java.util.List;

import com.linkin.model.SearchVideoDTO;
import com.linkin.model.VideoDTO;

public interface VideoService {
	
	void add(VideoDTO videoDTO);
	
	void delete(Long id);
	
	void update(VideoDTO videoDTO);
	
	VideoDTO getById(Long id);
	
	Long count(SearchVideoDTO searchVideoDTO);
	
	Long countToTal(SearchVideoDTO searchVideoDTO);
	
	List<VideoDTO> find(SearchVideoDTO searchVideoDTO);

}
