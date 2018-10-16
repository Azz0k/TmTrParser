package tmtrparser;


import org.apache.poi.ss.formula.functions.T;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class SiteConnecton {

    private HttpQuery httpQuery;
    private String hostName;


    private String loginName;
    private String password;
    private HashMap<String,String> headers = new HashMap<>();


    private CookieManager cookieManager=new CookieManager();

    private String cookiesFileName;


    SiteConnecton(String name, String loginName, String password, String cookiesFileName) throws Exception
    {

        this.hostName=name;
        this.loginName=loginName;
        this.password=password;
        this.cookiesFileName=cookiesFileName;

        headers.put("Content-Type", "application/x-www-form-urlencoded");
        loadCookiesFromFile();
        if (!firstTouch()) {
            connect();
            saveCookiesToFile();
        }

    }
    private void loadCookiesFromFile()
    {


        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "\\config\\" + cookiesFileName);
            properties.loadFromXML(fileInputStream);
            fileInputStream.close();
        }
        catch (Exception e)
        {
            System.out.println("cookies not found");
        }
        //List<HttpCookie> list=new ArrayList<>();
        int i=0;
        String name;
        while ((name=properties.getProperty("name"+(++i)))!=null) {

            HttpCookie key=new HttpCookie(name, properties.getProperty(name));
            key.setComment(properties.getProperty(name+"comment").equals("null")?null:properties.getProperty(name+"comment"));
            key.setCommentURL(properties.getProperty(name+"commenturl").equals("null")?null:properties.getProperty(name+"commenturl"));
            key.setDiscard(properties.getProperty(name + "discard").toUpperCase().equals("TRUE"));
            key.setSecure(properties.getProperty(name + "secure").toUpperCase().equals("TRUE"));
            key.setHttpOnly(properties.getProperty(name + "httponly").toUpperCase().equals("TRUE"));
            key.setPortlist(properties.getProperty(name+"portlist").equals("null")?null:properties.getProperty(name+"portlist"));
            key.setVersion(Integer.parseInt(properties.getProperty(name+"version")));
            key.setDomain(properties.getProperty(name+"domain").equals("null")?null:properties.getProperty(name+"domain"));
            key.setPath(properties.getProperty(name+"path").equals("null")?null:properties.getProperty(name+"path"));
            key.setMaxAge(Long.parseLong(properties.getProperty(name+"age")));



            cookieManager.getCookieStore().add(URI.create(hostName), key);
        }

    }
    private void saveCookiesToFile()
    {

        Properties properties =new Properties();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
            List<HttpCookie> list=cookieManager.getCookieStore().getCookies();
            int i=0;
            for (HttpCookie key:list) {
                i++;
                properties.put("name"+i,key.getName());
                properties.put(key.getName(), key.getValue());
                properties.put(key.getName()+"comment", key.getComment()==null?"null":key.getComment());
                properties.put(key.getName()+"commenturl", key.getCommentURL()==null?"null":key.getCommentURL());
                properties.put(key.getName()+"discard", Boolean.toString(key.getDiscard()));
                properties.put(key.getName()+"secure", Boolean.toString(key.getSecure()));
                properties.put(key.getName()+"httponly", Boolean.toString(key.isHttpOnly()));
                properties.put(key.getName()+"portlist", key.getPortlist()==null?"null":key.getPortlist());
                properties.put(key.getName()+"version", ""+key.getVersion());
                properties.put(key.getName()+"domain", key.getDomain()==null?"null":key.getDomain());
                properties.put(key.getName()+"path", key.getPath()==null?"null":key.getPath());
                properties.put(key.getName()+"age", ""+key.getMaxAge());
            }
            //properties.put("cookie",cookieManager.getCookieStore().getCookies().toString());
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(System.getProperty("user.dir") + "\\config\\" + cookiesFileName);
            properties.storeToXML(fileOutputStream, "");
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            System.out.println("Can't write cookies");

        }
    }
    /*

    */
    private void connect() throws Exception
    {
        if (httpQuery==null)
            httpQuery =new HttpQuery(hostName,headers,cookieManager);
       // if (!httpQuery.httpgetput("/login.aspx","GET",200,null)){
       //     System.out.println("Can't connect to site "+hostName+ ". Please check your internet connection");
       //     System.exit(0);
        //}
         if (httpQuery.httpquery("/login.aspx","GET",null)!=200)
         {
             System.out.println("Can't connect to site "+hostName+ ". Please check your internet connection");
                  System.exit(0);
         }

        cookieManager=httpQuery.getCookieManager();
        String html=httpQuery.getBody();
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
        System.out.println("waiting 10 sec");
        Thread.sleep(10000+(long)(Math.random()*10000));
        String params="__VIEWSTATE="+state+"&";
        params+="__VIEWSTATEGENERATOR="+generator+"&";
        params+="tbLogin="+loginName+"&";
        params+="tbPassword="+password+"&";
        params+="btSubmit=%D0%92%D0%BE%D0%B9%D1%82%D0%B8";
        if (httpQuery.httpquery("/login.aspx","POST",params)!=302){
            System.out.println("Wrong login/password");
            //System.exit(0);
        }


        cookieManager=httpQuery.getCookieManager();

    }
    private String extractURLFromSearch(String body){
        Pattern pattern= Pattern.compile("href=\"(.*?)\"");
        Matcher matcher=pattern.matcher(body);
        return matcher.find()?matcher.group(1):null;

    }
    String search(TmTrPrice price) throws Exception
    {
        String name=price.getOurCode();
        if (httpQuery==null) {
            httpQuery = new HttpQuery(hostName, headers, cookieManager);

        }


        String params="SearchNumber="+name;//search.aspx?SearchNumber=
        long  resCode;
        resCode=httpQuery.httpquery("/search.aspx","POST",params);
        System.out.println("Searching..."+name+"  Result code="+resCode);
         if (resCode==200) {
         //    System.out.println(httpQuery.getBody());
             String body1 =httpQuery.getBody();
             Pattern patternFirm= Pattern.compile("<td class=\"Margin5\" width=\"180\"><font face=\"Tahoma\">(.*?)<",Pattern.DOTALL+Pattern.MULTILINE);
             Matcher matcherFirm=patternFirm.matcher(body1);
             Pattern patternArt= Pattern.compile("ctl00\\$mainPlace\\$gvSKN&#39;,&#39;\\$[0-9]+&#39;[\\)]\">(.*?)</a>",Pattern.DOTALL+Pattern.MULTILINE);
             Matcher matcherArt=patternArt.matcher(body1);
             while (matcherArt.find() & matcherFirm.find())
             {
                 if ((matcherFirm.group(1).toUpperCase().equals("EBS")) && (matcherArt.group(1).toUpperCase().replaceAll("\\.","").equals(name.toUpperCase())))
                 {
                     //params="SearchNumber="+name;
                     Pattern patternToken=Pattern.compile("id=\"TokenSessionID\" value=\"(.*?)\"",Pattern.DOTALL+Pattern.MULTILINE);
                     Matcher matcherToken=patternToken.matcher(body1);
                     Pattern patternCview=Pattern.compile("id=\"__CVIEWSTATE\" value=\"(.*?)\"",Pattern.DOTALL+Pattern.MULTILINE);
                     Matcher matcherCview=patternCview.matcher(body1);
                     Pattern patternTarget=Pattern.compile("<a href=\"javascript:__doPostBack[\\(]&#39;(.*?)&#39;,&#39;([\\$0-9]{1,2}?)&#39;[\\)]\">"+matcherArt.group(1)+"</a></font></td>",Pattern.DOTALL+Pattern.MULTILINE);
                     Matcher matcherTarget=patternTarget.matcher(body1);//Таргет и аргумент
                     OutputStream output=new ByteArrayOutputStream();
                     PrintWriter writer= new PrintWriter(new OutputStreamWriter(output,"UTF-8"),true);
                     String boundary="----WebKitFormBoundaryT2h2In2mJBDPPVqM";
                     String CRLF="\r\n";
                     if (matcherToken.find() & matcherCview.find() & matcherTarget.find()) {
                        writer.append("--"+boundary+CRLF);
                        writer.append("Content-Disposition: form-data; name=\"TokenSessionID\""+CRLF+CRLF);
                        writer.append(matcherToken.group(1));
                        writer.flush();
                        writer.append(CRLF+"--"+boundary+CRLF);
                        writer.append("Content-Disposition: form-data; name=\"__EVENTTARGET\""+CRLF+CRLF);
                        writer.append(matcherTarget.group(1));
                        writer.flush();
                        writer.append(CRLF+"--"+boundary+CRLF);
                        writer.append("Content-Disposition: form-data; name=\"__EVENTARGUMENT\""+CRLF+CRLF);
                        writer.append(matcherTarget.group(2));
                        writer.flush();
                        writer.append(CRLF+"--"+boundary+CRLF);
                        writer.append("Content-Disposition: form-data; name=\"__CVIEWSTATE\""+CRLF+CRLF);
                        writer.append(matcherCview.group(1));
                        writer.flush();
                        writer.append(CRLF+"--"+boundary+CRLF);
                        writer.append("Content-Disposition: form-data; name=\"__CVIEWSTATE\""+CRLF+CRLF);
                        writer.append(matcherCview.group(1));
                        writer.flush();



              //           paramsBuilder.append("&");
                //         paramsBuilder.append("__VIEWSTATE=");
                  //       paramsBuilder.append("&");
                    //     paramsBuilder.append("__VIEWSTATEENCRYPTED=");
                      //   paramsBuilder.append("&");
                        // paramsBuilder.append("SearchInputHidden=");
                         //params=paramsBuilder.toString();

                         //params += "&TokenSessionID=" + matcherToken.group(1) + "&__EVENTTARGET=" + matcherTarget.group(1) + "&__EVENTARGUMENT=" + matcherTarget.group(2) + "&__CVIEWSTATE=" + matcherCview.group(1) + "&__VIEWSTATE=";
                     }


                     Thread.sleep(( long)(Math.random() * 30000));
                     HashMap<String,String> headers1 = new HashMap<>();


                     headers1.put("Content-Type", "multipart/form-data; boundary="+boundary);

                     httpQuery.setHeaders(headers1);
                     httpQuery.httpquery("/search.aspx?SearchNumber="+name,"POST",params);
                     break;
                 }

             }
         }


        httpQuery.setHeaders(headers);
        String body =httpQuery.getBody();
        String newaddress=extractURLFromSearch(body);
        System.out.println("Searching... Result code="+httpQuery.httpquery(newaddress,"GET",null));
        body =httpQuery.getBody();
        Pattern patternFirm= Pattern.compile("\" class=\"FirmTH\">(.*?)<",Pattern.DOTALL+Pattern.MULTILINE);
        Matcher matcherFirm=patternFirm.matcher(body);
        Pattern patternNumber= Pattern.compile("\" class=\"NumberTH\">(.*?)<",Pattern.DOTALL+Pattern.MULTILINE);
        Matcher matcherNumber=patternNumber.matcher(body);
        Pattern patternCount= Pattern.compile("\" class=\"CurCountTH\">(.*?)<",Pattern.DOTALL+Pattern.MULTILINE);
        Matcher matcherCount=patternCount.matcher(body);
        Pattern patternPrice= Pattern.compile("<b style=\"font-size: 12px; text-align:center; position: relative\">(.*?)</b>",Pattern.DOTALL+Pattern.MULTILINE);
        Matcher matcherPrice=patternPrice.matcher(body);

        while (matcherFirm.find() & matcherNumber.find() & matcherCount.find() & matcherPrice.find())
            {
                String tempFirm=matcherFirm.group(1).replaceAll("\\W","");
                String tempNum=matcherNumber.group(1).replaceAll("\\W","");
                String tempCount=matcherCount.group(1).replaceAll("\\W","");
                String tempPrice=matcherPrice.group(1).replaceAll("\\W","");

            System.out.println(tempFirm+" "+tempNum+" "+tempCount+" "+tempPrice);
            if (tempFirm.toUpperCase().equals("EBS"))
            {
                price.setTheirCode(tempNum);
                price.setMinPrice(Integer.parseInt(tempPrice));
                price.addAvailability(Integer.parseInt(tempCount));
            }
            }
       //     System.out.println(body);
        return "";
    }
    private boolean firstTouch() throws Exception
    {
        if (httpQuery==null)
            httpQuery =new HttpQuery(hostName,headers,cookieManager);

        if (httpQuery.httpquery("/main.aspx","GET",null)!=200){
        //    String body =httpQuery.getBody();
        //    System.out.println(body);
            System.out.println("Cookies did not fit. Trying to authenticate");
            //System.exit(0);
        }


        //System.out.println("Timeout, another try");
        return httpQuery.getStatuscode() == 200;
    }




}
