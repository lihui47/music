package com.woniu.controller;

import com.woniu.domain.Music;
import com.woniu.dto.Result;
import com.woniu.dto.StatusCode;
import com.woniu.service.MusicService;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/music")
public class MusicController {
    @Resource
    private MusicService musicService;
    //添加歌曲
    @GetMapping("add/{name}")
    public Result addMusic(@PathVariable  String name){
        val music = new Music();
        music.setName(name);
        musicService.addMusic(music);
        return new Result(true, StatusCode.OK,"新增成功");
    }
    //访问量
    @GetMapping("visit/{id}")
    public Result updateVisitById(@PathVariable Integer id){
        return new Result(true,StatusCode.OK,"访问数量更新成功",musicService.updateVisitById(id));
    }
    //点赞量
    @GetMapping("likes/{id}")
    public Result updateLikesById(@PathVariable Integer id){
        return new Result(true,StatusCode.OK,"点赞数量更新成功",musicService.updateLikesById(id));
    }
    //刷新排行榜
    @GetMapping("flushSort")
    public Result flushSort(){
        musicService.flushSort();
        return new Result(true,StatusCode.OK,"刷新排行榜成功");
    }
    //查看排行榜
    @GetMapping("findSort")
    public Result showSort(){
        return new Result(true,StatusCode.OK,"查看排行榜成功",musicService.findSort());
    }
}
