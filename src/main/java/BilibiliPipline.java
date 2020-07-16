import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 *
 *
 * @author yanglin
 * @version 1.0
 * @date 2020/7/15 16:29
 * @since 1.0
 */
public class BilibiliPipline implements Pipeline {

  @Override
  public void process(ResultItems resultItems, Task task) {
    //获取上一个节点穿过来的数据
    List<BiliBiliCourse> courses = resultItems.get("courses");
    System.out.println(courses);

  }
}
