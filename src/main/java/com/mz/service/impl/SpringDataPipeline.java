package com.mz.service.impl;

import com.mz.domain.JobInfo;
import com.mz.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 自定义一个Pipeline，持久化数据到数据库
 */
@Component
public class SpringDataPipeline implements Pipeline {
    //注入我们的JobInfoService
    @Autowired
    private JobInfoService jobInfoService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        JobInfo jobInfo = resultItems.get("jobInfo");
        if (jobInfo != null) {
            this.jobInfoService.save(jobInfo);
        }
    }
}
