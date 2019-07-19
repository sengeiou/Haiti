package com.aimir.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class MakeExcelUtil {
    private HSSFWorkbook workbook = null;
    private HSSFFont font = null;
    private HSSFRow row = null;
    private HSSFCell cell = null;
    private HSSFSheet sheet = null;
    private FileOutputStream fs = null;

    public MakeExcelUtil( String filename, String sheetName )
        throws FileNotFoundException
    {
        fs = new FileOutputStream(filename);
        workbook = new HSSFWorkbook();
        font = workbook.createFont();
        sheet = workbook.createSheet(sheetName);
    }
    
    public void setFontHeightInPoints(Short height) {
    	font.setFontHeightInPoints(height);
    }
    
    public void setFontName(String name) {
    	font.setFontName(name);
    }
   
    public void setColumnWidth(int columnIndex, int width) {
    	sheet.setColumnWidth(columnIndex, width);
    }

    public void createRow(int rowNum) {
    	row =sheet.createRow(rowNum);
    }
    
    public void createCell(int cellNum) {
    	cell = row.createCell(cellNum);
    }
    
    public void setCellValue(String value) {
    	cell.setCellValue(value);
    }
    
    public void writeExcel() throws IOException {
    	workbook.write(fs);
    }
    
    public void close()  throws IOException {
    	if (fs != null) fs.close();
    }
}
