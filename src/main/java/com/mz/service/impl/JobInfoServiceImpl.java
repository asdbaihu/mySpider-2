package com.mz.service.impl;

import com.mz.dao.JobInfoDao;
import com.mz.domain.JobInfo;
import com.mz.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class JobInfoServiceImpl implements JobInfoService {
    @Autowired
    private JobInfoDao jobInfoDao;
    /**
     * 新增一个职位信息；
     * 判断职位信息是否存在。如果不存在则新增
     * @param jobInfo
     */
    @Override
    public void save(JobInfo jobInfo) {
        JobInfo jobDo = new JobInfo(jobInfo.getUrl(), jobInfo.getTime());
        List<JobInfo> jobInfoDos = this.findAll(jobDo);
        if (jobInfoDos.size() == 0) {
            this.jobInfoDao.save(jobInfo);
        }
    }
    /**
     * 查询所有
     * @param jobInfo
     * @return
     */
    @Override
    public List<JobInfo> findAll(JobInfo jobInfo) {
        Example<JobInfo> example = Example.of(jobInfo);
        return this.jobInfoDao.findAll(example);
    }
}
