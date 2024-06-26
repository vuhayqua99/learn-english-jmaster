package com.linkin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

public interface FileStore {
	public static final String UPLOAD_FOLDER = "/root/files/";//"/Volumes/Data/files/";//
	//public static String STATIC_FOLDER = "/root/client/";//"/Volumes/MacOS/Users/dinhcuong/Desktop/apache-tomcat-8.0.28/webapps/ROOT/";// "/root/client/";//

//	public static final String UPLOAD_FOLDER = "F:\\abc\\";
	
	public static List<String> getFilePaths(List<MultipartFile> multipartFiles, String prefix) {
		List<String> images = new ArrayList<String>();
		if (multipartFiles != null) {
			for (int i = 0; i < multipartFiles.size(); i++) {
				MultipartFile imageFile = multipartFiles.get(i);
				if (imageFile != null && !imageFile.isEmpty()) {
					try {
						int index = imageFile.getOriginalFilename().lastIndexOf(".");
						String ext = imageFile.getOriginalFilename().substring(index);
						String image = prefix + System.currentTimeMillis() + "-" + i + ext;

						Path pathAvatar = Paths.get(UPLOAD_FOLDER + File.separator + image);
						Files.write(pathAvatar, imageFile.getBytes());

						images.add(image);
					} catch (IOException e) {
					}
				}
			}

		}
		return images;
	}

	public static String getFilePath(MultipartFile multipartFile, String prefix) {
		if (multipartFile != null && !multipartFile.isEmpty()) {
			try {
				int index = multipartFile.getOriginalFilename().lastIndexOf(".");
				String ext = multipartFile.getOriginalFilename().substring(index);
				String image = prefix + System.currentTimeMillis() + ext;

				Path pathImage = Paths.get(UPLOAD_FOLDER + File.separator + image);
				Files.write(pathImage, multipartFile.getBytes());

				return image;
			} catch (IOException e) {
			}
		}
		return null;
	}

	@Async
	public static void deleteFiles(List<String> filePaths) {
		if (filePaths != null) {
			filePaths.forEach(image -> {
				try {
					File avatarFile = new File(UPLOAD_FOLDER + File.separator + image);
					if (avatarFile.exists()) {
						avatarFile.delete();
					}
				} catch (Exception e) {
				}
			});
		}
	}

	@Async
	public static void deleteFile(String filePath) {
		if (filePath != null) {
			try {
				File avatarFile = new File(UPLOAD_FOLDER + File.separator + filePath);
				if (avatarFile.exists()) {
					avatarFile.delete();
				}
			} catch (Exception e) {
			}
		}
	}
}
