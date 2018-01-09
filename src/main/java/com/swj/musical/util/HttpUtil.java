package com.swj.musical.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/*
 *  Author: swj
 *  Date: 17-12-13 
 */
public class HttpUtil {

    private static Logger logger = Logger.getLogger("HttpUtil.class");

    public static String loginPost(String url, Map<String,String> extra_headers, Map<String,String> map,String encoding) throws IOException {
        String result;
        String ck="",cookiesStr="",tmp =  "",cookieStr = "";

        String user_agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        CloseableHttpClient client = HttpClients.createDefault();
        Map<String,Object> res = new HashMap<String, Object>();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent",user_agent);
        if(extra_headers != null) {
            for(Map.Entry<String,String> entry: extra_headers.entrySet()) {
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
        }

        // 装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);

        if (response.getHeaders("Content-Encoding").equals("gzip")) {
            logger.info("Response is gzip");
        }

        HttpEntity entity = response.getEntity();
        tmp = EntityUtils.toString(entity,encoding);
        logger.info("result"+ tmp);
        JsonObject jObj1 = new JsonParser().parse(tmp).getAsJsonObject();
        //  login success
        if( jObj1.get("r").getAsString().equals("0")) {

            ck = jObj1.get("user_info").getAsJsonObject().get("ck").getAsString();
            logger.info("ck"+ck);
            Header[] cookies= response.getHeaders("Set-Cookie");
            for(Header cookie:cookies) {
                Matcher match = Pattern.compile("dbcl2=\"([^\"]+)\"").matcher(cookie.getValue());
                if(match.find()){
                    cookiesStr = match.group();
                    logger.info("cookie:"+cookiesStr);
                }
            }

            cookieStr = cookiesStr.substring(cookiesStr.indexOf("\"")+1,cookiesStr.lastIndexOf("\""));
            result = cookieStr + "|" + ck;
        }else{
        // login fail
            result = null;
        }



        EntityUtils.consume(entity);
        response.close();
        return  result;

    }


    public static String doGet(String url, Map<String,String> extra_headers) throws IOException {
        String result = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String user_agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        httpGet.setHeader("User-Agent",user_agent);
        if(extra_headers != null) {
            for(Map.Entry<String,String> entry: extra_headers.entrySet()) {
                httpGet.setHeader(entry.getKey(),entry.getValue());
            }
        }
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // 按指定编码转换结果实体为String类型
            result = EntityUtils.toString(entity, "utf8");
        }
        EntityUtils.consume(entity);
        response.close();
        return result;

    }


    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[10240];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
