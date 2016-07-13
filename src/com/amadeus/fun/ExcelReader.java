/*
 * Amadeus Confidential Information:
 * Unauthorized use and disclosure strictly forbidden.
 * @1998-2015 - Amadeus s.a.s - All Rights Reserved.
 */
package com.amadeus.fun;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 
 * 
 * @author bmallick
 */
public class ExcelReader {
  ConfigReader cfg = new ConfigReader();

  public HashMap<Double, String> getBirthdayBabies() {
    HashMap<Double, String> birthdayBabies = new HashMap<Double, String>();

    try {
      SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
      Date currentDate = new Date();
      // Date currentDate = sdf.parse("10-4");

      FileInputStream fileInputStream = new FileInputStream(cfg.getPropValue("BIRTHDAY_EXCEL"));
      HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
      HSSFSheet worksheet = workbook.getSheet(cfg.getPropValue("BIRTHDAY_SHEET"));

      int i = 1;
      while (worksheet.getRow(i) != null) {
        HSSFRow row = worksheet.getRow(i);

        HSSFCell idCell = row.getCell(0);
        double id = idCell.getNumericCellValue();

        HSSFCell nameCell = row.getCell(1);
        String name = nameCell.getStringCellValue();

        HSSFCell bdayCell = row.getCell(2);
        Date bday = bdayCell.getDateCellValue();

        HSSFCell emailCell = row.getCell(3);
        String email = emailCell.getStringCellValue();

        HSSFCell imageCell = row.getCell(4);
        String image = imageCell.getStringCellValue();

        HSSFCell teamCell = row.getCell(5);
        String team = teamCell.getStringCellValue();

        if (sdf.format(currentDate).equals(sdf.format(bday))) {
          birthdayBabies.put(id, name + ":" + email + ":" + image + ":" + team);
        }

        i++;
      }
    }
    catch (FileNotFoundException e) {
      MailerService.logger.error(e.getMessage());
    }
    catch (IOException e) {
      MailerService.logger.error(e.getMessage());
    }
    catch (Exception e) {
      MailerService.logger
          .error("Excel sheet could not be fully parsed! Please verify if all values are in correct format! ERROR : " +
              e.getMessage());
    }
    return birthdayBabies;
  }
}