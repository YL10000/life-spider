import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.net.MalformedURLException;

/**
 * ajax请求爬取
 *
 * @author yanglin
 * @version 1.0
 * @date 2020/7/15 17:19
 * @since 1.0
 */
public class SeleniumProcessor implements PageProcessor {

  // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
  private Site site = Site.me().setRetryTimes(3).setSleepTime(1000 * 60 * 2).setTimeOut(5000);

  @Override
  public void process(Page page) {
    System.out.println(page.getHtml().get());
  }

  @Override
  public Site getSite() {
    return this.site;
  }

  public static void main(String[] args) throws MalformedURLException, InterruptedException {
    String targetUrl = "http://zhsk.12348.gov.cn/qa.web/query/lb?cls=10";
    //指定浏览器驱动所在的路径
    String driverPath="D:\\software\\expore\\chromedriver_win32\\chromedriver.exe";

    //爬虫入口
    Spider spider = Spider.create(new SeleniumProcessor())
        //设置使用的下载器
        .setDownloader(new ChromeDownLoader(driverPath,1000l))
        //设置爬取的入口页面
        .addUrl(targetUrl);

    spider.thread(5).run();

    //从这个地址下载驱动：http://npm.taobao.org/mirrors/chromedriver/


    //URL remoteAddress, Capabilities desiredCapabilities
    //WebDriver webDriver = new RemoteWebDriver(new URL(""), DesiredCapabilities.chrome());

    //SeleniumDownloader seleniumDownloader = new SeleniumDownloader();
    //WebDriver webDriver = new RemoteWebDriver(new URL("http://192.168.10.108:4444/wd/hub"), DesiredCapabilities.chrome());
    /*System.setProperty("webdriver.chrome.driver", "D:\\software\\expore\\chromedriver_win32\\chromedriver.exe");
    //System.setProperty("webdriver.chrome.driver", "http://192.168.10.108:4444/wd/hub");
    //new RemoteWebDriver();
    WebDriver webDriver= new ChromeDriver();
    webDriver.get("http://zhsk.12348.gov.cn/qa.web/query/lb?cls=10");

    String pageSource = webDriver.getPageSource();
    Thread.sleep(1000);
    int size = webDriver.findElement(By.id("faqtypeall")).findElements(By.className("temp")).size();
    System.out.println(size);
    //System.out.println(pageSource);
    webDriver.close();
    webDriver.quit();*/
  }

}
