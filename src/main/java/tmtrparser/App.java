package tmtrparser;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        try {
            fileInputStream = new FileInputStream(System.getProperty("user.dir") + "\\config\\tmtrparser.ini");
            properties.load(fileInputStream);
            fileInputStream.close();
            }
        catch (IOException e) {
            System.out.println("Can't open ini file");
            System.out.println(e.getLocalizedMessage());
            System.exit(0);
        }

        String loginName=properties.getProperty("login");
        String password=properties.getProperty("password");
        String inExcelFile=properties.getProperty("from");
        String outExcelFile=properties.getProperty("to");
        SiteConnecton siteConnecton=null;
        try {
             siteConnecton = new SiteConnecton("http://online.tmtr.ru", loginName, password);
         }
         catch (InterruptedException e){
             //НИНУЖНО
         }

        ExcelProcessor inputFile=new ExcelProcessor(inExcelFile, true);
        try {
            inputFile.open();
        }
        catch (Exception e)
        {
            System.out.println("Can't open input excel file");
            System.out.println(e.getLocalizedMessage());
            System.exit(0);
        }
        System.out.println(siteConnecton.search(inputFile.getCell(0,0)));

    }
}
