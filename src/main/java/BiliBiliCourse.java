import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程model
 *
 * @author yanglin
 * @version 1.0
 * @date 2020/7/15 16:33
 * @since 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiliBiliCourse {

  private String title;
  private String videoUrl;
  private String viewNums;
  private String autherName;
}

