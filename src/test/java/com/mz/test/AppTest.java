package com.mz.test;

import com.mz.dao.JobInfoDao;
import com.mz.domain.JobInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Autowired
    private JobInfoDao jobInfoDao;

    @Test
    public void contextLoads() {

    }

}
