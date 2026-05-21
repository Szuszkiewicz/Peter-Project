package com.Peter;

import com.Peter.dto.LikesInfoDto;

public interface LikesService {
    /**
     * 点赞和取消点赞
     * @param likesInfoDto
     * @return
     */
    int set(LikesInfoDto likesInfoDto);
}
