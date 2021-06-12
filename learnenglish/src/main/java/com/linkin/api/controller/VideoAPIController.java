package com.linkin.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchVideoDTO;
import com.linkin.model.VideoDTO;
import com.linkin.service.VideoService;
import com.linkin.utils.FileStore;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = -1)
public class VideoAPIController {
	@Autowired
	private VideoService videoService;

	@PostMapping(value = "/member/video/list")
	public ResponseDTO<VideoDTO> find(@RequestBody SearchVideoDTO searchVideoDTO) {
		ResponseDTO<VideoDTO> responseDTO = new ResponseDTO<VideoDTO>();
		responseDTO.setData(videoService.find(searchVideoDTO));
		responseDTO.setRecordsFiltered(videoService.count(searchVideoDTO));
		responseDTO.setRecordsTotal(videoService.countToTal(searchVideoDTO));
		return responseDTO;
	}

	@PostMapping("/admin/video/add")
	public VideoDTO add(@ModelAttribute VideoDTO videoDTO) {
		videoDTO.setFileName(FileStore.getFilePath(videoDTO.getImageFile(), "video-"));
		videoService.add(videoDTO);

		return videoDTO;
	}

	@DeleteMapping("/admin/video/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		videoService.delete(id);
	}

	@DeleteMapping(value = "/admin/video/delete-multi/{ids}")
	public void del(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				videoService.delete(id);
			} catch (Exception e) {
			}
		}
	}

	@PostMapping("/admin/video/update")
	public VideoDTO update(@ModelAttribute VideoDTO videoDTO) {
		videoDTO.setFileName(FileStore.getFilePath(videoDTO.getImageFile(), "video-"));
		videoService.update(videoDTO);
		return videoDTO;
	}
}
