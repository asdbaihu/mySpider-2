package com.mz.job;

import com.mz.domain.JobInfo;
import com.mz.service.impl.SpringDataPipeline;
import com.mz.utils.MathSalaryUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * 步骤：
 * 1、创建定时器定时抓取代码。
 * 2、创建爬虫，初始化爬虫配置：种子页面，请求配置
 * 3、编写爬取页面逻辑(判断是不是列表页面)
 * 获取本页面所有职位详情地址，加入队列
 * 获取下一页所有职位详情地址，加入队列
 * 4、持久化爬取数据。自定义一个数据库的Pipeline。
 */
@Component
public class JobProcessor implements PageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JobProcessor.class);

    /**
     * 自定义Pipeline
     */
    @Autowired
    private SpringDataPipeline springDataPipeline;

    /**
     * 种子页面
     */
    @Value("${spider.url}")
    private String url;


    /**
     * 1、创建定时器定时抓取代码。
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 2000)
    public void taskTime() {
        logger.info("url===========>>" + url);
        //2、创建爬虫，初始化爬虫配置：种子页面，请求配置
        Spider spider = Spider.create(new JobProcessor());
        spider.addPipeline(new ConsolePipeline());
        spider.addPipeline(new FilePipeline("D://crawler//"));
        spider.addPipeline(this.springDataPipeline);
        spider.thread(10);
        spider.addUrl(url);
        spider.setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)));
        spider.run();
    }

    @Override
    public void process(Page page) {
        //3、编写爬取页面逻辑(判断是不是列表页面)。怎么判断是不是页面刚才已经讲过了。
        List<Selectable> nodes = page.getHtml().css("div#resultList div.el").nodes();
        //是详情页
        if (nodes.size() == 0) {
            this.saveJobInfo(page);
        } else {//列表页
            //获取本页面所有职位详情地址
            for (Selectable node : nodes) {
                String jobDetailUrl = node.css("p span a").links().get();
                page.addTargetRequest(jobDetailUrl);
            }
            //获取下一页所有职位详情地址
            String nextPageUrl = page.getHtml().css("div.p_in ul li.bk").nodes().get(1).links().get();
            page.addTargetRequest(nextPageUrl);
        }
    }

    /**
     * 持久化职位信息
     *
     * @param page
     */
    private void saveJobInfo(Page page) {
        Html html = page.getHtml();
        //4、持久化爬取数据。自定义一个数据库的Pipeline。
        JobInfo jobInfo = new JobInfo();
        //公司名称
        String companyName = html.css("p.cname a", "title").get();
        jobInfo.setCompanyName(companyName);
        //公司地址
        String companyAddr = html.css("div.cn p.msg", "title").get();
        if (StringUtils.isNotBlank(companyAddr)) {
            companyAddr = companyAddr.substring(0, companyAddr.indexOf("|"));
        }
        jobInfo.setCompanyAddr(companyAddr);
        //公司信息 body > div.tCompanyPage > div.tCompany_center.clearfix > div.tCompany_main > div:nth-child(3) > div
        String companyInfo = html.css("div.tCompany_main div.tBorderTop_box div.tmsg").get();
        jobInfo.setCompanyInfo(companyInfo);
        //职位名称
        String jobName = html.css("div.cn h1", "title").get();
        jobInfo.setJobName(jobName);
        //工作地点
        String jobAddr = html.css("div.tBorderTop_box  > div.bmsg.inbox > p.fp").get();
        if (StringUtils.isNotBlank(jobAddr)) {
            jobAddr = Jsoup.parse(jobAddr).text();
        }
        jobInfo.setJobAddr(jobAddr);
        //职位信息
        String jobDetail = html.css("div.tBorderTop_box > div.bmsg.job_msg").get();
        jobInfo.setJobInfo(jobDetail);
        //薪资
        String salaryStr = html.css("div.cn > strong", "text").get();
        if (StringUtils.isNotBlank(salaryStr)) {
            Integer[] salary = MathSalaryUtil.getSalary(salaryStr);
            //薪资范围，最小
            jobInfo.setSalaryMin(salary[0]);
            //薪资范围，最大
            jobInfo.setSalaryMax(salary[1]);
        }
        jobInfo.setUrl(page.getUrl().toString());
        //职位最近发布时间
        String time = html.css("div.cn > p.msg", "title").get();
        if (StringUtils.isNotBlank(time)) {
            time = time.substring(time.indexOf("发布") - 5, time.indexOf("发布"));
            jobInfo.setTime(time);
        }
        page.putField("jobInfo", jobInfo);
    }

    //请求配置
    private Site site = Site.me()
            .setRetryTimes(3)
            .setRetrySleepTime(3000)
            .setTimeOut(5000);

    @Override
    public Site getSite() {
        return site;
    }
}
