package com.mahara.stocker.util.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.util.List;

public class ExcelSheetWriter {
    private SXSSFSheet sheet;
    private CreationHelper creationHelper;
    private int currentRowNumber = 0;
    private int columnNumber = 1;

    public void setSheet(SXSSFSheet sheet) {
        this.sheet = sheet;
    }

    public void setCreationHelper(CreationHelper creationHelper) {
        this.creationHelper = creationHelper;
    }

    public ExcelSheetWriter head(List<SheetColumn> columns) {
        if (columns == null) {
            return this;
        }
        columnNumber = columns.size();
        currentRowNumber++;
        // 定义数据单元格样式
        var dataFont = sheet.getWorkbook().createFont();
        dataFont.setBold(false);
        dataFont.setFontHeightInPoints((short) 16);
        var dataCellStyle = sheet.getWorkbook().createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataCellStyle.setFont(dataFont);
        dataCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        // 设置列宽和列的默认样式
        for (int i = 0; i < columns.size(); i++) {
            sheet.setColumnWidth(i, columns.get(i).getWidth()*256);
            sheet.setDefaultColumnStyle(i, dataCellStyle);
        }
        // 定义表头样式
        var headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 16);
        var headCellStyle = sheet.getWorkbook().createCellStyle();
        headCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headCellStyle.setFont(headerFont);
        // 添加表头
        var firstRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            var tmp = columns.get(i);
            createCell(firstRow, i, headCellStyle, tmp.getTitle(), tmp.getComment());
        }

        return this;
    }

    public void doWrite(List<List<String>> data) {
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            var row = sheet.createRow(currentRowNumber);
            var rowData = data.get(i);
            for (int j = 0; j < columnNumber; j++) {
                if (j < rowData.size()) {
                    createCell(row, j, null, rowData.get(j), null);
                }
            }
            currentRowNumber++;
        }
    }


    private void createCell(SXSSFRow row, int column, CellStyle cellStyle, String value, String comment) {
        var cell = row.createCell(column);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        cell.setCellValue(value);
        if (!StringUtils.isEmpty(comment)) {
            var tmp = sheet.createDrawingPatriarch().createCellComment(getClientAnchor(cell));
            tmp.setString(creationHelper.createRichTextString(comment));
            cell.setCellComment(tmp);
        }
    }

    private ClientAnchor getClientAnchor(SXSSFCell cell) {
        ClientAnchor anchor = creationHelper.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setDx1(10* Units.EMU_PER_PIXEL);
        anchor.setCol2(cell.getColumnIndex()+2);
        anchor.setDx2(10*Units.EMU_PER_PIXEL);
        anchor.setRow1(cell.getRowIndex());
        anchor.setDy1(10*Units.EMU_PER_PIXEL);
        anchor.setRow2(cell.getRowIndex()+5);
        anchor.setDy2(10*Units.EMU_PER_PIXEL);

        return anchor;
    }
}
