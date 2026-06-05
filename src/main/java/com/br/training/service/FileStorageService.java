package com.br.training.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.br.training.exception.FileStorageException;
import com.br.training.util.FileUtils;

@Service
public class FileStorageService {
	
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path uploadPath;
    
    private final Set<String> allowedTypes = Set.of("application/pdf", "image/png", "image/jpeg", "image/jpg", "text/plain");
    
    public String uploadFile(MultipartFile file) throws FileStorageException {    	
    	if (file.isEmpty()) {
    		log.error("Falha no upload do arquivo, o objeto File está vazio!");
    		throw new FileStorageException("Falha no upload do arquivo, o objeto File está vazio!");
    	}
    	return file.getOriginalFilename();
    }
    
	public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws FileStorageException {
		this.uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.uploadPath);
		} catch (IOException e) {
			log.error("Erro ao criar diretório de upload! {}", e);
			throw new FileStorageException("Erro ao criar diretório de upload!".concat(e.getCause().toString()));
		}		
	}	
	
	public String store(MultipartFile file) throws FileStorageException {
		
		if(file == null || file.isEmpty()) {
			throw new FileStorageException("Arquivo vazio ou nullo, validação no metodo store().");
		}
		
		validateMimeType(file, file.getContentType());		
		validarTamanhoArquivo(file);
		try {			
			String originalName = FileUtils.sanitizeFilename(file.getOriginalFilename());			
			Path targetLocation = this.uploadPath.resolve(originalName);
			Files.copy(file.getInputStream(), targetLocation);
			log.info("Upload recebido - nome={}, size={}, tipo={}",
				    file.getOriginalFilename(),
				    file.getSize(),
				    file.getContentType()
				);
			return targetLocation.toString();
		} catch (IOException e) {
			log.error("Falha ao tentar salvar o arquivo: {}", file.getOriginalFilename(), e);
			throw new FileStorageException("Falha ao tentar salvar o arquivo: ".concat(file.getOriginalFilename()).concat(e.getCause().toString()));			
		}		
	}
	
	private void validarTamanhoArquivo(MultipartFile file) throws FileStorageException {
		long maxSizeInBytes = 10 * 1024 * 1024; // 10 MB
		if (file.getSize() > maxSizeInBytes) {
			throw new FileStorageException("O tamanho do arquivo excede o limite permitido de 10 MB.");
		}
	}

	public void validateMimeType(MultipartFile file, String expectedMimeType) throws FileStorageException {		
			if (!allowedTypes.contains(expectedMimeType)) {
				throw new FileStorageException("Tipo de arquivo não permitido");
			}			
	}
	
	public Path load(String filename) {
		return uploadPath.resolve(filename).normalize();
	}
	
	public String getFilesUploaded() {
		StringBuilder fileList = new StringBuilder();
		try {
			if (!Files.exists(uploadPath) || !Files.isDirectory(uploadPath)) {
				return "Nenhum arquivo encontrado.";
			}
		} catch (Exception e) {
			log.error("Erro ao acessar o diretório de upload: {}", e.getMessage());
			return "Erro ao acessar o diretório de upload.";
		}	
		try {
			Files.list(uploadPath).forEach(path -> {
				String originalName = FileUtils.sanitizeFilename(path.getFileName().toString());
				fileList.append(originalName).append("\n");
			});
		} catch (IOException e) {
			log.error("Erro ao listar arquivos: {}", e.getMessage());
			throw new FileStorageException("Erro ao tentar listar os arquivos: ".concat(e.getCause().toString()));
		}
		return fileList.toString();
	}
}
