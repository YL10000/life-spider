import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * TODO
 *
 * @author Administrator
 * @version 1.0
 * @date 2020/5/28 15:30
 * @since TODO
 */
public class Demo {


  public static void main(String[] args) throws Exception {
    String url = "https://t-12368hotline.aegis-info.com/apps/resource/voice/404be040-7aca-4c68-9421-69618463c294.wav";
    //HttpUtil.downloadFile(url, new File("1.wav"), 5000);
    saveFileToHttp(url,"1.wav");
  }

  static boolean saveFileToHttp(String urlPath, String fileNamePath) {

    try {

      URL url = new URL(urlPath);

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      DataInputStream in = new DataInputStream(connection.getInputStream());

      DataOutputStream out = new DataOutputStream(new FileOutputStream(fileNamePath));

      byte[] buffer = new byte[4096];

      int count = 0;

      while ((count = in.read(buffer)) > 0) {

        out.write(buffer, 0, count);

      }

      out.close();

      in.close();

      return true;

    } catch (Exception e) {

      return false;

    }

  }

}
