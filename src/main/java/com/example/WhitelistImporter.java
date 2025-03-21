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

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    int rowNum = row.getRowNum();
                    if (rowNum == 0) {
                        continue; // 跳过表头
                    }
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

                        value = value.trim();
                        if (!value.isEmpty()) {
                            // 这里增加调试输出
                            System.out.println("[DEBUG] Row " + (rowNum + 1) + ": " + value);
                            whitelist.add(value);
                        }
                    }
                }
            }
        }
        return whitelist;
    }
}
