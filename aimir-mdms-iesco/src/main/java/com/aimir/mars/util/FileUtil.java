package com.aimir.mars.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.UnexpectedJobExecutionException;

public class FileUtil {
	
	protected static Log log = LogFactory.getLog(FileUtil.class);
	
	public static String[] getAllFileNames(String filePath) throws Exception {
        
		List<String> fileNames = new ArrayList<String>();
        File fileDir = new File(filePath);
        
        if (fileDir != null) {
        	// get list of all files
            File[] aFiles = fileDir.listFiles();
            if( aFiles!= null )
            for (int x = 0; x < aFiles.length; x++) {
            	// ignore directories
                if (aFiles[x].isDirectory())
                    continue;
                fileNames.add(aFiles[x].getCanonicalPath());
            } // end for()
        }
        
        String[] ret = new String[0];
        return (String[]) fileNames.toArray(ret);
    }
	
	public static void fileDelete(String filePath) {
		
		File fileDir = new File(filePath);
        
        if( fileDir != null ) {
        	// get list of all files
            File[] files = fileDir.listFiles();
        
            if( files != null &&  files.length > 0){
            	// delete files iteratively
            	for (int i = 0; i < files.length; i++) {
            		boolean deleted = files[i].delete();
            		if (!deleted) {
            			log.error("Could not delete file " + files[i].getPath());
            			throw new UnexpectedJobExecutionException("Could not delete file " +  files[i].getPath());
            		}
            	}
            }
        }
	}
	
	
	public static void filesMove(String srcPath, String destPath) {
		
		// get the directory 
        File dir = new File(srcPath);
        File dest = new File(destPath);
        
        Path path = Paths.get(dest.toString());
		
		try {
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
            
        File currDest = new File(dest.getAbsolutePath());        

        if( dir != null ) {

            File[] files = dir.listFiles();
        
            if( files != null) {
            	
            	for (int i = 0; i < files.length; i++) {
                	try {
						FileUtils.moveFileToDirectory(files[i], currDest, true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}            	
            }
        }
	}
}