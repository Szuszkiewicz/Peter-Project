package com.Peter;

import com.Peter.dto.CollectsInfoDto;

public interface CollectsService {
    /**
     * 收藏和取消收藏
     * @param collectsInfoDto
     * @return
     */
    int set(CollectsInfoDto collectsInfoDto);
}
