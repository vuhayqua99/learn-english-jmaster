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

import com.linkin.model.GameOneDTO;
import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchGameOneDTO;
import com.linkin.service.GameOneService;
import com.linkin.utils.FileStore;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = -1)
public class GameOneAPIController {
	@Autowired
	private GameOneService gameOneService;

	@PostMapping(value = "/member/game-one/list")
	public ResponseDTO<GameOneDTO> find(@RequestBody SearchGameOneDTO searchGameOneDTO) {
		ResponseDTO<GameOneDTO> responseDTO = new ResponseDTO<GameOneDTO>();
		responseDTO.setData(gameOneService.find(searchGameOneDTO));
		responseDTO.setRecordsFiltered(gameOneService.count(searchGameOneDTO));
		responseDTO.setRecordsTotal(gameOneService.countTotal(searchGameOneDTO));
		return responseDTO;
	}

	@PostMapping("/admin/game-one/add")
	public GameOneDTO add(@ModelAttribute GameOneDTO gameOneDTO) {
		gameOneDTO.setAudio(FileStore.getFilePath(gameOneDTO.getAudioFile(), "audio-"));
		gameOneDTO.setImage(FileStore.getFilePath(gameOneDTO.getImageFile(), "image-"));
		gameOneService.add(gameOneDTO);
		return gameOneDTO;
	}

	@DeleteMapping(value = "/admin/game-one/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		gameOneService.delete(id);
	}

	@DeleteMapping(value = "/admin/game-one/delete-multi/{ids}")
	public void des(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				gameOneService.delete(id);
			} catch (Exception e) {
			}
		}
	}

	@PostMapping("/admin/game-one/update")
	public GameOneDTO update(@ModelAttribute GameOneDTO gameOneDTO) {
		gameOneDTO.setAudio(FileStore.getFilePath(gameOneDTO.getAudioFile(), "audio-"));
		gameOneDTO.setImage(FileStore.getFilePath(gameOneDTO.getImageFile(), "image-"));
		gameOneService.edit(gameOneDTO);
		return gameOneDTO;
	}

}
