package com.aimir.schedule.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SAP 포멧 및 기타 검침 정보를 파일로 출력할때 필요한 메소드를 정의하고 공통으로 사용될만한 기능이 구현되어 있다.<br>
 * 미터마다 또는 요구사항에 따라 파일 포멧이 다르기때문에 포멧화 하는 기능은 인터페이스로 빼놓음.
 * @author kskim
 *
 */
public abstract class SAPFileOutputUtil {
	
	//한 폴더에 최대 파일 생성 제한수
	static int DEF_LIMIT_FILE_CNT = 100;
	
	//백업 폴더 기본 이름
	static String BACKUP_DIR = "backup";
	
	/**
	 * 한폴더에 파일 생성 갯수 제한 설정.
	 */
	private int limitFileCount=DEF_LIMIT_FILE_CNT;
	
	
	/**
	 * 실패 목록 
	 */
	private List<String> failList = new ArrayList<String>();
	
	public List<String> getFailList(){
		return this.failList;
	}
	
	protected void addFailList(String meterSerial){
		this.failList.add(meterSerial);
	}
	
	public int getLimitFileCount() {
		return limitFileCount;
	}

	public void setLimitFileCount(int limitFileCount) {
		this.limitFileCount = limitFileCount;
	}

	/**
	 * 정의된 파일 포멧으로 바이너리를 구성하는 기능을 구현하면 된다.
	 * @param condition 디비 조회 조건 VO 객체
	 */
	public abstract byte[] build(SAPFileOutputCondition condition);
	
	/**
	 * 파일 생성 및 조회 중 오류생겼을 경우가 있는지
	 * @return
	 */
	public abstract boolean isError();
	
	/**
	 * 오류가 생겼을때 오류 메시지.
	 * @return
	 */
	public abstract String getErrorMsg();
	
	
	/**
	 * 파일 쓰는 기능. 한 폴더에 파일 생성 갯수 제한되는 기능이 있다.
	 * @param data
	 * @param dirPath 폴더 위치
	 * @param fileName 파일이름
	 * @param ext 파일 확장자
	 * @param overWrite 중복파일 삭제 옵션
	 * @return
	 * @throws IOException
	 */
	public boolean write(byte[] data, String dirPath, String fileName, String ext, boolean overWrite) throws IOException{

		File dir = new File(dirPath);
		
		//폴더내 파일 갯수를 찾는다.
		File[] findFiles = dir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				if(pathname.isFile())
					return true;
				else
					return false;
			}
			
		});
		
		//파일 생성 갯수제한 검사.
		if(findFiles!=null && findFiles.length>=limitFileCount){
			//갯수 제한 초과시 백업 폴더를 만들고 기존 파일들을
			
			String backupDirPath = getUniqueMkdir(dirPath,BACKUP_DIR);
			
			for (File f : findFiles) {
				File destFile = new File(backupDirPath+"/"+f.getName());
				f.renameTo(destFile);
			}
		}
		
		// 폴더 생성.
		dir.mkdirs();
		
		StringBuilder fullPath = new StringBuilder();
		fullPath.append(dirPath);
		fullPath.append("/");
		fullPath.append(fileName);
		fullPath.append(".");
		fullPath.append(ext);
		
		File file = new File(fullPath.toString());
		
		if(file.exists()){
			//기존 파일은 삭제
			if(overWrite){
				file.deleteOnExit();
			}else{
				return false;
			}
		}
		
		FileOutputStream fos = null;
		fos = new FileOutputStream(file);
		fos.write(data);
		fos.flush();
		fos.close();
		return true;
	}

	/**
	 * name이 중복되지 않는 디렉토리를 새성하고 path를 구한다.
	 * @param dirPath 기준 디렉토리
	 * @param baseName
	 * @return
	 */
	private String getUniqueMkdir(String dirPath, String baseName) {
		File dir = new File(dirPath);

		// getAbsolutePath 를 하면 dirPath 마지막에 / 가 누락 되어도 상관 없다.
		String fullPath = dir.getAbsolutePath() + "/" + baseName;

		File newDir = new File(fullPath);

		if (newDir.exists()) {
			// 뒤에 번호부분을 삭제
			String fname = baseName.replaceAll("_[0-9]*$", "");

			// 파일 번호부분만 추출해서 증가한뒤 다시 검사한다.
			String numStr = "0";
			if (baseName.matches(".*_([0-9]*$)")) {
				numStr = baseName.replaceAll(".*_([0-9]*$)", "$1");
			}
			Integer fnum = Integer.parseInt(numStr) + 1;
			// 재귀하여 검사한다.
			return getUniqueMkdir(dirPath, String.format("%s_%d", fname, fnum));
		} else {
			newDir.mkdirs();
		}

		return newDir.getAbsolutePath();
	}
	
}
