package com.Peter;

import com.Peter.dto.DeleteActivityInfoDto;
import com.Peter.dto.PostActivityInfoDto;

public interface ActivityService {
    /**
     * 新增活动或修改活动
     * @param postActivityInfoDto
     * @ return
     */
    int PostActivity(PostActivityInfoDto postActivityInfoDto);
    /**
     * 删除活动
     * @param deleteActivityInfoDto
     * @return
     */
    int DeleteActivity(DeleteActivityInfoDto deleteActivityInfoDto);
}
