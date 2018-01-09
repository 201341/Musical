package com.swj.musical.service;
/*
 *  Author: swj
 *  Date: 17-12-11 
 */

import com.swj.musical.mapper.UserMapper;
import com.swj.musical.pojo.User;
import com.swj.musical.util.GsonUtil;
import com.swj.musical.util.HttpUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;



@Service
public class DoubanService {
    private static Logger logger = Logger.getLogger("DoubanService.classs");


    @Autowired
    private static UserMapper userMapper;

    public static void savaImgToPath(String url,String path) throws Exception {
        logger.info("Fetching url: " + url);
        InputStream inStream;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        httpGet.setHeader("User-Agent",userAgent);
        CloseableHttpResponse response = httpclient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        inStream = entity.getContent();

        byte[] data = HttpUtil.readInputStream(inStream);
        File imageFile = new File(path);
        logger.info("path:"+imageFile.getAbsolutePath());
        FileOutputStream outStream = new FileOutputStream(imageFile);
        outStream.write(data);
        logger.info("sucess:save valid to dir tmp");
        outStream.close();



    }
    public static String getCaptchaToken(String path) throws Exception {
        String humanUrl = "https://douban.fm/j/new_captcha";
        String tmp = HttpUtil.doGet(humanUrl,null);
        String captchaToken = tmp.substring(1,tmp.length()-1);
        String picUrl = "http://douban.fm/misc/captcha?size=m&id="+captchaToken;
//        String c = HttpUtil.doGet(picUrl,null);
        savaImgToPath(picUrl,path);
        logger.info("tmp:"+tmp);
        return captchaToken;

    }

    public static Map<String,String> getCaptCha() throws Exception {
        Map<String,String> map = new HashMap<String,String>();
        String uuid = UUID.randomUUID().toString();
        String filename = uuid.replaceAll("-","")+".jpg";
        String path =  "/home/swj/IdeaProjects/Musical/src/main/webapp/temp/"+filename;
        String path1 = "temp/" + filename;
        String token = getCaptchaToken(path);
        map.put("path",path1);
        map.put("token",token);
        return map;

    }

    public static String getDbFilePath() {
        String rootDir = "/home/swj/IdeaProjects/Musical/src/main/webapp/user/";
        String fileName = rootDir + "douban_userinfo.json";
        return fileName;
    }


    public static Map<String,Object> getDbCkTk() throws IOException {
        Map<String,Object> map = new HashMap<>();
        String path = getDbFilePath();
        File file = new File(path);
        if(!file.exists()) {
            return null;
        }
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(reader);
        String s = null;
        while((s = bufferedReader.readLine()) != null) {
            map = GsonUtil.toMap(s);
        }
        reader.close();

        return  map;

    }


    public static void setDbCkTk(Map<String,Object> map) throws IOException {
        String path = getDbFilePath();
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        out.write(GsonUtil.mapToJson(map));
        out.close();
    }

    public static void removeDbCkTk() {
        String path = getDbFilePath();
        File file = new File(path);
        file.delete();

    }


    public static String login(String user,String password,String token,String solution) throws IOException {
        String res;
        String loginUrl = "https://douban.fm/j/login";
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
        String referer = "http://douban.fm/";
        String xwith = "XMLHttpRequest";
        String cookie = "openExpPan=Y; flag=\"ok\"; ac=\"1448675235\"; bid=\"2dLYThADnhQ\";_pk_ref.100002.6447=%5B%22%22%2C%22%22%2C1448714740%2C"+
                "%22http%3A%2F%2Fwww.douban.com%2F%22%5D; _ga=GA1.2.1634773349.1402330632; _pk_id.100002.6447=7d9729e0b4385d49.14023306"+
                "33.88.1448714753.1448700119.; _pk_ses.100002.6447=*; dbcl2=\""+
                token+"\"; fmNlogin=\"y\"; ck=\"boPw\"; _gat=1";

        Map<String,String> data = new HashMap<String,String>();
        data.put("source","radio");
        data.put("alias",user);
        data.put("form_password",password);
        data.put("captcha_id",token);
        data.put("captcha_solution",solution);
        data.put("task","sync_channel_list");


        Map<String,String> headers = new HashMap<String,String>();
        headers.put("User-Agent",userAgent);
        headers.put("Refer",referer);
        headers.put("Cookie",cookie);
        headers.put("X-Request-With",xwith);
        headers.put("Accpt-language","zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2");
        headers.put("Accept-Encoding","gzip, deflate, sdch");
        res = HttpUtil.loginPost(loginUrl,headers,data,"utf-8");

        return res;


    }


    public static void clearTmpFolder() {
        String path = "/home/swj/IdeaProjects/Musical/src/main/webapp/temp";
        File file = new File(path);
        if(file.exists() && file.isDirectory()) {
            File delFile[] = file.listFiles();
            int i = file.listFiles().length;
            for(int j=0;j<i;j++) {
                delFile[j].delete();
            }
        }
    }


    public static void insertUser(User user) {
        userMapper.insertUser(user);
    }

/*    public static void main(String[] args) throws Exception {
        String res;
        res = login("1186093704@qq.com","/nswj0707","iuyLol4s7TpbT0Xu1gj8yGVr:en","account");
    }*/
}
