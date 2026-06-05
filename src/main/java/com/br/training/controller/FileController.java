package com.br.training.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.training.exception.FileStorageException;
import com.br.training.service.FileStorageService;

@RestController
@RequestMapping("/files")
public class FileController {
	
	private final FileStorageService service;
		
	public FileController(FileStorageService service) {		
		this.service = service;
	}

	@PostMapping("/enviarArquivo")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws FileStorageException {		
		return ResponseEntity.ok("File uploaded successfully! E o nome do arquivo é: %s".formatted(service.uploadFile(file)));
	}
	
	@PostMapping("/upload")
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {		
		String filename = service.store(file);
		return ResponseEntity.ok("Arquivo salvo: %s".formatted(filename));				
	}
	
	@GetMapping("/download/{filename}")
	public ResponseEntity<Resource> downloads(@PathVariable("filename") String filename) throws MalformedURLException {
		
			Path file = service.load(filename);
			Resource resource = new UrlResource(file.toUri());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
	}
	
	@GetMapping("/list")
	public ResponseEntity<String> lisToDownload() {								
			return ResponseEntity.ok().body(service.getFilesUploaded());		
	}
}
