package com.mz.service;

import com.mz.domain.JobInfo;

import java.util.List;

public interface JobInfoService {
    /**
     * 保存
     * @param jobInfo
     */
    void save(JobInfo jobInfo);

    /**
     * 查询
     * @param jobInfo
     * @return
     */
    List<JobInfo> findAll(JobInfo jobInfo);
}
