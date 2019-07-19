package com.aimir.util;

/**
 * Excel Format Read Util
 * @author Hun (lucky@nuritelecom.com)
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadXLS
{
    private Workbook workbook = null;
    private Sheet[] sheet = null;
    private int sheetNo = -1;
    private int rowNo = -1;

    public ReadXLS( String filename )
        throws IOException, FileNotFoundException, BiffException
    {
        File f = new File( filename );
        workbook = Workbook.getWorkbook( f );
        sheet = workbook.getSheets();
    }

    public boolean nextSheet()
    {
        if ( workbook == null || sheet == null || sheet.length == 0 )
            return false;
        if ( sheetNo == sheet.length - 1 )
            return false;
        sheetNo++;
        rowNo = -1;
        return true;
    }

    public boolean nextRow()
    {
        if ( workbook == null || sheet == null || sheet.length == 0 )
            return false;
        if ( rowNo == sheet[sheetNo].getRows() - 1 )
            return false;
        rowNo++;
        return true;
    }

    public String getLine()
    {
        Cell[] cell = sheet[sheetNo].getRow( rowNo );
        StringBuffer sbTemp = new StringBuffer( "" );
        for ( int i = 0; i < cell.length; i++ )
        {
            sbTemp.append( cell[i].getContents() ).append( "," );
        }
        return sbTemp.toString();
    }
    
    public String getFormattedLine()
    {
        Cell[] cell = sheet[sheetNo].getRow( rowNo );
        StringBuffer sbTemp = new StringBuffer( "" );
        int currCen = 0;
        try{
            currCen = (Integer.parseInt(TimeUtil.getCurrentTime().substring(0,4))/100);
           
            for ( int i = 0; i < cell.length; i++ )
            {
                if(cell[i].getType() == CellType.DATE){

                    DateCell dc = (DateCell)cell[i];
                    String formattedDate = "";
                    if(dc.isTime()){                    
                        formattedDate = DateTimeUtil.getDateString(dc.getDate());
                    }else{      
                        formattedDate = DateTimeUtil.getDateString(dc.getDate()).substring(0, 8);
                    }
                    
                    if(!formattedDate.startsWith(currCen+"")){
                        sbTemp.append( cell[i].getContents() ).append( "," );
                    }else{
                        sbTemp.append(formattedDate).append( "," );
                    }

                }else{
                    sbTemp.append( cell[i].getContents() ).append( "," );
                }

            }
        }catch(ParseException e){
            
        }catch(Exception e){
            
        }

        return sbTemp.toString();
    }

    public void close() throws IOException {
        workbook.close();
    }

    public String getAutoLine()
    {
        if(sheetNo!=-1 && nextRow())
            return escapeStr(getLine());
        else if(nextSheet() && nextRow())
            return escapeStr(getLine());
        else if(!nextRow() && nextSheet())
            return getAutoLine();
        else
            return null;
    }

    private String escapeStr(String str)
    { 
        if(str == null || str.length() < 1)
            return str;
        try
        {
            byte[] data = str.getBytes(); 
            byte[] tmpData = new byte[data.length]; 
            int count = 0; 
            for(int i = 0 ; i < data.length ; i++) 
            {    
                if(data[i] >= 0 && data[i] < 32 || data[i] > 126) // skip 
                    continue; 
                tmpData[count] = data[i]; 
                count++; 
            } 
            return new String(tmpData,0,count); 
        }catch(Exception ex) { }

        return str;
    }
}
