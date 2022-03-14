package com.mahara.stocker.util.excel;

import org.apache.poi.ss.usermodel.Workbook;

public class ExcelReaderBuilder {
    private Workbook workbook;

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public ExcelSheetReader sheet() {
        return sheet(0, null);
    }

    public ExcelSheetReader sheet(Integer sheetNo) {
        return sheet(sheetNo, null);
    }

    public ExcelSheetReader sheet(String sheetName) {
        return sheet(null, sheetName);
    }

    public ExcelSheetReader sheet(Integer sheetNo, String sheetName) {
        ExcelSheetReader excelReaderSheetBuilder = new ExcelSheetReader();
        if (sheetNo != null) {
            excelReaderSheetBuilder.setSheet(workbook.getSheetAt(sheetNo));
        }
        if (sheetName != null) {
            excelReaderSheetBuilder.setSheet(workbook.getSheet(sheetName));
        }
        return excelReaderSheetBuilder;
    }
}
