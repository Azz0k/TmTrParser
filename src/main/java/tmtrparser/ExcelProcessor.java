package tmtrparser;



import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelProcessor  implements AutoCloseable {
    private String fileName;
    private XSSFWorkbook excelWorkBook;
    private XSSFSheet excelSheet;
    boolean isOnlyRead;

    public ExcelProcessor(String fileName, boolean isOnlyRead) {
        this.fileName = fileName;
        this.isOnlyRead = isOnlyRead;
    }
    public void open() throws FileNotFoundException, IOException {
        excelWorkBook = new XSSFWorkbook(new FileInputStream(fileName));
        excelSheet = excelWorkBook.getSheetAt(0);
    }

    public String getCell(int intRow,int intCol)
    {
        return excelSheet.getRow(intRow).getCell(intCol).getStringCellValue();
    }

    @Override
    public void close() throws IOException
    {
        if (excelWorkBook!=null) {
            if (!isOnlyRead)
                excelWorkBook.write(new FileOutputStream(fileName));
            excelWorkBook.close();
        }
    }


}
