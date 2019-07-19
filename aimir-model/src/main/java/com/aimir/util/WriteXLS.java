package com.aimir.util;

/**
 * Excel Format Write Util
 * @author Hun (lucky@nuritelecom.com)
 */

import java.io.File; 
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * WriteXLS
 *
 * @author 2.x버전에서 가져옴
 */
public class WriteXLS
{
    private WritableWorkbook workbook = null;
    private WritableSheet sheet = null;
    //private WritableCell cell = null;
    private Token token = null;
    private int rowNo = 0;
    private int cellNo = 0;

    public WriteXLS(String filename) throws IOException
    {
        workbook = Workbook.createWorkbook(new File(filename)); 
        sheet = workbook.createSheet("Fail List", 0); 

    }

    public void setLine(String line) throws IOException, RowsExceededException, WriteException {
        token = new Token(line,",");
        int elcnt = token.getCountElt();
        cellNo = 0;
        for(int i = 0 ; i < elcnt ; i++)
        {
            Label label = new Label(cellNo, rowNo, token.getElementAt( i )); 
            sheet.addCell( label );
            cellNo++;
        }
        rowNo++;
    }

    public void close() throws IOException, WriteException {
        workbook.write(); 
        workbook.close(); 
    }
}
