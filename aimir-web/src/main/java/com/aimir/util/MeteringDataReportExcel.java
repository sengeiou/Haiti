package com.aimir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;



public class MeteringDataReportExcel {

    public MeteringDataReportExcel() {

    }

    /**
     * @param result
     * @param msgMap
     * @param isLast
     * @param filePath
     * @param fileName
     */
    public void writeReportExcel(List<Map<String, Object>> result, Map<String, String> msgMap, boolean isLast, String filePath, String fileName) {

        try {
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFFont font = workbook.createFont();
            //font.setFontName("맑은 고딕");
            font.setFontHeightInPoints((short)9);
            

            HSSFRow row = null;
            HSSFCell cell = null;

            Map<String, Object> resultMap = new HashMap<String, Object>();

            String fileFullPath = new StringBuilder().append(filePath).append(File.separator).append(fileName).toString();
            
            int dataCount = 0;
            int colcnt = 0;

            HSSFSheet sheet = workbook.createSheet("Report");
            int colIdx = 0;
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 22);
            sheet.setColumnWidth(colIdx++, 256 * 19);
            sheet.setColumnWidth(colIdx++, 256 * 8);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            sheet.setColumnWidth(colIdx++, 256 * 18);
            sheet.setColumnWidth(colIdx++, 256 * 11);
            
            if (isLast) {
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
                sheet.setColumnWidth(colIdx++, 256 * 18);
                sheet.setColumnWidth(colIdx++, 256 * 11);
            }
            
			HSSFCellStyle dataCellStyle_11101000 = getStyle(workbook, font, 1, 1, 1, 0, 1, 0, 0, 0);
			HSSFCellStyle dataCellStyle_11000001 = getStyle(workbook, font, 1, 1, 0, 0, 0, 0, 0, 1);
			HSSFCellStyle dataCellStyle_11000000 = getStyle(workbook, font, 1, 1, 0, 0, 0, 0, 0, 0);
			HSSFCellStyle dataCellStyle_11010000 = getStyle(workbook, font, 1, 1, 0, 1, 0, 0, 0, 0);
			HSSFCellStyle dataCellStyle_11100101 = getStyle(workbook, font, 1, 1, 1, 0, 0, 1, 0, 1);
			HSSFCellStyle dataCellStyle_11000101 = getStyle(workbook, font, 1, 1, 0, 0, 0, 1, 0, 1);
			HSSFCellStyle dataCellStyle_11010101 = getStyle(workbook, font, 1, 1, 0, 1, 0, 1, 0, 1);
			HSSFCellStyle dataCellStyle_11100011 = getStyle(workbook, font, 1, 1, 1, 0, 0, 0, 1, 1);
			HSSFCellStyle dataCellStyle_11000011 = getStyle(workbook, font, 1, 1, 0, 0, 0, 0, 1, 1);
			HSSFCellStyle dataCellStyle_11010111 = getStyle(workbook, font, 1, 1, 0, 1, 0, 1, 1, 1);
			HSSFCellStyle dataCellStyle_10101000 = getStyle(workbook, font, 1, 0, 1, 0, 1, 0, 0, 0);
			HSSFCellStyle dataCellStyle_00010001 = getStyle(workbook, font, 0, 0, 0, 1, 0, 0, 0, 1);
			HSSFCellStyle dataCellStyle_11011000 = getStyle(workbook, font, 1, 1, 0, 1, 1, 0, 0, 0);
			HSSFCellStyle dataCellStyle_11100002 = getStyle(workbook, font, 1, 1, 1, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_11000002 = getStyle(workbook, font, 1, 1, 0, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_11010002 = getStyle(workbook, font, 1, 1, 0, 1, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_00001000 = getStyle(workbook, font, 0, 0, 0, 0, 1, 0, 0, 0);
			HSSFCellStyle dataCellStyle_10011002 = getStyle(workbook, font, 1, 0, 0, 1, 1, 0, 0, 2);
			HSSFCellStyle dataCellStyle_10100002 = getStyle(workbook, font, 1, 0, 1, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_10000002 = getStyle(workbook, font, 1, 0, 0, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_10010002 = getStyle(workbook, font, 1, 0, 0, 1, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_00101000 = getStyle(workbook, font, 0, 0, 1, 0, 1, 0, 0, 0);
			HSSFCellStyle dataCellStyle_00011002 = getStyle(workbook, font, 0, 0, 0, 1, 1, 0, 0, 2);
			HSSFCellStyle dataCellStyle_00100002 = getStyle(workbook, font, 0, 0, 1, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_00000002 = getStyle(workbook, font, 0, 0, 0, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_00010002 = getStyle(workbook, font, 0, 0, 0, 1, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_01100002 = getStyle(workbook, font, 0, 1, 1, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_01000002 = getStyle(workbook, font, 0, 1, 0, 0, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_01010002 = getStyle(workbook, font, 0, 1, 0, 1, 0, 0, 0, 2);
			HSSFCellStyle dataCellStyle_00100000 = getStyle(workbook, font, 0, 0, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle dataCellStyle_00010000 = getStyle(workbook, font, 0, 0, 0, 1, 0, 0, 0, 0);
			HSSFCellStyle dataCellStyle_01101000 = getStyle(workbook, font, 0, 1, 1, 0, 1, 0, 0, 0);
			HSSFCellStyle dataCellStyle_01011002 = getStyle(workbook, font, 0, 1, 0, 1, 1, 0, 0, 2);
			HSSFCellStyle dataCellStyle_01100000 = getStyle(workbook, font, 0, 1, 1, 0, 0, 0, 0, 0);
			HSSFCellStyle dataCellStyle_01010000 = getStyle(workbook, font, 0, 1, 0, 1, 0, 0, 0, 0);
			
            resultMap = result.get(0);
            
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellStyle(getStyle(workbook, font, 0, 0, 0, 0, 0, 1, 0, 1));
            cell.setCellValue((String)resultMap.get("searchDate"));

            cell = row.createCell(1);
            cell.setCellStyle(getStyle(workbook, font, 0, 0, 0, 0, 0, 0, 0, 0));
            cell.setCellValue(msgMap.get("msgSearchDate"));

            // 전일(전월)
            if (isLast) {
                cell = row.createCell(2);
                cell.setCellStyle(getStyle(workbook, font, 0, 0, 0, 0, 0, 0, 1, 1));
                cell.setCellValue((String)resultMap.get("lastSearchDate"));

                cell = row.createCell(3);
                cell.setCellStyle(getStyle(workbook, font, 0, 0, 0, 0, 0, 0, 0, 0));
                cell.setCellValue(msgMap.get("msgLastDay"));
            }

            row = sheet.createRow(1);

            dataCount = result.size();

            for(int i = 0 ; i < dataCount ; i++) {
                resultMap = result.get(i);
                colcnt = 0;

                row = sheet.createRow((i * 13 + 2 + 0));

                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11101000);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                cell.setCellValue(msgMap.get("msgDate"));

                cell = row.createCell(++colcnt);
                
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000001);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                cell.setCellValue((String)resultMap.get("YYYYMMDD"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000000);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11010000);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 2), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11100101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgActImp"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));
                
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgActExp"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRactLagImp"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRactLeadImp"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRactLagExp"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRactLeadExp"));
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgkVAh1"));
                //end

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11010101);
                } else {
                    cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11100011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgActImp"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11100011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgActExp"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgRactLagImp"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgRactLeadImp"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgRactLagExp"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgRactLeadExp"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000011);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(msgMap.get("msgkVAh1"));
                    //end

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11010111);
                    } else {
                        cell.setCellStyle(sheet.getRow(2).getCell(colcnt).getCellStyle());
                    }

                    sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 0), (i * 13 + 2 + 0), (colcnt - 1), colcnt));
                }

                row = sheet.createRow((i * 13 + 2 + 1));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10101000);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgNo"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010001);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("rowNum"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11101000);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgTotEnergy"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11011000);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 1), (i * 13 + 2 + 1), 2, 3));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11100002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("ACTENGYIMPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("ACTENGYEXPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGIMPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADIMPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGEXPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADEXPTOT"));
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11010002);
                } else {
                    cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("KVAH"));
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String)resultMap.get("LSTACTENGYIMPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTACTENGYEXPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGIMPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADIMPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGEXPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADEXPTOT"));
                    
                  //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(3).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("KVAH"));
                    //end
                }

                row = sheet.createRow((i * 13 + 2 + 2));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00001000);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgCustomerName"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010001);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("CUSTOMERNAME"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10101000);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgEnergy"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10011002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10100002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("ACTENGYIMPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("ACTENGYEXPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGIMPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADIMPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGEXPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADEXPRAT1"));
                
                //jhkim
                cell = row.createCell(++colcnt);
                
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10010002);
                } else {
                    cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue("0");
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String)resultMap.get("LSTACTENGYIMPRAT1"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTACTENGYEXPRAT1"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGIMPRAT1"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADIMPRAT1"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGEXPRAT1"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADEXPRAT1"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);
                    
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(4).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue("0");
                    //end
                }

                row = sheet.createRow((i * 13 + 2 + 3));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgConstractNo"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010001);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("CONTRACTNUMBER"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00011002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate2"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("ACTENGYIMPRAT2"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("ACTENGYEXPRAT2"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGIMPRAT2"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADIMPRAT2"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGEXPRAT2"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADEXPRAT2"));
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010002);
                } else {
                    cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue("0");
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String)resultMap.get("LSTACTENGYIMPRAT2"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTACTENGYEXPRAT2"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGIMPRAT2"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADIMPRAT2"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGEXPRAT2"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADEXPRAT2"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(5).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue("0");
                    //end
                }

                row = sheet.createRow((i * 13 + 2 + 4));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgMeterId"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010001);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("METERID"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00011002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate3"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01100002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("ACTENGYIMPRAT3"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("ACTENGYEXPRAT3"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGIMPRAT3"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADIMPRAT3"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLAGEXPRAT3"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("RACTENGYLEADEXPRAT3"));
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01010002);
                } else {
                    cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue("0");
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String)resultMap.get("LSTACTENGYIMPRAT3"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTACTENGYEXPRAT3"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGIMPRAT3"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADIMPRAT3"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLAGEXPRAT3"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTRACTENGYLEADEXPRAT3"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(6).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue("0");
                    //end
                }

                row = sheet.createRow((i * 13 + 2 + 5));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgContractDemand"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("CONTRACTDEMAND"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11101000);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgTotDemandTime"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11011000);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 5), (i * 13 + 2 + 5), (colcnt - 1), colcnt));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11100002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEIMPTOT")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String)resultMap.get("ACTDMDMXIMPTOT")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEEXPTOT")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("ACTDMDMXEXPTOT")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGIMPTOT")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGIMPTOT")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADIMPTOT")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADIMPTOT")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGEXPTOT")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGEXPTOT")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADEXPTOT")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADEXPTOT")).toString());
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATETOTAL")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11010002);
                } else {
                    cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATETOTAL")).toString());
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEIMPTOT")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String)resultMap.get("LSTACTDMDMXIMPTOT")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEEXPTOT")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTACTDMDMXEXPTOT")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPTOT")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGIMPTOT")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPTOT")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADIMPTOT")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPTOT")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGEXPTOT")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPTOT")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADEXPTOT")).toString());
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATETOTAL")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(7).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATETOTAL")).toString());
                    //end
                }

                row = sheet.createRow((i * 13 + 2 + 6));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgTariffType"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010001);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("TARIFFTYPENAME"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10101000);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgMaxDemandTime"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10011002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10100002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEIMPRAT1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String)resultMap.get("ACTDMDMXIMPRAT1")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEEXPRAT1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("ACTDMDMXEXPRAT1")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGIMPRAT1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGIMPRAT1")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADIMPRAT1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADIMPRAT1")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGEXPRAT1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGEXPRAT1")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADEXPRAT1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADEXPRAT1")).toString());
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATE1")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10010002);
                } else {
                    cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATE1")).toString());
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEIMPRAT1")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String)resultMap.get("LSTACTDMDMXIMPRAT1")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEEXPRAT1")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTACTDMDMXEXPRAT1")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPRAT1")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGIMPRAT1")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPRAT1")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADIMPRAT1")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPRAT1")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGEXPRAT1")).toString());

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPRAT1")).append(") ").toString());

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADEXPRAT1")).toString());
                    
                  //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATE1")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(8).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATE1")).toString());
                    //end
                }

                row = sheet.createRow((i * 13 + 2 + 7));
                colcnt = 0;

                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgLocation"));
                
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010001);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("LOCATIONNAME"));
                
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00011002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate2"));
               
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEIMPRAT2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String)resultMap.get("ACTDMDMXIMPRAT2")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEEXPRAT2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("ACTDMDMXEXPRAT2")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGIMPRAT2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGIMPRAT2")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADIMPRAT2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADIMPRAT2")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGEXPRAT2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGEXPRAT2")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADEXPRAT2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADEXPRAT2")).toString());
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATE2")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010002);
                } else {
                    cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATE2")).toString());
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEIMPRAT2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String)resultMap.get("LSTACTDMDMXIMPRAT2")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEEXPRAT2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTACTDMDMXEXPRAT2")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPRAT2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGIMPRAT2")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPRAT2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADIMPRAT2")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPRAT2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGEXPRAT2")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPRAT2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADEXPRAT2")).toString());
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATE2")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(9).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATE2")).toString());
                    //end
                }
                
                row = sheet.createRow((i * 13 + 2 + 8));
                colcnt = 0;
                
                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100000);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010000);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01101000);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01011002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate3"));
               
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01100002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEIMPRAT3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String)resultMap.get("ACTDMDMXIMPRAT3")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("ACTDMDMXTIMEEXPRAT3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("ACTDMDMXEXPRAT3")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGIMPRAT3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGIMPRAT3")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADIMPRAT3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADIMPRAT3")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELAGEXPRAT3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLAGEXPRAT3")).toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("RACTDMDMXTIMELEADEXPRAT3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("RACTDMDMXLEADEXPRAT3")).toString());
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATE3")).append(") ").toString());

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01010002);
                } else {
                    cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATE3")).toString());
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEIMPRAT3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String)resultMap.get("LSTACTDMDMXIMPRAT3")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTACTDMDMXTIMEEXPRAT3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTACTDMDMXEXPRAT3")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGIMPRAT3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGIMPRAT3")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADIMPRAT3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADIMPRAT3")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELAGEXPRAT3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLAGEXPRAT3")).toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("LSTRACTDMDMXTIMELEADEXPRAT3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("LSTRACTDMDMXLEADEXPRAT3")).toString());
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append('(').append((String)resultMap.get("MAXDMDKVAH1TIMERATE3")).append(") ").toString());

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(10).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue(new StringBuilder().append((String) resultMap.get("MAXDMDKVAH1RATE3")).toString());
                    //end
                }
                
                row = sheet.createRow((i * 13 + 2 + 9));
                colcnt = 0;

                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100000);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010000);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11101000);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgTotCummDemand"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11011000);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                sheet.addMergedRegion(new CellRangeAddress((i * 13 + 2 + 9), (i * 13 + 2 + 9), 2, 3));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11100002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("CUMACTDMDMXIMPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMACTDMDMXEXPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGIMPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADIMPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGEXPTOT"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADEXPTOT"));
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11000002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_11010002);
                } else {
                    cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMMKVAH1RATETOTAL"));
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String)resultMap.get("LSTCUMACTDMDMXIMPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMACTDMDMXEXPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGIMPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADIMPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGEXPTOT"));

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADEXPTOT"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_11010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(11).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("CUMMKVAH1RATETOTAL"));
                    //end
                }
                
                row = sheet.createRow((i * 13 + 2 + 10));
                colcnt = 0;

                cell = row.createCell(colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100000);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010000);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10101000);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgCummDemand"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10011002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10100002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String)resultMap.get("CUMACTDMDMXIMPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMACTDMDMXEXPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGIMPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADIMPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGEXPRAT1"));

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADEXPRAT1"));
                
                //jhkim
                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10000002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);

                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_10010002);
                } else {
                    cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMMKVAH1RATE1"));
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String)resultMap.get("LSTCUMACTDMDMXIMPRAT1"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMACTDMDMXEXPRAT1"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGIMPRAT1"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADIMPRAT1"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGEXPRAT1"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);                    
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);                    
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADEXPRAT1"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);

                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_10010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(12).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("CUMMKVAH1RATE1"));
                    //end
                }
                
                row = sheet.createRow((i * 13 + 2 + 11));
                colcnt = 0;

                cell = row.createCell(colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100000);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010000);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00101000);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00011002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate2"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00100002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMACTDMDMXIMPRAT2"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMACTDMDMXEXPRAT2"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGIMPRAT2"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADIMPRAT2"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGEXPRAT2"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADEXPRAT2"));
                
                //jhkim
                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00000002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_00010002);
                } else {
                    cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMMKVAH1RATE2"));
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMACTDMDMXIMPRAT2"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMACTDMDMXEXPRAT2"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGIMPRAT2"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADIMPRAT2"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGEXPRAT2"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADEXPRAT2"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_00010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(13).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("CUMMKVAH1RATE2"));
                    //end
                }
                
                row = sheet.createRow((i * 13 + 2 + 12));
                colcnt = 0;

                cell = row.createCell(colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01100000);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01010000);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01101000);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01011002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue(msgMap.get("msgRate3"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01100002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMACTDMDMXIMPRAT3"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMACTDMDMXEXPRAT3"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGIMPRAT3"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADIMPRAT3"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLAGEXPRAT3"));

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMRACTDMDMXLEADEXPRAT3"));
                
                //jhkim
                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01000002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }

                cell = row.createCell(++colcnt);
                if (i == 0) {
                    cell.setCellStyle(dataCellStyle_01010002);
                } else {
                    cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                }
                cell.setCellValue((String) resultMap.get("CUMMKVAH1RATE3"));
                //end

                // 전일(전월)
                if (isLast) {
                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01100002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMACTDMDMXIMPRAT3"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMACTDMDMXEXPRAT3"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGIMPRAT3"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADIMPRAT3"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLAGEXPRAT3"));

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("LSTCUMRACTDMDMXLEADEXPRAT3"));
                    
                    //jhkim
                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01000002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }

                    cell = row.createCell(++colcnt);
                    if (i == 0) {
                        cell.setCellStyle(dataCellStyle_01010002);
                    } else {
                        cell.setCellStyle(sheet.getRow(14).getCell(colcnt).getCellStyle());
                    }
                    cell.setCellValue((String) resultMap.get("CUMMKVAH1RATE3"));
                    //end
                }
            }

            FileOutputStream fs = null;
            try {
                fs = new FileOutputStream(fileFullPath);
                workbook.write(fs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fs != null) fs.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    private HSSFCellStyle getStyle(HSSFWorkbook workbook, HSSFFont font, int top, int bottom, int left, int right, int grey,
            int green, int orange, int align) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);

        if (top == 1) {
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
            style.setTopBorderColor(HSSFColor.BLACK.index);
        }

        if (bottom == 1) {
            style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            style.setBottomBorderColor(HSSFColor.BLACK.index);
        }

        if (left == 1) {
            style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
            style.setLeftBorderColor(HSSFColor.BLACK.index);
        }

        if (right == 1) {
            style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
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
        }

        if (align == 1) {
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (align == 2) {
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        }

        return style;
    }
}
