import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.util.List;

/**
 * 爬虫
 *
 * @author yanglin
 * @version 1.0
 * @date 2020/4/22 9:04
 * @since 1.0
 */
public class AegisRepoPageProcessor implements PageProcessor {

  //期望的总条数
  private Long expectTotalNum;
  //保存的目录
  private String saveDir;
  private Site site = Site.me()/*.setRetryTimes(3).setSleepTime(100)*/.setTimeOut(300);

  public AegisRepoPageProcessor(String saveDir, Long expectTotalNum) {
    this.saveDir = saveDir;
    this.expectTotalNum = expectTotalNum;
  }

  public static void main(String[] args) {
    if (ArrayUtil.isEmpty(args) || args.length < 3) {
      throw new RuntimeException("参数错误，需要提供爬取的目录url,存储的目标目录，期望爬取的数量");
    }
    //args的需要三个参数，[0]:爬取的目标url；[1]:存储的目标目录；[2]:要爬取的数量
    String targetUrl = args[0];
    String saveDir = args[1];
    Long expectTotalNum = Convert.toLong(args[2], 0l);
    Spider.create(new AegisRepoPageProcessor(saveDir, expectTotalNum))
        .addUrl(targetUrl)
        .thread(5).run();
  }

  @Override
  public void process(Page page) {
    //分页查询的url
    String pageURL = page.getHtml().css("#qcQueryForm").$("form", "action").get();

    //当前页
    Long currentPageNum = Convert.toLong(page.getHtml().css("input[name='pageNum']").$("input", "value").get(), 0l);

    //总页数
    Long totalPageNum = Convert.toLong(page.getHtml().css(".navbar .toolbar td:eq(2) nobr").$("nobr", "text").regex("\\d{1,}").get(), 0l);

    //获取每页显示的条数
    Long pageSize = Convert.toLong(page.getHtml().css("input[name='rowsDisplay']").$("input", "value").get(), 1l);

    //期望的总页数
    double expectPageNum = Math.ceil(expectTotalNum / pageSize);

    //获取当前页的所有音频链接zx
    List<String> urls = page.getHtml().css("#ec_table tr td:eq(1) a:eq(2)").links().regex(".*V3").all();
    page.putField("currentPageNum", currentPageNum);
    page.putField("totalPageNum", totalPageNum);
    page.putField("data", urls);
    if (currentPageNum < totalPageNum && currentPageNum < expectPageNum) {
      //抓取下一页数据
      page.addTargetRequest(pageURL + "&pageNum=" + (currentPageNum + 1));
    }


    //根据链接下载音频文件到指定的目录中
    urls.forEach((url) -> {
      System.out.println(url);
      //获取文件名
      String fileName = StrUtil.subAfter(url, "/", true);
      //将音频保存到指定的文件夹中
      ThreadUtil.execute(() -> {
        HttpUtil.downloadFile(url, new File(this.saveDir + "\\" + fileName), 5000);

      });
    });
  }

  @Override
  public Site getSite() {
    return site;
  }

}
