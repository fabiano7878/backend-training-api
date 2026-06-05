package com.br.training.util;

public class FileUtils {

	public static String sanitizeFilename(String filename) {
	    return filename.replaceAll("[\\\\/]", "");
	}	
}
