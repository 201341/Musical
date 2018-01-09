package com.swj.musical.controller;
/*  Author: swj
 *  Date: 17-12-16 
 */



import com.swj.musical.pojo.User;
import com.swj.musical.service.DoubanService;
import com.swj.musical.util.GsonUtil;
import com.swj.musical.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.HashMap;

import java.util.Map;

@Controller
public class DoubanController {

    @Autowired
    private static DoubanService doubanService;




    @RequestMapping(path = {"/dbvalidcode"})
    @ResponseBody
    public static String ValidCode() throws Exception {
        Map<String,Object> map = new HashMap<String,Object>();
        Map<String,Object> res = new HashMap<String,Object>();
        map = doubanService.getDbCkTk();
        if(map == null) {
            res.put("isLogin","0");
        }else{
            res.put("isLogin","1");
        }
        Map<String,String> tmp = doubanService.getCaptCha();
        res.put("captcha",tmp);
        return GsonUtil.mapToJson(res);
    }


    @RequestMapping(path = {"/dblogin"},method = RequestMethod.POST)
    @ResponseBody
    public static String DBlogin(@RequestParam("user") String user,
                                   @RequestParam("password") String password,
                                   @RequestParam("token") String token,
                                   @RequestParam("solution") String solution) throws Exception {
        Map<String,String> res = new HashMap<String,String>();
        token = doubanService.login(user,password,token,solution);
        String []re;
        String realtoken,ck;
        Map<String,Object> userInfo = new HashMap<String,Object>();
        if( token == null) {
            res = doubanService.getCaptCha();
            res.put("success","0");

        }else{
            re = token.split("|");
            realtoken = re[0];
            ck = re[1];
            User user1 = new User();
            user1.setUsername(user);
            user1.setPassword(password);
            user1.setCk(ck);
            user1.setTk(realtoken);
            user1.setToken(token);
            doubanService.insertUser(user1);
            userInfo.put("token",realtoken);
            userInfo.put("ck",ck);
            doubanService.setDbCkTk(userInfo);
            res.put("token",token);
            res.put("success","1");
            res.put("ck",ck);
            res.put("tk",realtoken);
            doubanService.clearTmpFolder();
        }

        return GsonUtil.mapToJson(res);

    }

    @RequestMapping(path = {"/dblogout"})
    @ResponseBody
    public static Response DBLogout() {
        doubanService.removeDbCkTk();
        Map<String,String> res = new HashMap<String,String>();
        res.put("success","1");
        return new Response(GsonUtil.mapToJson(res));
    }

    @RequestMapping(path = {"/dbfav"})
    @ResponseBody
    public static Response DBFavorite() {
        return new Response();

    }
}
