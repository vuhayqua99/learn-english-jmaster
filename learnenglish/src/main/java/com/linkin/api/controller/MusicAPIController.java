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

import com.linkin.model.MusicDTO;
import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchMusicDTO;
import com.linkin.service.MusicService;
import com.linkin.utils.FileStore;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = -1)
public class MusicAPIController {
	@Autowired
	private MusicService musicService;

	@PostMapping(value = "/member/music/list")
	public ResponseDTO<MusicDTO> find(@RequestBody SearchMusicDTO searchMusicDTO) {
		ResponseDTO<MusicDTO> responseDTO = new ResponseDTO<MusicDTO>();
		responseDTO.setData(musicService.find(searchMusicDTO));
		responseDTO.setRecordsFiltered(musicService.count(searchMusicDTO));
		responseDTO.setRecordsTotal(musicService.countTotal(searchMusicDTO));
		return responseDTO;
	}

	@PostMapping("/admin/music/add")
	public MusicDTO add(@ModelAttribute MusicDTO musicDTO) {
		musicDTO.setFileName(FileStore.getFilePath(musicDTO.getImageFile(), "music-"));
		musicService.add(musicDTO);
		return musicDTO;
	}

	@DeleteMapping(value = "/admin/music/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		musicService.delete(id);
	}

	@DeleteMapping(value = "/admin/music/delete-multi/{ids}")
	public void des(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				musicService.delete(id);
			} catch (Exception e) {
			}
		}
	}

	@PostMapping("/admin/music/update")
	public MusicDTO update(@ModelAttribute MusicDTO musicDTO) {
		musicDTO.setFileName(FileStore.getFilePath(musicDTO.getImageFile(), "music-"));
		musicService.edit(musicDTO);
		return musicDTO;
	}

}
