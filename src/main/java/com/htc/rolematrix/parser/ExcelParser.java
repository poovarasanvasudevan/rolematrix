package com.htc.rolematrix.parser;

import com.htc.rolematrix.constants.Constant;
import com.htc.rolematrix.services.LoggerService;
import com.htc.rolematrix.services.RemedyServices;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Created by poovarasanv on 19/10/17.
 * Project : role-matrix
 */

@Component
public class ExcelParser {

    @Autowired
    LoggerService loggerService;


    @Autowired
    RemedyServices remedyServices;


    public Set<String> validateRequiredRows(File excelFile) {
        Workbook workbook = null;
        Sheet sheet = null;

        Set<String> missingColumns = new HashSet<>();
        try {
            workbook = Workbook.getWorkbook(excelFile);
            sheet = workbook.getSheet(0);


            Cell[] header = sheet.getRow(0);

            for (Cell cell : header) {
                if (cell.getContents().trim().contains(Constant.ROLE_X)) {
                    missingColumns.add(Constant.ROLE_X);
                }


                if (cell.getContents().trim().contains(Constant.ROLE_PREFIX)) {
                    missingColumns.add(Constant.ROLE_PREFIX);
                }

                if (cell.getContents().trim().contains(Constant.COST_CODE_EXCEL)) {
                    missingColumns.add(Constant.COST_CODE_EXCEL);
                }
            }

        } catch (Exception e) {
            loggerService.errorLof(e.getLocalizedMessage());
        }

        return missingColumns;
    }

    public Map<String, List<Map<String, String>>> parserExcel(
            File excelFile,
            Integer answerIndex,
            String templateInstanceId) {
        List parsedArray = new ArrayList<HashMap<String, String>>();
        List answerArray = new ArrayList<HashMap<String, String>>();
        Workbook workbook = null;
        Sheet sheet = null;
        try {

            loggerService.log("Getting Excel File from " + excelFile.getAbsolutePath());
            loggerService.log("Getting Sheet 0");
            workbook = Workbook.getWorkbook(excelFile);


            // loggerService.log("Getting Sheet 0");
            sheet = workbook.getSheet(Constant.SHEET_NO);


            int noc = sheet.getColumns(); //  Number of Rows
            int nor = sheet.getRows();    // Number Of Columns


            //loggerService.log("Found Columns : " + noc + " Rows : "+nor);

            String[] columns = new String[noc];


            for (int i = 0; i < noc; i++) {
                Cell[] header = sheet.getRow(0);
                columns[i] = header[i].getContents().trim();


                //loggerService.log("Header At Position " + i + " is " + header[i].getContents().trim());
            }

            for (int i = 1; i < nor; i++) {
                Cell[] currentRow = sheet.getRow(i);
                HashMap currentRowJson = new HashMap<String, String>();
                for (int j = 0; j < noc; j++) {

                    String content = "";
                    if (currentRow.length < i) {
                        content = currentRow[j].getContents();
                    }

                    currentRowJson.put(columns[j], currentRow[j].getContents().trim());
                }

                HashMap currentAnswer = new HashMap<String, String>();
                for (int k = answerIndex; k < noc; k++) {
                    //parseCondition(currentAnswer, columns[k], currentRow[k].getContents().toString(),templateInstanceId);
                    currentAnswer.put(columns[k], currentRow[k].getContents().toString());
                }
                remedyServices.addToMap(String.valueOf(i), currentAnswer);
                answerArray.add(currentAnswer);
                parsedArray.add(currentRowJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, List<Map<String, String>>> retuenMap = new HashMap<>();
        retuenMap.put("answer", answerArray);
        retuenMap.put("parsed", parsedArray);

        return retuenMap;
    }

    public void parseCondition(HashMap<String, String> fullMap, String columnName, String data, String templateInstanceId) {

//        if(!remedyServices.getQuestionMap().containsKey(columnName.trim())) {
//            remedyServices.addToQuestionMap(columnName.trim(), remedyServices.getQuestionId(templateInstanceId, columnName.trim()));
//        }
        if (data.trim().toLowerCase().equalsIgnoreCase("x")) {
            fullMap.put(columnName.trim(), columnName.trim());
        }

        if (data.trim().isEmpty() || data.trim().equalsIgnoreCase("NONE")) {

        } else {
            fullMap.put(columnName.trim(), data);
        }

    }
}
