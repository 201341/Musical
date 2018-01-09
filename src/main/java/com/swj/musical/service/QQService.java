package com.swj.musical.service;


/*
 *  Author: swj
 *  Date: 17-12-11 
 */


import com.google.gson.*;
import com.swj.musical.mapper.PlaylistMapper;
import com.swj.musical.pojo.Album;
import com.swj.musical.pojo.Artist;
import com.swj.musical.pojo.Playlist;
import com.swj.musical.pojo.Song;
import com.swj.musical.util.GsonUtil;
import com.swj.musical.util.HttpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Service
public class QQService {


    private static Logger logger = Logger.getLogger("QQService.class");

    @Autowired
    private static PlaylistMapper playlistMapper;

    public static String genUrlParams(String qqImgMid,String imgType) {
        String category,imgUrl;
        int len = qqImgMid.length();
        if(imgType.equals("artist")) {
            category = "mid_singer_300";
        }else if(imgType.equals("album")){
            category = "mid_album_300";
        }else {
            return "";
        }
        if(len > 2) {
            imgUrl = "http://imgcache.qq.com/music/photo/" + category + "/" + qqImgMid.charAt(len - 2) + "/" + qqImgMid.charAt(len - 1) + "/" + qqImgMid + ".jpg";
        }else {
            imgUrl = "http://imgcache.qq.com/music/photo/" + category + "/" + qqImgMid.charAt(len - 2) + "/" + qqImgMid.charAt(len - 1) + "/" + qqImgMid + ".jpg";
        }

        return imgUrl;

    }

