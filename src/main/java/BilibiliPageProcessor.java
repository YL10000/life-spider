import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 爬取github上的项目
 *
 * @author yanlin
 * @version 1.0
 * @date 2020/7/15 10:15
 * @since 1.0
 */
public class BilibiliPageProcessor implements PageProcessor {

  // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
  private Site site = Site.me().setRetryTimes(3).setSleepTime(1000 * 60 * 2).setTimeOut(5000);

  public static void main(String[] args) throws JMException {
    //String targetUrl="https://search.bilibili.com/all?keyword=java&from_source=nav_suggest_new";
    String targetUrl = "https://search.bilibili.com/all?keyword=java&from_source=nav_suggest_new";

    //爬虫入口
    Spider spider = Spider.create(new BilibiliPageProcessor())
        //定义数据处理的流程
        .addPipeline(new BilibiliPipline())
        //设置爬取的入口页面
        .addUrl(targetUrl);

    SpiderMonitor.instance().register(spider);
    spider.thread(5).run();
  }

  @Override
  public void process(Page page) {

    List<BiliBiliCourse> courses = new ArrayList<>();
    //page表示抓取到的页面，可以进行解析
    page.getHtml().css(".video-list .video-item").nodes().forEach(node -> {
      //String title=node.css(".headline a","title").get(); //使用css选择器
      String title = node.xpath("//div[@class='headline']/a/@title").get(); //使用xpath选择器
      String videoUrl = node.css(".headline a", "href").get();
      //String viewNums=node.css(".watch-num","text").get();
      String viewNums = node.xpath("//span[@class='watch-num']/text()").get();
      String autherName = node.css(".up-name", "text").get();
      //System.out.println(String.format("%s,%s,%s,%s", title,videoUrl,viewNums,autherName));
      BiliBiliCourse biliBiliCourse = new BiliBiliCourse(title, "http:" + videoUrl, viewNums, autherName);
      courses.add(biliBiliCourse);
      //将解析后的数据交个下一个pipline进行处理
      page.putField("courses", courses);
    });

    //获取当前页号
    String pageNum = page.getUrl().regex("page=(\\d+)").get();//使用正则选择器
    //获取总页数
    String lastNum = page.getHtml().css(".pages .last button", "text").get().trim();
    Integer currentPageNum = Integer.parseInt(Optional.ofNullable(pageNum).orElse("1"));
    Integer totalPageNum = Integer.parseInt(Optional.ofNullable(lastNum).orElse("0"));
    //如何当前不是最后一页，就去获取下一页数据
    if (currentPageNum < totalPageNum) {
      //构建下一页的url路径
      String nextUrl = page.getUrl().regex("(^https://.*&from_source=nav_suggest_new)", 0).get() + "&page=" + (++currentPageNum);
      //将下一页请的请求交个Downloader
      page.addTargetRequest(nextUrl);
    }
  }

  @Override
  public Site getSite() {
    return this.site;
  }

}
