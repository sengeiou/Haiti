package com.aimir.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * WriteFile
 *
 * @author 2.x버전에서 가져옴
 */
public class WriteFile {
	private BufferedWriter file = null;
	public WriteFile(String filename) throws IOException,FileNotFoundException{
		FileWriter fd = new FileWriter(filename);	
		file = new BufferedWriter(fd); 
	}

	public void setLine(String line) throws IOException {
		file.write(line);	
        file.newLine();
        file.flush();
	}

	public void close() throws IOException {
		file.close();
	}
}
