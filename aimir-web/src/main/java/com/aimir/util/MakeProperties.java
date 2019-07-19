package com.aimir.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * 언어별 Message Properties 파일 생성.
 * @author kskim
 *
 * aimir가 BEMS로 서비스 되는 경우에는 일부 메세지를 다른용어로 사용되도록 한다.
 * bems_charge.proeprties에서 besm.service 프로퍼티 확인.
 * 2012.07.25. bmhan.
 */
public class MakeProperties {

	private static String DELI; 
	private static int ORDER = 1; 

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String path = new java.io.File(".").getCanonicalPath();
		String message_path = "src/main/resources/";//../resources
		String message_prefix = "message_";
		String message_excel = "aimir_ui_message.xls";

		boolean ismc = isMessageConvert();

		String realPath = message_path;
		File excelFile = new File(realPath + File.separator + message_excel);
		WorkbookSettings workbookSetting = new WorkbookSettings();
		workbookSetting.setEncoding("ISO8859_1");
		Workbook workbook = Workbook.getWorkbook(excelFile, workbookSetting);
		Sheet sheet = workbook.getSheet(0);
		int columns = sheet.getColumns();
		//int columns = 10;	
		String[] encodes = new String[columns - 1];
		Properties[] props = new Properties[encodes.length];

		// 테이블에서 인코딩을 가져온다.
		Cell cell = null;
		for (int i = 0; i < encodes.length; i++) {
			cell = sheet.getCell(i + 1, 0);
			encodes[i] = cell.getContents();
			props[i] = new Properties();
		}

		// 1번 행부터 읽어서 프로퍼티에 넣는다.
		String property = null;
		int rows = sheet.getRows();

		for (int i = 1; i < rows; i++) {
			property = sheet.getCell(0, i).getContents();
			for (int j = 0; j < encodes.length; j++) {
				cell = sheet.getCell(j + 1, i);
				if (props[j].get(property) != null) {
					System.out.println("same property[" + property + "]");
				}

				props[j].put(property, convert(cell.getContents(), ismc));
			}
		}
		System.out.println("message properties rows[" + props[0].size() + "]");
		for (int i = 0; i < encodes.length; i++) {
			props[i].store(new FileOutputStream(realPath + File.separator
					+ message_prefix + encodes[i] + ".properties"), "Message "
					+ encodes[i] + " Properties");
		}

		System.out.println("메시지 프로퍼티 생성 완료~");
	}

	private static boolean isMessageConvert() 
	{
		Properties p = new Properties();
		try {
			p.load(com.aimir.util.MakeProperties.class.getClassLoader()
					.getResourceAsStream("messages.properties"));

			String isconvert = p.getProperty("message.convert");
			System.out.println("message.convert["+isconvert+"]");
	
			if ( isconvert == null || isconvert.equals("false") ) 
				return false;

			DELI = p.getProperty("message.convert.delimiter");	
			ORDER = Integer.parseInt(p.getProperty("message.convert.order"));	
			System.out.println("convert delimiter["+DELI+"], order["+ORDER+"]");

		} catch (Exception e) {
			System.out.println("ERROR["+e+"]");
		}

		return true;
	}

	private static String convert(String contents, boolean convert)
	{
		if ( contents == null ||  "".equals(contents) ) return contents;

		String[] mess = contents.split("\\" + DELI); // 구분자 "|"

		// 구분자 없이 Cell에 하나만 들어가 있으면 그냥 리턴.	
		if ( mess.length == 1 )
			return mess[0];

		// message.convert 가 true이면 |로 구분되어 있는 
		// ORDER번째 문자를 사용한다.
		if ( convert &&  mess.length == ORDER)
			return mess[ORDER-1];
		else 
			return mess[0];
	}
}
