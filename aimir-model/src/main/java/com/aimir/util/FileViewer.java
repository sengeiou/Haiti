package com.aimir.util;

/**
 * FileViewer
 *
 * @author 2.x버전에서 가져옴
 */
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

public class FileViewer {

	File myDir;
	File[] contents;
	Vector<File> vectorList;
	Iterator<File> currentFileView;
	File currentFile;

	public FileViewer(String dirname) {
		myDir=new File(dirname);
		vectorList=new Vector<File>();
	}

	public void setMyDir(String str) {
		this.myDir=new File(str);
	}

	public String getDirectory() {
		return myDir.getPath();
	}

	public int size() {
		return contents.length;
	}

	public void refreshList() {
		contents=myDir.listFiles();
		vectorList.clear();

		for (int i=0;i<contents.length;i++) {
			vectorList.add(contents[i]);
		}

		currentFileView=vectorList.iterator();
	}

	public boolean nextFile() {
		while (currentFileView.hasNext()) {
			currentFile=(File) currentFileView.next();
			return true;
		}
		return false;
	}

	public String getFileName() {
		return currentFile.getName();
	}

	public String getFileSize() {
		return new Long(currentFile.length()).toString();
	}

	public String getFileTimeStamp() {
		java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date(currentFile.lastModified()));
		//return new Date(currentFile.lastModified()).toString();
	}

	public boolean getFileType() {
		return currentFile.isDirectory();
	}

	public boolean canRead() {
		return currentFile.canRead();
	}

	public boolean canWrite() {
		return currentFile.canWrite();
	}
}