package com.mahara.stocker.util.excel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;

public class ExcelWriterBuilder {
    private SXSSFWorkbook workbook;

    public void setWorkbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public ExcelSheetWriter sheet() {
        return sheet("Sheet-1");
    }
    public ExcelSheetWriter sheet(String sheetName) {
        ExcelSheetWriter excelSheetWriter = new ExcelSheetWriter();
        if (sheetName != null) {
            excelSheetWriter.setSheet(workbook.createSheet(sheetName));
            excelSheetWriter.setCreationHelper(workbook.getCreationHelper());
        }
        return excelSheetWriter;
    }

    public void finish(OutputStream out) throws IOException {
        workbook.write(out);
        workbook.dispose();
    }
}
