package tmtrparser;



import org.apache.commons.lang3.ObjectUtils;
import  org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExcelProcessor {
    private String fileName;
    private XSSFWorkbook excelWorkBook;
    private XSSFSheet excelSheet;
    private boolean isOnlyRead;


    public ExcelProcessor(String fileName, boolean isOnlyRead) {
        this.fileName = fileName;
        this.isOnlyRead = isOnlyRead;

    }

    TmTrPrice[] open() throws FileNotFoundException, IOException {
        excelWorkBook = new XSSFWorkbook(new FileInputStream(fileName));
        excelSheet = excelWorkBook.getSheetAt(0);
        TmTrPrice[] dataContainer;
        int index = -1;
        String tempData;
        ArrayList<TmTrPrice> tempContainer = new ArrayList<>();
        do {
            if (index == 100) {
                System.out.println();
            }
            tempData = getCell(++index, 0);
            if (!tempData.isEmpty())
                tempContainer.add(new TmTrPrice(tempData));

        }
        while (!tempData.isEmpty());
        dataContainer = tempContainer.toArray(new TmTrPrice[index + 1]);
        excelWorkBook.close();
        return dataContainer;
    }


    private String getCell(int intRow,int intCol)
    {
        try {
            excelSheet.getRow(intRow).getCell(intCol);
        }
        catch (NullPointerException e) {
            return "";
        }
        if (excelSheet.getRow(intRow).getCell(intCol).getCellType()==CellType.NUMERIC)
            return ""+excelSheet.getRow(intRow).getCell(intCol).getNumericCellValue();
        else if (excelSheet.getRow(intRow).getCell(intCol).getCellType()==CellType.STRING)
            return ""+excelSheet.getRow(intRow).getCell(intCol).getStringCellValue();
        else
        return "";
    }


}
