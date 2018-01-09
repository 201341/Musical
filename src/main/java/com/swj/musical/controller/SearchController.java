package com.swj.musical.controller;
/*  Author: swj
 *  Date: 12/19/17 
 */


import com.swj.musical.pojo.Song;
import com.swj.musical.service.QQService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {



    @RequestMapping(path = {"/search"})
    @ResponseBody
    public Map<String,Object> search(String source,String keywords) throws IOException {
        Map<String,Object> res = new HashMap<String,Object>();
        List<Song> trackList = new ArrayList<Song>();
        int index = Integer.parseInt(source);
        switch (index) {
            case 0:
                trackList = QQService.searchTrack(keywords);
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }

        res.put("result",trackList);
        return res;

    }

}
