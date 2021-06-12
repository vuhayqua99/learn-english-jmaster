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

import com.linkin.model.GameTwoDTO;
import com.linkin.model.ResponseDTO;
import com.linkin.model.SearchGameTwoDTO;
import com.linkin.service.GameTwoService;
import com.linkin.utils.FileStore;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = -1)
public class GameTwoApiController {

	@Autowired
	private GameTwoService gameTwoService;

	@PostMapping(value = "/member/game-two/list")
	public ResponseDTO<GameTwoDTO> find(@RequestBody SearchGameTwoDTO SearchCategoryUnitGameDTO) {
		ResponseDTO<GameTwoDTO> responseDTO = new ResponseDTO<GameTwoDTO>();
		responseDTO.setData(gameTwoService.find(SearchCategoryUnitGameDTO));
		responseDTO.setRecordsFiltered(gameTwoService.count(SearchCategoryUnitGameDTO));
		responseDTO.setRecordsTotal(gameTwoService.countTotal(SearchCategoryUnitGameDTO));
		return responseDTO;
	}

	@PostMapping("/admin/game-two/add")
	public GameTwoDTO add(@ModelAttribute GameTwoDTO gameTwoDTO) {
		gameTwoDTO.setAudio(FileStore.getFilePath(gameTwoDTO.getAudioFile(), "audio-"));
		gameTwoDTO.setImage1(FileStore.getFilePath(gameTwoDTO.getImageFile1(), "image1-"));
		gameTwoDTO.setImage2(FileStore.getFilePath(gameTwoDTO.getImageFile2(), "image2-"));
		gameTwoDTO.setImage3(FileStore.getFilePath(gameTwoDTO.getImageFile3(), "image3-"));
		gameTwoDTO.setImage4(FileStore.getFilePath(gameTwoDTO.getImageFile4(), "image4-"));
		gameTwoService.add(gameTwoDTO);
		return gameTwoDTO;
	}

	@DeleteMapping(value = "/admin/game-two/delete/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		gameTwoService.delete(id);
	}

	@DeleteMapping(value = "/admin/game-two/delete-multi/{ids}")
	public void des(@PathVariable(name = "ids") List<Long> ids) {
		for (Long id : ids) {
			try {
				gameTwoService.delete(id);
			} catch (Exception e) {
			}
		}
	}

	@PostMapping("/admin/game-two/update")
	public GameTwoDTO update(@ModelAttribute GameTwoDTO gameTwoDTO) {
		
		gameTwoDTO.setAudio(FileStore.getFilePath(gameTwoDTO.getAudioFile(), "audio-"));
		gameTwoDTO.setImage1(FileStore.getFilePath(gameTwoDTO.getImageFile1(), "image1-"));
		gameTwoDTO.setImage2(FileStore.getFilePath(gameTwoDTO.getImageFile2(), "image2-"));
		gameTwoDTO.setImage3(FileStore.getFilePath(gameTwoDTO.getImageFile3(), "image3-"));
		gameTwoDTO.setImage4(FileStore.getFilePath(gameTwoDTO.getImageFile4(), "image4-"));
		gameTwoService.edit(gameTwoDTO);
		return gameTwoDTO;
	}

}
