package com.boot.template.service.storage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageStorageServiceImpl implements FilesStorageService {

	@Value("${property.app.static-path}")
	private String staticPath;

	@Value("${property.app.temp-upload-path}")
	private String tempUploadPath;

	@Value("${property.app.upload-path-member-pic}")
	private String uploadPathMemberPic;

	@Value("${property.app.allow-extentions}")
	private List<String> allowExtentions;

	@Override
	public void init(Path dir) {
		try {
			Files.createDirectory(dir);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	@Override
	public List<String> save(MultipartFile[] files, String uniqId) {
		try {
			List<String> uploadedFiles = new ArrayList<String>();
			log.info("Resource Path : ["+ staticPath+tempUploadPath+"]");
			String uniqImgeFileDir = staticPath+tempUploadPath;
			Path root = Paths.get(uniqImgeFileDir);

			int cnt =0;
			for (MultipartFile file : files) {
				String fileName = uniqId+"_"+
						new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
					    + "_"+
					    file.getOriginalFilename();

				log.info("[ "+cnt+" ]"+ fileName);

				Files.copy(file.getInputStream(), root.resolve(fileName));
				uploadedFiles.add(fileName);

				cnt++;
			}

			return uploadedFiles;

		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}


	@Override
	public String saveOne(MultipartFile file, String uniqId) {
		try {

			log.info("Resource Path : ["+ staticPath+tempUploadPath+"]");
			String uniqImgeFileDir = staticPath+tempUploadPath;
			Path root = Paths.get(uniqImgeFileDir);
			String fileName = uniqId+"_"+
					new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
					   + "_"+
					file.getOriginalFilename();

			Files.copy(file.getInputStream(), root.resolve(fileName));

			return fileName;

		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}



	@Override
	public Resource load(String filename, String uniqId) {

		log.info("Load File :"+staticPath+tempUploadPath+"/"+filename);

		try {
			String uniqImgeFileDir = staticPath+tempUploadPath;
			Path root = Paths.get(uniqImgeFileDir);

			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}


	@Override
	public void deleteAll(String uniqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Stream<Path> loadAll(String uniqId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String move(String tempFile, String moveFile) {
		log.info("Temp File : {} -> Move File : {} ", (staticPath+tempUploadPath+"/"+tempFile), (staticPath+uploadPathMemberPic+"/"+moveFile));

		try {
			String tempFileDir = staticPath+tempUploadPath;
			String moveFileDir = staticPath+uploadPathMemberPic;

			Path tempRoot = Paths.get(tempFileDir);
			Path moveRoot = Paths.get(moveFileDir);

			Path tempFilePath = tempRoot.resolve(tempFile);
			Path moveFilePath = moveRoot.resolve(moveFile);

			Resource tempFileResource = new UrlResource(tempFilePath.toUri());
			Resource moveFileResource = new UrlResource(moveFilePath.toUri());

			Files.move(tempFileResource.getFile().toPath(), moveFileResource.getFile().toPath());

			return moveFilePath.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}



//	@Override
//	public void deleteAll() {
//		FileSystemUtils.deleteRecursively(root.toFile());
//	}
//
//	@Override
//	public Stream<Path> loadAll() {
//		try {
//			return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
//		} catch (IOException e) {
//			throw new RuntimeException("Could not load the files!");
//		}
//	}
}
