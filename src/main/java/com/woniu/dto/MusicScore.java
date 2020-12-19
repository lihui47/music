package com.woniu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MusicScore implements Comparable<MusicScore> {
    private Integer id;
    private Integer likesScore;
    private Integer visitScore;

    //比较并排序
    @Override
    public int compareTo(MusicScore musicScore) {
        if (likesScore > musicScore.getLikesScore()) {
            return -1;
        } else if (likesScore == musicScore.getLikesScore()) {
            if (visitScore > musicScore.getVisitScore()) {
                return -1;
            } else if (visitScore == musicScore.getVisitScore()) {
                return 0;
            }
        }
        return 1;
    }
}
