package com.mahara.stocker.util.excel;

import com.mahara.stocker.util.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExcelSheetReader {
    private Sheet sheet;
    private int headRowNumber = 0;
    private int columnCount = 1;

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * 设置表头所在行，读取表头计算列数。
     * 不支持多行表头。
     * @param headRowNumber 表头所在行，0开始
     */
    public ExcelSheetReader headRowNumber(int headRowNumber) {
        this.headRowNumber = headRowNumber;
        if (this.headRowNumber < 0) {
            this.headRowNumber = 0;
        }
        var header = sheet.getRow(headRowNumber);
        var cells = header.cellIterator();
        var tmpCount = 0;
        while (cells.hasNext()) {
            var cell = cells.next();
            var cellValue = convertCellValueToString(cell);
            if (StringUtils.isEmpty(cellValue)) {
                break;
            }
            tmpCount++;
        }
        this.columnCount = tmpCount;
        return this;
    }

    /**
     * 检查数据合法性。
     *
     * @param action
     * @throws ValidationException
     */
    public ExcelSheetReader doCheck(BiConsumer<Map<Integer, String>, Integer> action) throws ValidationException {
        var endRowNumber = sheet.getPhysicalNumberOfRows();
        for (var i = headRowNumber + 1; i< endRowNumber; i++) {
            var row = sheet.getRow(i);
            var rowData = convertRowDataToMap(row);
            if (rowData != null) {
                action.accept(rowData, i);
            }
        }
        return this;
    }

    /**
     * 按照指定批量读取并处理
     * @param mapAction
     * @param handleAction
     * @param batchSize
     * @param <R>
     */
    public <R> void doBatchRead(Function<Map<Integer, String>, R> mapAction, Consumer<List<R>> handleAction, int batchSize) {
        var endRowNumber = sheet.getPhysicalNumberOfRows();
        List<R> cached = new ArrayList<>(batchSize);
        for (var i = headRowNumber + 1; i< endRowNumber; i++) {
            var row = sheet.getRow(i);
            var rowData = convertRowDataToMap(row);
            if (rowData != null) {
                cached.add(mapAction.apply(rowData));
                if (cached.size() == batchSize) {
                    handleAction.accept(cached);
                    cached = new ArrayList<>(batchSize);
                }
            }
        }
        if (cached.size() > 0) {
            handleAction.accept(cached);
        }
    }

    /**
     * 一行一行读取并处理。
     * @param mapAction
     * @param consumeAction
     * @param <R>
     */
    public <R> void doRead(Function<Map<Integer, String>, R> mapAction, Consumer<R> consumeAction) {
        var endRowNumber = sheet.getPhysicalNumberOfRows();
        for (var i = headRowNumber + 1; i< endRowNumber; i++) {
            var row = sheet.getRow(i);
            var rowData = convertRowDataToMap(row);
            if (rowData != null) {
                consumeAction.accept(mapAction.apply(rowData));
            }
        }
    }

    /**
     * 读取cell的内容，转换成字符串。
     * 目前只读取数字、布尔、字符串类型。
     * @param cell
     * @return
     */
    private String convertCellValueToString(Cell cell) {
        if(cell==null){
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:
                Double doubleValue = cell.getNumericCellValue();
                DecimalFormat df = new DecimalFormat("#########.#########");
                returnValue = df.format(doubleValue);
                break;
            case STRING:
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
//            case BLANK:
//                break;
//            case FORMULA:
//                break;
//            case ERROR:
//                break;
            default:
                break;
        }
        return returnValue;
    }

    private Map<Integer, String> convertRowDataToMap(Row row) {
        var rowDate = new HashMap<Integer, String>(columnCount);
        // row.iterator()返回结果中不包含空的cell
        var cells = row.iterator();
        var tmpCount = 0;
        var notEmptyCount = 0;
        while (cells.hasNext() && tmpCount < columnCount) {
            var cell = cells.next();
            var cellValue = convertCellValueToString(cell);
            if (!StringUtils.isEmpty(cellValue)) {
                notEmptyCount++;
            }
            rowDate.put(cell.getColumnIndex(), cellValue);
            tmpCount = cell.getColumnIndex();
        }

        // 所有cell都为空，认定为空行，返回null
        if (notEmptyCount > 0) {
            return rowDate;
        } else {
            return null;
        }
    }
}
