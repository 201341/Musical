package com.swj.musical.controller;
import com.swj.musical.pojo.Playlist;
import com.swj.musical.service.DoubanService;
import com.swj.musical.service.QQService;
import com.swj.musical.util.GsonUtil;
import com.swj.musical.util.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PlaylistController {

    private static final Logger logger = Logger.getLogger(PlaylistController.class);

    @Autowired
    private static QQService qqService;



    @RequestMapping(path = {"/show_playlist"})
    @ResponseBody
    public static Response ShowPlaylist(String source) {
        logger.info("Info:start to show playlist");
        int index = Integer.parseInt(source);
        List<Playlist> result = new ArrayList<Playlist>();

        if(index >=0  && index <3) {
            try {
                result = qqService.listPlaylist();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            result = null;
        }


        return new Response(result);
    }


    @RequestMapping(path = {"/playlist"})
    @ResponseBody
    public static Map<String,Object>  PlayList(String list_id) throws IOException {
        String tmp,itemId;
        Map<String,Object> map = new HashMap<String,Object>();
        if(list_id != null) {
            tmp = list_id.substring(0, 3);
            itemId = list_id.split("_")[1];
        }else{
            tmp = "qq";
            itemId = "";
        }


        if(tmp.equals("my_")) {

        }else if(tmp.equals("qq")) {
            map=qqService.getPlayList(itemId);
        }else{
            map=qqService.getPlayList(itemId);
        }
        map.put("is_mine","0");
        return map;

    }

    @RequestMapping("/artist")
    @ResponseBody
    public static Map<String,Object> Artist(@RequestParam("artist_id") String artistId) throws IOException {
        Map<String,Object> res = new HashMap<String,Object>();
        String []re = artistId.split("_");
        if(re[0].equals("dbartist")) {

        }else if(re[0].equals("neartist")) {

        }else if(re[0].equals("xmartist")) {

        }else {
            res = qqService.getArtist(re[1]);
        }
        res.put("status","1");
        res.put("is_minne","0");

        return res;
    }


    @RequestMapping(path = {"/album"})
    @ResponseBody
    public static Map<String,Object> Album(@RequestParam("album_id") String albumId) throws IOException {
        Map<String,Object> res = new HashMap<String,Object>();

        String []re = albumId.split("_");
        if(re[0].equals("dbalbum")) {

        }else if(re[0].equals("nealbum")) {

        }else if(re[0].equals("xmalbum")) {

        }else {
            res = qqService.getAlbum(re[1]);
        }
        res.put("status","1");
        res.put("is_minne","0");

        return res;



    }

}
