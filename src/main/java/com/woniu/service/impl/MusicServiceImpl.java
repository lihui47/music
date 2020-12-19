package com.woniu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.woniu.domain.Music;
import com.woniu.dto.MusicScore;
import com.woniu.mapper.MusicMapper;
import com.woniu.service.MusicService;
import io.netty.util.internal.ObjectUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Resource
    private MusicMapper musicMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void addMusic(Music music) {
        music.setCreate_time(System.currentTimeMillis());
        music.setVisit_count(0);
        music.setLikes_count(0);
        //将音乐添加进数据库
        QueryWrapper<Music> wrapper = new QueryWrapper<>();
        wrapper.eq("name",music.getName());
        Music musicdb = musicMapper.selectOne(wrapper);
        if(ObjectUtils.isEmpty(musicdb)){
            musicMapper.insert(music);
            redisTemplate.opsForHash().put("music:"+music.getId(),
                    "likes:count",
                    music.getLikes_count().toString());
            redisTemplate.opsForHash().put("music:"+music.getId(),
                    "visit:count",
                    music.getVisit_count().toString());
            redisTemplate.opsForSet().add("music:ids",music.getId().toString());
        }


    }

    @Override
    public Object updateVisitById(Integer id) {
        //根据id从redis中取出访问量
        System.out.println(111);
        String visitCount =(String)redisTemplate.opsForHash().get("music:" + id, "visit:count".toString());
        System.out.println(visitCount);
            if(!visitCount.toString().equals("")){
                Integer upvisitCount=Integer.parseInt(visitCount.toString());
                upvisitCount+=1;
                redisTemplate.opsForHash().put("music:"+id, "visit:count", upvisitCount.toString());
                return upvisitCount;
            }
        return null;
    }

    @Override
    public Object updateLikesById(Integer id) {
        //根据id从redis中取出访问量
        System.out.println(111);
        String likesCount =(String)redisTemplate.opsForHash().get("music:" + id, "likes:count".toString());
        System.out.println(likesCount);
        if(!likesCount.toString().equals("")){
            Integer uplikesCount=Integer.parseInt(likesCount.toString());
            uplikesCount+=1;
            redisTemplate.opsForHash().put("music:"+id, "likes:count", uplikesCount.toString());
            return uplikesCount;
        }
        return null;
    }

    @Override
    public void flushSort() {
        //查询所有音乐的id值
        Set<Object> ids = redisTemplate.opsForSet().members("music:ids");
        List<MusicScore> sortMusicScores=new ArrayList<>();
        ids.forEach(id->{
            Object visitCount = redisTemplate.opsForHash().get("music:" + id, "visit:count");
            Object likesCount = redisTemplate.opsForHash().get("music:" + id, "likes:count");
           //将缓存中的点赞量和访问量更新到数据库
            musicMapper.updateById(
                    Music.builder()
                            .id(Integer.parseInt(id.toString()))
                            .visit_count(Integer.parseInt(visitCount.toString()))
                            .likes_count(Integer.parseInt(likesCount.toString()))
                            .build()
            );
            //将缓存中的点赞量和访问量添加到集合中，为了排序
            sortMusicScores.add(MusicScore.builder()
                    .id(Integer.parseInt(id.toString()))
                    .visitScore(Integer.parseInt(visitCount.toString()))
                    .likesScore(Integer.parseInt(likesCount.toString()))
                    .build());
        });
        //调用排序
        Collections.sort(sortMusicScores);
        sortMusicScores.forEach(System.out::println);
        //每次刷新排行榜，需要将之前的排行榜删除
        redisTemplate.delete("music:sort");
        for(int i=0;i<3;i++){
            MusicScore musicScore = sortMusicScores.get(i);
            redisTemplate.opsForZSet().add("music:sort",
                    musicScore.getId().toString(),musicScore.getLikesScore());
        }


    }

    @Override
    public Object findSort() {
        //查询所有排过序的音乐id
        Set<Object> sorts = redisTemplate.opsForZSet().range("music:sort", 0, -1);
        //根据id从数据库中查询数量并返回
        //以下代码为正序排序
        Object[] objects = sorts.toArray();
        ArrayList<Music> musics = new ArrayList<>();
        for (int i = objects.length-1; i >=0; i--) {
            QueryWrapper<Music> wrapper = new QueryWrapper<>();
            wrapper.eq("id",Integer.parseInt(objects[i].toString()));
            musics.add(musicMapper.selectOne(wrapper));
            musics.forEach(music -> {
                System.out.println(music);
            });

        }
        return musics;
    }
}
