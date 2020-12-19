package com.woniu.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_music")
public class Music {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Long create_time;
    private Integer visit_count;
    private Integer likes_count;
}
