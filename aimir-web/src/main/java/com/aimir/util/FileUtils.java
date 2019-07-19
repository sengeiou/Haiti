package com.aimir.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	public static String makeDuplicationSafeFileName(String basepath, String filename) {
		String originalFilename = filename;
		
		File f = new File(makePath(basepath, filename));
		if (!f.exists()) 
			System.out.println("file not exists");
		
		int duplicateIdx = 0;
		while (f.exists()) {
			int idx = originalFilename.indexOf(".");
			duplicateIdx++;
			filename = originalFilename.substring(0, idx) + "_" + duplicateIdx + originalFilename.substring(idx);
			f = new File(makePath(basepath, filename));			
		}
		return filename;
	}

	public static boolean removeExistingFile(String path) {
		File f = new File(path);
		return f.delete();
	}
	
	public static boolean exists(String path) {
		File f = new File(path);
		return f.exists();
	}
	
	public static String makePath(String basepath, String filename) {
		if ("/".equals(basepath.substring(basepath.length()))) 
			return basepath + filename;
		else
			return basepath + "/" + filename;
	}
	
	public static void copy(String originalPath, String copyPath) throws IOException {

		File originalFile = new File(originalPath);
		File copyingFile = new File(copyPath);
		
		FileInputStream in = new FileInputStream(originalFile);
		FileOutputStream out = new FileOutputStream(copyingFile);
		
		int c;
		while ((c=in.read())!=-1) out.write(c);
		in.close();
		out.close();
	}
	
}
