package com.aimir.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelUtil {
	
	private static final String hourPrefix	= "meteringDataHour";		//16글자
	private static final String dayPrefix		= "meteringDataDay";		//15글자
	private static final String dayWeekPrefix	= "meteringDataDayWeek";	//19글자
	private static final String weekPrefix	= "meteringDataWeek";		//16글자
	private static final String monthPrefix	= "meteringDataMonth";		//17글자
	private static final String seasonPrefix	= "meteringDataSeason";		//18글자
	private static final String yearPrefix	= "meteringDataYear";		//16글자
	
	public static File initDirectory(String path) {
		long deleteCount = 0;
		File dir = new File(path);
		if (dir.exists()) {
			try {
				File[] files = dir.listFiles();

				if (files != null) {
					String filename = null;
					String deleteDate;

					deleteDate = CalendarUtil.getDate(
						TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10
					);

					boolean isDel = false;

					for (File file : files) {
						filename = file.getName();
						isDel = false;
						
						// 파일길이 : 22이상, 확장자 : xls|zip
						if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
							
							// 10일 지난 파일들 삭제
							if (filename.startsWith(hourPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
								isDel = true;
							} 
							else if (filename.startsWith(dayPrefix) && filename.substring(15, 23).compareTo(deleteDate) < 0) {
								isDel = true;
							} 
							else if (filename.startsWith(dayWeekPrefix) && filename.substring(19, 27).compareTo(deleteDate) < 0) {
								isDel = true;
							}
							else if (filename.startsWith(weekPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
								isDel = true;
							}
							else if (filename.startsWith(monthPrefix) && filename.substring(17, 25).compareTo(deleteDate) < 0) {
								isDel = true;
							}
							else if (filename.startsWith(seasonPrefix) && filename.substring(18, 26).compareTo(deleteDate) < 0) {
								isDel = true;
							}
							else if (filename.startsWith(yearPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
								isDel = true;
							}

							if (isDel) {
								file.delete();
								deleteCount = deleteCount + 1;
							}
						}
						filename = null;
					}
				}
			}
			catch (ParseException e) {
			}
		}
		else {
			dir.mkdir();
		}  
		return dir;
	}
	
	 /**
     * 엑셀 파일을 읽는다.
     * @param filename
     */
    public static List<HashMap<String, String>> readExcel(String filename) {

        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = null;

        Workbook workbook = null;
        Sheet sheet = null;
        Cell cell = null;
        Cell[] cells;

        int row=0;
        String str = "";
        String header = "";

        try {
            workbook = Workbook.getWorkbook(new File(filename));
            sheet = workbook.getSheet(0); // 엑셀의 첫번째 시트를 가져온다
            row = sheet.getRows();  // 엑셀파일의 줄수를 가져온다
            for(int i=1 ; i<row ; i++) {
                map = new HashMap<String,String>();
                cells = sheet.getRow(i); //해당 row에 있는 cell의 갯수                
                int colSize = cells.length;
                
                for(int k=0 ; k < colSize ; k++) {
                    cell = sheet.getCell(k, i);
                    str = nvl(cell.getContents(), "");
                    header = sheet.getCell(k, 0).getContents();
                    map.put(header, str);
                }                
                list.add(map);
            }
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            workbook.close();
        }

        return list;
    }

    public static String nvl(String str, String val) {

        if (str == null || "".equals(str.trim())) {
            return val;
        } else {
            return str;
        }
    }
    
    /**
     * @param workbook
     * @param top
     * @param bottom
     * @param left
     * @param right
     * @param grey
     * @param green
     * @param orange
     * @param align
     * @return HSSFCellStyle
     */
    public static HSSFCellStyle getStyle(HSSFWorkbook workbook, HSSFFont font, int top, int bottom, int left, int right, int grey,
            int green, int orange, int align, int border) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        short borderStyle = HSSFCellStyle.BORDER_MEDIUM;
        
        if(border == 0) {
        	borderStyle = HSSFCellStyle.BORDER_THIN;
        }

        if (top == 1) {
        	
    	   style.setBorderTop(borderStyle);
           style.setTopBorderColor(HSSFColor.BLACK.index);
        
        }

        if (bottom == 1) {
            style.setBorderBottom(borderStyle);
            style.setBottomBorderColor(HSSFColor.BLACK.index);
        }

        if (left == 1) {
            style.setBorderLeft(borderStyle);
            style.setLeftBorderColor(HSSFColor.BLACK.index);
        }

        if (right == 1) {
            style.setBorderRight(borderStyle);
            style.setRightBorderColor(HSSFColor.BLACK.index);
        }

        if (grey == 1) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            
        } else if (green == 1) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.LIME.getIndex());
        } else if (orange == 1) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
//            style.setFillForegroundColor(IndexedColors.TAN.getIndex());
        }else if (orange == 2) {
        	style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        	style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        }else if (orange == 3) {
	    	style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    	style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
	    }else if (orange == 4) {
	    	style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    	style.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
	    }

        if (align == 1) {
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (align == 2) {
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        }

        return style;
    }
}
