package com.br.training.exception;

public class FileStorageException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private String message;	
		
	public FileStorageException(String message) {		
		super(message);
		this.message = super.getMessage();	
	}

	public String getMessage() {
		return message;
	}	
	
	public void setMessage(String message) {
		this.message = message;
	}	
}
