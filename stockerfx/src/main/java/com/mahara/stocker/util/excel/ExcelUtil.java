package com.mahara.stocker.util.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ExcelUtil {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    public static ExcelReaderBuilder read(InputStream inputStream, String fileType) throws IOException {
        var excelReaderBuilder = new ExcelReaderBuilder();
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        excelReaderBuilder.setWorkbook(workbook);
        return excelReaderBuilder;
    }

    public static ExcelWriterBuilder write() throws IOException {
        var excelWriterBuilder = new ExcelWriterBuilder();
        var workbook = new SXSSFWorkbook(1000);
        excelWriterBuilder.setWorkbook(workbook);
        return excelWriterBuilder;
    }
}
