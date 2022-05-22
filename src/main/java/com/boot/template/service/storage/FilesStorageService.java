package com.boot.template.service.storage;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
	  public void init(Path dir);

	  public List<String> save(MultipartFile[] files, String uniqId);

	  public Resource load(String filename, String uniqId);

	  public void deleteAll(String uniqId);

	  public Stream<Path> loadAll(String uniqId);

	  public String saveOne(MultipartFile file, String uniqId);

	  public String move(String tempFile, String moveFile);

}
