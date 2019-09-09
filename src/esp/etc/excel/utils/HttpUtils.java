package esp.etc.excel.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static String post(String requestUrl, String requestMethod, String requestBody) {
        
        String readLine = "";
        StringBuffer outResult = new StringBuffer();
        
        try {
            URL url = new URL(requestUrl);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "applicaton/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes("UTF-8"));
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ( (readLine = br.readLine()) != null ) {
                outResult.append(readLine);
            }
            
            conn.disconnect();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return outResult.toString();
    }
}
