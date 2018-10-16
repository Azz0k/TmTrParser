package tmtrparser;


import org.apache.poi.ss.formula.functions.T;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{

    static void test(){
        Iterator<Integer> it1 = Arrays.asList(1, 2, 3).iterator();
        Iterator<Integer> it2 = Arrays.asList(4, 5, 6).iterator();
        Iterator<Integer> it3 = Arrays.asList(7, 8, 9).iterator();
        Iterator<Iterator<Integer>> its = Arrays.asList(it1, it2, it3).iterator();
        Converter IteratorOfIterators = new Converter();
        Iterator<Integer> it;
        it = IteratorOfIterators.convert(its);
        for (int i = 0; i <10 ; i++) {
            boolean b=it.hasNext();
            int c=0;
            if (b)
                c=it.next();
            System.out.println(b+" "+c);

        }
    }

    public static void main( String[] args )
    {
        test();
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
        String cookiesFileName=properties.getProperty("cookies");
        SiteConnecton siteConnecton=null;
        try {
             siteConnecton = new SiteConnecton("online.tmtr.ru", loginName, password,cookiesFileName);
         }
         catch (Exception e){
             System.out.println(e.getLocalizedMessage());
         }
        TmTrPrice[] tmTrPrices=null;
        ExcelProcessor inputFile=new ExcelProcessor(inExcelFile, true);
        try {
            tmTrPrices=inputFile.open();
        }
        catch (Exception e)
        {
            System.out.println("Can't open input excel file");
            System.out.println(e.getLocalizedMessage());
            System.exit(0);
        }
        try {
 //           for (int i = 0; i <tmTrPrices.length ; i++) {


  //              System.out.println(siteConnecton.search(tmTrPrices[i]));
 //               Thread.sleep(( long)(Math.random() * 30000));
            System.out.println(siteConnecton.search(tmTrPrices[5]));
            //}
        }
        catch (Exception e){
            System.out.println(e.getLocalizedMessage());

        }
        finally {
            for (int i = 0; i <tmTrPrices.length ; i++) {
                System.out.println(tmTrPrices[i].toString());

            }
        }
    }
}
