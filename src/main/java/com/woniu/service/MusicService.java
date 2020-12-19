package com.woniu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.woniu.domain.Music;
import com.woniu.mapper.MusicMapper;

public interface MusicService extends IService<Music> {

    void addMusic(Music music);

    Object updateVisitById(Integer id);

    Object updateLikesById(Integer id);

    void flushSort();

    Object findSort();
}