    public static String qqGet(String url) throws IOException {
        Map<String,String> extra_headers = new HashMap<String,String>();
        extra_headers.put("Accept","*/*");
        extra_headers.put("Accept-Encoding","gzip,deflate,sdch");
        extra_headers.put("Accept-Language","zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        extra_headers.put("Connection","keep-alive");
        extra_headers.put("Content-Type","application/x-www-form-urlencoded");
        extra_headers.put("Host","i.y.qq.com");
        extra_headers.put("Referer","http://y.qq.com/y/static/taoge/taoge_list.html?pgv_ref=qqmusic.y.topmenu");
        extra_headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");

        return HttpUtil.doGet(url,extra_headers);
    }



    public static List<Playlist> listPlaylist() throws IOException {
        List<Playlist> result = new ArrayList<Playlist>();
        Map<String,String> map = new HashMap<String,String>();
        String response,res1;
        Gson gson = new Gson();
        int i=0;
        String url = "http://i.y.qq.com/s.plcloud/fcgi-bin/fcg_get_diss_by_tag.fcg?categoryId=10000000&sortId=1&sin=0&ein=29&format=jsonp&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=GB2312&outCharset=utf-8&notice=0&platform=yqq&jsonpCallback=MusicJsonCallback&needNewCode=0";
        response = qqGet(url);
        res1 = response.substring("MusicJsonCallback(".length(),response.length()-1);
        JsonObject jObj = new JsonParser().parse(res1).getAsJsonObject();
        JsonArray jsonArray = jObj.getAsJsonObject("data").getAsJsonArray("list");

        Iterator it = jsonArray.iterator();
        for (i=0;i<30;i++) {
            map.put("coverImgUrl",jsonArray.get(i).getAsJsonObject().get("imgurl").getAsString());
            map.put("title", jsonArray.get(i).getAsJsonObject().get("dissname").getAsString());
            map.put("playCount", jsonArray.get(i).getAsJsonObject().get("listennum").getAsString());
            map.put("listId" ,"qqplaylist_" + jsonArray.get(i).getAsJsonObject().get("dissid").getAsString());

            Playlist playlist = gson.fromJson(GsonUtil.mapToJson(map),Playlist.class);
            result.add(playlist);

        }
        return result;
    }

    public static Map<String,Object> getPlayList(String listId) throws IOException {
        if(listId == null) {
            return null;
        }
        List<Song> songTrack = new ArrayList<Song>();
        Map<String,Object> map = new HashMap<String,Object>();
        Map<String,String> playListMap = new HashMap<String,String>();
        Map<String,String> songMap = new HashMap<String,String>();
        String response,res1;
        int i;
        Gson gson = new Gson();
        String url = "http://i.y.qq.com/qzone-music/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&jsonpCallback=jsonCallback&nosign=1&disstid=" + listId +"&g_tk=5381&loginUin=0&hostUin=0&format=jsonp&inCharset=GB2312&outCharset=utf-8&notice=0&platform=yqq&jsonpCallback=jsonCallback&needNewCode=0";
        response = qqGet(url);
        res1 = response.substring("jsonCallback(".length(),response.length()-1);
 //       System.out.println(res1);

        JsonObject jObj1 = new JsonParser().parse(res1).getAsJsonObject();
        JsonArray jsonArray = jObj1.get("cdlist").getAsJsonArray();


        logger.info("getPlayList() works");

        playListMap.put("coverImgUrl",jsonArray.get(0).getAsJsonObject().get("logo").getAsString());
        playListMap.put("title",jsonArray.get(0).getAsJsonObject().get("dissname").getAsString());
        playListMap.put("playCount","0");
        playListMap.put("listId","qqplaylist_"+listId);
        Playlist playlist = gson.fromJson(GsonUtil.mapToJson(playListMap),Playlist.class);

        JsonArray jsonArray1 = jsonArray.get(0).getAsJsonObject().getAsJsonArray("songlist");

 //       System.out.println(jsonArray1);

        for (i=0;i<30;i++) {
            songMap.put("songId","qqtrack_"+jsonArray1.get(i).getAsJsonObject().get("songmid").getAsString());
            songMap.put("title", jsonArray1.get(i).getAsJsonObject().get("songname").getAsString());
            songMap.put("artist", jsonArray1.get(i).getAsJsonObject().get("singer").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
            songMap.put("artistId" ,"qqplaylist_" + jsonArray1.get(i).getAsJsonObject().get("singer").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsString());
            songMap.put("album",jsonArray1.get(i).getAsJsonObject().get("albumname").getAsString());
            songMap.put("albumId","qqalbum_"+jsonArray1.get(i).getAsJsonObject().get("albummid").getAsString());
 //           System.out.println(jsonArray1.get(i).getAsJsonObject().get("albummid").getAsString());
            songMap.put("imgUrl",genUrlParams(jsonArray1.get(i).getAsJsonObject().get("albummid").getAsString(),"album"));
            songMap.put("source","qq");
            songMap.put("sourceUrl","http://y.qq.com/#type=song&mid=" +jsonArray1.get(i).getAsJsonObject().get("songmid").getAsString()+ "&tpl=yqq_song_detail");

            Song song = gson.fromJson(GsonUtil.mapToJson(songMap),Song.class);
            songTrack.add(song);
        }


        map.put("tracks",songTrack);
        map.put("info",playlist);


        return  map;

    }



    public static Map<String,Object> getArtist(String artistId) throws IOException {
        String data;
        List<Song> songTrack = new ArrayList<Song>();
        Map<String,String> artistMap = new HashMap<String,String>();
        Map<String,String> songMap = new HashMap<String,String>();
        Map<String,Object> map = new HashMap<String,Object>();
        String url = "http://i.y.qq.com/v8/fcg-bin/fcg_v8_singer_track_cp.fcg?platform=h5page&order=listen&begin=0&num=50&singermid="+
                artistId + "&g_tk=938407465&uin=0&format=jsonp&inCharset=utf-8&outCharset=utf-8&notice=0&platform=" +
                "h5&needNewCode=1&from=h5&_=1459960621777&jsonpCallback=ssonglist1459960621772";
        String response = qqGet(url);
        data = response.substring(" ssonglist1459960621772(".length(),response.length()-1);

 //       System.out.println(data);
        JsonObject jObj = new JsonParser().parse(data).getAsJsonObject();
        JsonArray jsonArray = jObj.get("data").getAsJsonObject().get("list").getAsJsonArray();

        artistMap.put("coverImgUrl",genUrlParams(artistId,"artist"));
        artistMap.put("title",jObj.get("data").getAsJsonObject().get("singer_name").getAsString());
        artistMap.put("artistId","qqartist_"+artistId);
        logger.info("ArtistInfo" + GsonUtil.mapToJson(artistMap));

        Gson gson = new Gson();
        Artist artist = gson.fromJson(GsonUtil.mapToJson(artistMap),Artist.class);

        for(JsonElement jsonElement :jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject().get("musicData").getAsJsonObject();
            songMap.put("songId","qqtrack_"+jsonObject.get("songmid").getAsString());
            songMap.put("title", jsonObject.get("songname").getAsString());
            songMap.put("artist", jsonObject.get("singer").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
            songMap.put("artistId" ,"qqplaylist_" + jsonObject.get("singer").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsString());
            songMap.put("album",jsonObject.get("albumname").getAsString());
            songMap.put("albumId","qqalbum_"+jsonObject.get("albummid").getAsString());
            //           System.out.println(jsonArray1.get(i).getAsJsonObject().get("albummid").getAsString());
            songMap.put("imgUrl",genUrlParams(jsonObject.get("albummid").getAsString(),"album"));
            songMap.put("source","qq");
            songMap.put("sourceUrl","http://y.qq.com/#type=song&mid=" +jsonObject.get("songmid").getAsString()+ "&tpl=yqq_song_detail");
            Song song = gson.fromJson(GsonUtil.mapToJson(songMap),Song.class);
            songTrack.add(song);
        }


        map.put("tracks",songTrack);
        map.put("info",artist);


        return  map;


    }

    public static Map<String,Object> getAlbum(String albumId) throws IOException {
        String data;

        Map<String,Object> res = new HashMap<String,Object>();
        List<Song> songTrack = new ArrayList<Song>();
        Map<String,String> songMap = new HashMap<String,String>();
        Map<String,String> albumMap = new HashMap<String,String>();

        String url = "http://i.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?platform=h5page&albummid="+albumId+"&g_tk=938407465"+
                     "&uin=0&format=jsonp&inCharset=utf-8&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&_=1459961045571" +
                     "&jsonpCallback=asonglist1459961045566";

        String response = qqGet(url);
        data = response.substring(" asonglist1459961045566(".length(),response.length()-1);

        JsonObject jObj = new JsonParser().parse(data).getAsJsonObject();
        JsonArray jsonArray = jObj.get("data").getAsJsonObject().get("list").getAsJsonArray();

        albumMap.put("coverImgUrl",genUrlParams(albumId,"album"));
        albumMap.put("title",jObj.get("data").getAsJsonObject().get("name").getAsString());
        albumMap.put("id","qqalbum_"+albumId);

        Gson gson = new Gson();
        Album album = gson.fromJson(GsonUtil.mapToJson(albumMap),Album.class);


        for(JsonElement jsonElement :jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            songMap.put("songId","qqtrack_"+jsonObject.get("songmid").getAsString());
            songMap.put("title", jsonObject.get("songname").getAsString());
            songMap.put("artist", jsonObject.get("singer").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
            songMap.put("artistId" ,"qqplaylist_" + jsonObject.get("singer").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsString());
            songMap.put("album",jsonObject.get("albumname").getAsString());
            songMap.put("albumId","qqalbum_"+jsonObject.get("albummid").getAsString());
            //           System.out.println(jsonArray1.get(i).getAsJsonObject().get("albummid").getAsString());
            songMap.put("imgUrl",genUrlParams(jsonObject.get("albummid").getAsString(),"album"));
            songMap.put("source","qq");
            songMap.put("sourceUrl","http://y.qq.com/#type=song&mid=" +jsonObject.get("songmid").getAsString()+ "&tpl=yqq_song_detail");
            Song song = gson.fromJson(GsonUtil.mapToJson(songMap),Song.class);
            songTrack.add(song);
        }

        res.put("tracks",songTrack);
        res.put("info",album);

        return res;
    }

    public static  List<Song> searchTrack(String keywords) throws IOException {
        String data,response;
        Map<String,String> songMap = new HashMap<String,String>();
        List<Song> songTrack = new ArrayList<Song>();
        String url = "http://i.y.qq.com/s.music/fcgi-bin/search_for_qq_cp?g_tk=938407465&uin=0&format=jsonp&inCharset=utf-8"+
                "&outCharset=utf-8&notice=0&platform=h5&needNewCode=1&w="+keywords + "&zhidaqu=1&catZhida=1" +
                "&t=0&flag=1&ie=utf-8&sem=1&aggr=0&perpage=20&n=20&p=1&remoteplace=txt.mqq.all&_=1459991037831&jsonpCallback=jsonp4";

        response = qqGet(url);

        data = response.substring("jsonp4(".length(),response.length()-1);
        JsonObject jObj = new JsonParser().parse(data).getAsJsonObject();
        JsonArray jsonArray = jObj.get("data").getAsJsonObject().get("song").getAsJsonObject().get("list").getAsJsonArray();
        Gson gson = new Gson();
        for(JsonElement jsonElement :jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            songMap.put("songId","qqtrack_"+jsonObject.get("songmid").getAsString());
            songMap.put("title", jsonObject.get("songname").getAsString());
            songMap.put("artist", jsonObject.get("singer").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
            songMap.put("artistId" ,"qqplaylist_" + jsonObject.get("singer").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsString());
            songMap.put("album",jsonObject.get("albumname").getAsString());
            songMap.put("albumId","qqalbum_"+jsonObject.get("albummid").getAsString());
            //           System.out.println(jsonArray1.get(i).getAsJsonObject().get("albummid").getAsString());
            songMap.put("imgUrl",genUrlParams(jsonObject.get("albummid").getAsString(),"album"));
            songMap.put("source","qq");
            songMap.put("sourceUrl","http://y.qq.com/#type=song&mid=" +jsonObject.get("songmid").getAsString()+ "&tpl=yqq_song_detail");
            Song song = gson.fromJson(GsonUtil.mapToJson(songMap),Song.class);
            songTrack.add(song);
        }

        return songTrack;

    }



/*    public static void main(String[] args) throws IOException {
        List<Playlist> res = new ArrayList<Playlist>(30);
        try {
            res = listPlaylist();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Playlist playlist: res){
            System.out.println(playlist.getCoverImgUrl());
            System.out.println(playlist.getListId());
            System.out.println(playlist.getPlayCount());
            System.out.println(playlist.getTitle());

        }
        Map<String,Object> map = new HashMap<String,Object>();
        try {
            map = getPlayList("2016330420");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(GsonUtil.mapToJson(map));

        getArtist("neartist_11974075");
    }*/




}
