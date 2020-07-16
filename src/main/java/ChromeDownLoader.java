import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 自定义下载器
 *
 * @author yanlin
 * @version 1.0
 * @date 2020/7/16 9:06
 * @since 1.0
 */

public class ChromeDownLoader implements Downloader, Closeable {

  //等待时长，让页面渲染完成
  private Long waitTime;

  //驱动池，用来提高效率
  private int poolSize=1;

  private BlockingDeque<WebDriver> driverPool=new LinkedBlockingDeque<>();
  private List<WebDriver> webDrivers =new ArrayList<>();

  //初始化驱动池
  private void initDriverPool(){
    if (driverPool.size()<poolSize){
      WebDriver webDriver=new ChromeDriver();
      driverPool.add(webDriver);
      webDrivers.add(webDriver);
    }

  }

  public ChromeDownLoader(String chromeDriverPath,Long waitTime) {
    //指定驱动所在的路径
    System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    this.waitTime=waitTime;
  }

  @Override
  public void close() throws IOException {
    Iterator<WebDriver> iterator = this.webDrivers.iterator();
    while (iterator.hasNext()){
      iterator.next().quit();
    }
  }

  @SneakyThrows
  @Override
  public Page download(Request request, Task task) {
    WebDriver webDriver= this.driverPool.take();
    webDriver.get(request.getUrl());
    Thread.sleep(waitTime);
    WebDriver.Options manage = webDriver.manage();
    Site site = task.getSite();
    if (site.getCookies() != null) {
      Iterator var6 = site.getCookies().entrySet().iterator();

      while(var6.hasNext()) {
        Map.Entry<String, String> cookieEntry = (Map.Entry)var6.next();
        Cookie cookie = new Cookie((String)cookieEntry.getKey(), (String)cookieEntry.getValue());
        manage.addCookie(cookie);
      }
    }

    WebElement webElement = webDriver.findElement(By.xpath("/html"));
    String content = webElement.getAttribute("outerHTML");
    Page page = new Page();
    page.setRawText(content);
    page.setHtml(new Html(content, request.getUrl()));
    page.setUrl(new PlainText(request.getUrl()));
    page.setRequest(request);
    this.driverPool.add(webDriver);
    return page;
  }

  @Override
  public void setThread(int poolSize) {
    this.poolSize=poolSize;
    initDriverPool();
  }
}

