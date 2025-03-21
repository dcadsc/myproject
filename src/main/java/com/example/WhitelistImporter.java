package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhitelistImporter {

    public List<String> importWhitelist(String excelFilePath) throws IOException {
        List<String> whitelist = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // 遍历所有 sheet
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    Cell cell = row.getCell(0); // 只取 A 列
                    if (cell != null) {
                        String value = "";
                        if (cell.getCellType() == CellType.STRING) {
                            value = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            value = String.valueOf((long) cell.getNumericCellValue());
                        } else if (cell.getCellType() == CellType.FORMULA) {
                            value = cell.getStringCellValue();
                        }
                        whitelist.add(value.trim());
                    }
                }
            }
        }
        return whitelist;
    }
}
