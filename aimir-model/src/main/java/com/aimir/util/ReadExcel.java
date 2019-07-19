/**
 * 
 */
package com.aimir.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * @author goodjob
 *
 */
public class ReadExcel {

    private HSSFWorkbook workbook = null;
   // private HSSFFont font = null;
   /// private HSSFRow row = null;
   // private HSSFCell cell = null;
   // private HSSFSheet sheet = null;
    //private FileInputStream fis = null;
    
    public HSSFSheet getSheet(String sheetName){
        HSSFSheet sheet = workbook.getSheet(sheetName);
        return sheet;
    }
    
    
    
    public void readExcel(String filePath, String sheetName) {
        FileInputStream fi = null;

        try {
            fi = new FileInputStream(new File(filePath));
            this.workbook = new HSSFWorkbook(fi);
            HSSFFont font = workbook.getFontAt((short)0);

//            System.out.println("" + font.getFontName());

            HSSFSheet sheet = workbook.getSheet(sheetName);

            // Title
            HSSFRow titlerow = sheet.getRow(0);
            short titleRowHeight = titlerow.getHeight();
            //System.out.println("Title Row Height : " + titleRowHeight);
            HSSFCell titlecell = titlerow.getCell(0);
            HSSFCellStyle titlestyle = titlecell.getCellStyle();
            short titleBg = titlestyle.getFillBackgroundColor();
            //System.out.println("Title Background Color : " + titleBg);
            short titleFg = titlestyle.getFillForegroundColor();
            //System.out.println("Title Foreground Color : " + titleFg);

            // Header
            HSSFRow headerrow = sheet.getRow(1);
            short headerRowHeight = headerrow.getHeight();
            //System.out.println("Header Row Height : " + headerRowHeight);
            HSSFCell headercell = headerrow.getCell(0);
            HSSFCellStyle headerstyle = headercell.getCellStyle();
            short headerBg = headerstyle.getFillBackgroundColor();
            //System.out.println("Header Background Color Index : " + headerBg);

            IndexedColors headerBgColor = null;
            for (IndexedColors obj : IndexedColors.values()) {
                if (headerBg == obj.getIndex()) {
                    headerBgColor = obj;
                    break;
                }
            }
            
//            System.out.println("Header BackgroundColor : " + headerBgColor.name());
            
            short headerFg = headerstyle.getFillForegroundColor();
            //System.out.println("Header Foreground Color Index : " + headerFg);

            IndexedColors headerFgColor = null;
            for (IndexedColors obj : IndexedColors.values()) {
                if (headerFg == obj.getIndex()) {
                    headerFgColor = obj;
                    break;
                }
            }
            
//            System.out.println("Header Foreground Color : " + headerFgColor.name());
            
            // Fst Row
            HSSFRow datafstrow = sheet.getRow(2);

            short fstRowHeight = datafstrow.getHeight();
            //System.out.println("Fst Row Height : " + fstRowHeight);
            HSSFCell fstcell = datafstrow.getCell(0);
            HSSFCellStyle fststyle = fstcell.getCellStyle();
            short fstBg = fststyle.getFillBackgroundColor();
            //System.out.println("Fst Background Color Index : " + fstBg);

            IndexedColors fstBgColor = null;
            for (IndexedColors obj : IndexedColors.values()) {
                if (fstBg == obj.getIndex()) {
                    fstBgColor = obj;
                    break;
                }
            }

//            System.out.println("Fst BackgroundColor : " + fstBgColor.name());

            short fstFg = fststyle.getFillForegroundColor();
            //System.out.println("Fst Foreground Color Index : " + fstFg);

            IndexedColors fstFgColor = null;
            for (IndexedColors obj : IndexedColors.values()) {
                if (fstFg == obj.getIndex()) {
                    fstFgColor = obj;
                    break;
                }
            }
//            sheet.get
            HSSFColor color = new HSSFColor();
//            color.
            
            //System.out.println("fst Foreground Color : " + fstFgColor.name());

            // Snd Row
            HSSFRow datasndrow = sheet.getRow(3);

            short sndRowHeight = datasndrow.getHeight();
            //System.out.println("Snd Row Height : " + sndRowHeight);
            HSSFCell sndcell = datasndrow.getCell(0);
            HSSFCellStyle sndstyle = sndcell.getCellStyle();
            short sndBg = sndstyle.getFillBackgroundColor();
            //System.out.println("Snd Background Color Index : " + sndBg);

            IndexedColors sndBgColor = null;
            for (IndexedColors obj : IndexedColors.values()) {
                if (sndBg == obj.getIndex()) {
                    sndBgColor = obj;
                    break;
                }
            }

//            System.out.println("Snd BackgroundColor : " + sndBgColor.name());

            short sndFg = sndstyle.getFillForegroundColor();
            //System.out.println("Snd Foreground Color Index : " + sndFg);

            IndexedColors sndFgColor = null;
            for (IndexedColors obj : IndexedColors.values()) {
                if (sndFg == obj.getIndex()) {
                    sndFgColor = obj;
                    break;
                }
            }
            
            //System.out.println("Snd Foreground Color : " + sndFgColor.name());

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}
	
	public static boolean isEmptyRow(HSSFRow row){

		if(row.getFirstCellNum() == -1 ){
			return true;
		}
		
		if(row.getLastCellNum() == -1){
			return true;
		}
		
		return false;
		
	}
	
	public static String getCelltoString(HSSFCell cell){

		switch(cell.getCellType()){
			case HSSFCell.CELL_TYPE_NUMERIC : 
				return Double.toString(cell.getNumericCellValue());
			case HSSFCell.CELL_TYPE_STRING :
				return cell.getStringCellValue();
			case HSSFCell.CELL_TYPE_FORMULA :
				return cell.getCellFormula();
			case HSSFCell.CELL_TYPE_BLANK :
				return "";
			case HSSFCell.CELL_TYPE_BOOLEAN :
				return Boolean.toString(cell.getBooleanCellValue());
			case HSSFCell.CELL_TYPE_ERROR : 
				return "";
			default : 
				return "";
		}
	}

}
