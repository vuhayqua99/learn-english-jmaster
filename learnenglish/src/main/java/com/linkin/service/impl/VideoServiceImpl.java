package com.linkin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linkin.dao.VideoDao;
import com.linkin.entity.Unit;
import com.linkin.entity.Video;
import com.linkin.model.SearchVideoDTO;
import com.linkin.model.VideoDTO;
import com.linkin.service.VideoService;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideoDao videoDao;

	@Override
	public void add(VideoDTO videoDTO) {
		Video video = new Video();
		video.setName(videoDTO.getName());
		video.setFileName(videoDTO.getFileName());
		video.setUnit(new Unit(videoDTO.getUnitId()));

		videoDao.add(video);
		videoDTO.setId(video.getId());
	}

	@Override
	public void delete(Long id) {
		Video video = videoDao.getById(id);
		if (video != null) {
			videoDao.delete(video);
		}
	}

	@Override
	public void update(VideoDTO videoDTO) {
		Video video = videoDao.getById(videoDTO.getId());
		if (video != null) {
			video.setName(videoDTO.getName());
			if (StringUtils.isNotBlank(videoDTO.getFileName())) {
				video.setFileName(videoDTO.getFileName());
			}
			video.setUnit(new Unit(videoDTO.getUnitId()));
			videoDao.update(video);
		}
	}

	@Override
	public VideoDTO getById(Long id) {
		Video video = videoDao.getById(id);
		if (video != null) {
			convert(video);
		}
		return null;
	}

	@Override
	public Long count(SearchVideoDTO searchVideoDTO) {
		return videoDao.count(searchVideoDTO);
	}

	@Override
	public Long countToTal(SearchVideoDTO searchVideoDTO) {
		return videoDao.coutTotal(searchVideoDTO);
	}

	@Override
	public List<VideoDTO> find(SearchVideoDTO searchVideoDTO) {
		List<Video> videos = videoDao.find(searchVideoDTO);
		List<VideoDTO> videoDTOs = new ArrayList<VideoDTO>();

		videos.forEach(video -> {
			videoDTOs.add(convert(video));
		});
		return videoDTOs;
	}

	private VideoDTO convert(Video video) {
		VideoDTO videoDTO = new VideoDTO();
		videoDTO.setId(video.getId());
		videoDTO.setName(video.getName());
		videoDTO.setFileName(video.getFileName());
		videoDTO.setUnitId(video.getUnit().getId());
		videoDTO.setUnitName(video.getUnit().getName());
		return videoDTO;
	}
}
