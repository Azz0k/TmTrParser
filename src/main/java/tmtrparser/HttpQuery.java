package tmtrparser;


import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;



 class HttpQuery {
    HttpClient httpClient;
    private String hostname;

    private String body;



    private long statuscode;
    private HashMap<String, String> headers;

    private CookieManager cookieManager;



     long getStatuscode() {

        return statuscode;
    }

     HttpQuery(String hostName, HashMap<String, String> headers, CookieManager cookies) {
        this.hostname = hostName;
        this.headers = headers;
        this.cookieManager=cookies;

    }





     String getBody() {
        return body;
    }

     CookieManager getCookieManager() {
        return cookieManager;
    }



     private String getParams(HashMap<String, String> params){

        StringBuilder stringBuffer= new StringBuilder();
        for (String key:params.keySet())

            stringBuffer.append(key+"="+params.get(key)+"&");
        String res=stringBuffer.toString();
        return res.substring(0,res.length()-1);

    }


     long httpquery(String urlName, String method,String params) throws Exception
    {
        String prefix="http://";
        if (httpClient==null)
             httpClient = HttpClient.newBuilder().cookieHandler(cookieManager).build();
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(prefix+hostname+urlName));
        for (String key:headers.keySet())
            httpRequestBuilder=httpRequestBuilder.header(key,headers.get(key));
        if (method.toUpperCase().equals("GET"))
            httpRequestBuilder.GET();
        else if (method.toUpperCase().equals("POST"))
                httpRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(params));
        HttpRequest httpRequest=httpRequestBuilder.build();
        HttpResponse<String> response =httpClient.send(httpRequest,HttpResponse.BodyHandlers.ofString());
        body=response.body();



    return statuscode=response.statusCode();
    }


}
