package xyz.yhsj.kmusic;

import xyz.yhsj.kmusic.entity.MusicResp;
import xyz.yhsj.kmusic.entity.Song;
import xyz.yhsj.kmusic.impl.QQImpl;
import xyz.yhsj.kmusic.site.MusicSite;

import java.util.List;

public class JavaTest {

    public static void main(String args[]) {
        //未封装过的类调用,需要写上INSTANCE(因为实现接口,暂时没办法写成下面的形式)
        QQImpl.INSTANCE.getSongTop();
        //封装过的接口调用,和Java的静态方法一样
        MusicResp<List<Song>> tops = KMusic.search("薛之谦", 1, 10, MusicSite.QQ);
        System.out.println(tops);
    }


}
