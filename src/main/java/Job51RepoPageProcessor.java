import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;

/**
 * 爬取51job
 *
 * @author yanglin
 * @version 1.0
 * @date 2020/4/23 14:01
 * @since 1.0
 */

public class Job51RepoPageProcessor implements PageProcessor {

  private Site site = Site.me()/*.setRetryTimes(3).setSleepTime(100)*/.setTimeOut(3000);

  public static void main(String[] args) throws JMException {

    String targetUrl = "https://search.51job.com/list/010000,000000,0000,00,9,99,java,2,1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=";
    Spider jobSpider = Spider.create(new Job51RepoPageProcessor())
        //.setScheduler(new RedisScheduler(""))
        .addUrl(targetUrl);
    //添加監控
    //SpiderMonitor.instance().register(jobSpider);
    jobSpider.thread(5).run();

  }

  @Override
  public void process(Page page) {
    String currentPage = page.getHtml().css(".dw_page li.on", "text").get();
    System.out.println(currentPage);
    page.putField("currentPage", currentPage);
    page.getHtml().css("#resultList .el").nodes().forEach((node) -> {

      if (null != node) {
        String jobName = node.css(".t1 span a", "text").get();
        if (null != jobName) {
          //获取job详情页面的链接
          String jobInfoUrl = node.css(".t1 span a", "href").get();
          String address = node.css(".t2 a", "text").get();
          page.putField(jobName + address, jobInfoUrl);

          //将详情页面保存到指定的目录中
          /*ThreadUtil.execute(() -> {
            HttpUtil.downloadFile(jobInfoUrl, "C:\\Users\\EDZ\\Desktop\\download\\" + address + ".html");
          });*/

        }
      }
    });

    //获取到下一页的链接
    String nextUrl = page.getHtml().css(".dw_page li.bk").nodes().get(1).$("a", "href").get();
    //page.addTargetRequest(nextUrl);
  }

  @Override
  public Site getSite() {
    return site;
  }

}

