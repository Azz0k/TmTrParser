package tmtrparser;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.CoreConnectionPNames;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SiteConnecton {
    boolean isConnected=false;

    private String hostName;
    private RequestSpecification request;
    private Response response;
    private String loginName;
    private String password;
    private HashMap<String,String> headers = new HashMap<>();
    private HashMap<String,String> params = new HashMap<>();
    private Map<String,String> cookies = new HashMap<>();
    private RestAssuredConfig config;

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }


    public SiteConnecton(String name, String loginName, String password) throws InterruptedException
    {
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 10000));
        this.hostName=name;
        this.loginName=loginName;
        this.password=password;
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        request=RestAssured.given();
        request.config(config);
        request.headers(headers);
        request.baseUri(hostName);
        boolean isTimeOut;
        do {
            isTimeOut=false;

            try {
                response = request.get("/login.aspx");
            } catch (Exception e) {
                isTimeOut = true;
                System.out.println("Timeout, another try");
            }
        }
        while (isTimeOut);
        if (!(response.getStatusCode()==200)) {
            System.out.println("Can't connect to site "+hostName+ ". Please check your internet connection");
            System.exit(0);
        }
        cookies= response.cookies();
        System.out.println("Connecting...");
        String html=response.getBody().asString();
        html = html.replaceAll("\\r\\n","");
        Pattern pattern= Pattern.compile("id=\"__VIEWSTATE\" value=\"(.*?)\" /><input type=\"hidden\" name=\"__VIEWSTATEGENERATOR\" id=\"__VIEWSTATEGENERATOR\" value=\"(.*?)\"");
        Matcher matcher=pattern.matcher(html);
        String state="";
        String generator="";
        if (matcher.find() && (matcher.groupCount()==2)) {

            state=matcher.group(1);
            generator=matcher.group(2);
            }
         else
        {
            System.out.println("the structure of the authorization document has changed");
            System.exit(0);
        }
        System.out.println("waiting 30 sec");
        Thread.sleep(30000+(long)(Math.random()*10000));
        params.put("__VIEWSTATE",state);
        params.put("__VIEWSTATEGENERATOR",generator);
        params.put("tbLogin",loginName);
        params.put("tbPassword",password);
        params.put("btSubmit","%D0%92%D0%BE%D0%B9%D1%82%D0%B8");
        request.given();
        request.config(config);
        request.cookies(cookies);
        request.headers(headers);
        request.baseUri(hostName);
        request.queryParams(params);
        do {
            isTimeOut=false;

            try {
        response=request.post("/login.aspx");
            } catch (Exception e) {
                isTimeOut = true;
                System.out.println("Timeout, another try");
            }
        }
        while (isTimeOut);
        if (!(response.statusCode()==302))
        {
            System.out.println("Wrong login/password");
            System.exit(0);
        }

        cookies.putAll(response.cookies());
        isConnected=true;
    }
    public String search(String name)
    {
        request.given();
        request.cookies(cookies);
        request.config(config);
        request.headers(headers);
        request.baseUri(hostName);

        boolean isTimeOut;
        do {
            isTimeOut=false;

            try {
                response=request.post("/search.aspx?SearchNumber="+name);
            } catch (Exception e) {
                isTimeOut = true;
                System.out.println("Timeout, another try");
            }
        }
        while (isTimeOut);
        String body =response.getBody().asString();
        System.out.println(body);
        System.out.println("Timeout, another try");
        return "";
    }






}
