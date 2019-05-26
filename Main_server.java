import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import sun.misc.BASE64Decoder;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main_server {

    public static void main(String[] args) throws IOException {
        HttpServerProvider provider = HttpServerProvider.provider();
        HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(2222), 100);//监听端口19017,能同时接受100个请求
        httpserver.createContext("/", new MyResponseHandler());
        httpserver.setExecutor(null);
        httpserver.start();
        System.out.println("server started");
    }

    public static class MyResponseHandler implements HttpHandler {


        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("receive-once");
            //System.err.println(httpExchange.getRequestMethod());
            //System.err.println(httpExchange.getHttpContext());
            String requestMethod = httpExchange.getRequestMethod();
            //System.err.println(requestMethod);
            if (requestMethod.equalsIgnoreCase("GET")) {
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/html;charset=utf-8");
                System.out.println(httpExchange.getRequestURI().toString());
                //String response="hey";
                String response = Deal.handle(httpExchange);
                System.err.println("response"+response);
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream responseBody = httpExchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(responseBody, StandardCharsets.UTF_8);
                writer.write(response);
                writer.close();
                responseBody.close();
            }

        }

    }

    static class Deal {

        final private static String[] art = {"南方科技大学，简称“南科大”，",
                "创办于2011年",
                "是国家高等教育综合改革试验校、广东省高水平大学重点建设高校，",
                "由广东省领导和管理的全日制公办普通高等学校，是深圳市创办的一所创新型大学，",
                "目标是迅速建成国际化高水平研究型大学。",
                "南科大以理、工、医学学科为主，兼具部分特色人文社会学科与管理学科。"
        };

        static Map<String, Integer>[] map = new HashMap[art.length];

        static {
            for (int i = 0; i < map.length; ++i) {
                map[i] = new HashMap<>();
                char[] cvt = art[i].toCharArray();
                for (char c: cvt) {
                    String curr = String.valueOf(c);
                    if (map[i].containsKey(curr)) {
                        map[i].put(curr, map[i].get(curr) + 1);
                    } else {
                        map[i].put(curr, 1);
                    }
                }
                for (int j = 0; j < cvt.length - 1; ++j) {
                    String curr = (cvt[j] + String.valueOf(cvt[j + 1]));
                    if (map[i].containsKey(curr)) {
                        map[i].put(curr, map[i].get(curr) + 1);
                    } else {
                        map[i].put(curr, 1);
                    }
                }
            }

        }

        private static String work(String question) {
            Set<String> set = new HashSet<>();
            char[] cvt = question.toCharArray();
            for (char c: cvt) {
                set.add(String.valueOf(c));
            }
            for (int i = 0; i < cvt.length - 1; ++i) {
                set.add(cvt[i] + String.valueOf(cvt[i + 1]));
            }
            if (set.contains("时") || set.contains("创办") || set.contains("年")) {
                return "创办于2011年";
            } else if (set.contains("类型") || set.contains("类别")) {
                return "是深圳创办的一所创新型大学";
            } else if (set.contains("主要") || set.contains("为主")) {
                return "南科大以理、工、医学学科为主";
            } else if (set.contains("学科") || set.contains("科目") ) {
                return "南科大以理、工、医学学科为主，兼具部分特色人文社会学科与管理学科。";
            } else if (set.contains("目标")||set.contains("目的") || set.contains("宗旨") || set.contains("为了") || set.contains("样子")) {
                return "目标是迅速建成国际化高水平研究型大学。";
            } else if (set.contains("哪里") || set.contains("位置") || set.contains("省") || set.contains("市") || set.contains("在哪")
            || set.contains("制度") || set.contains("性质")) {
                return "由广东省领导和管理的全日制公办普通高等学校，是深圳市创办的一所创新型大学，";
            }
            int[] score = new int[map.length];
            for (String s: set) {
                for (int i = 0; i < score.length; ++i) {
                    if (map[i].containsKey(s)) {
                        score[i] += map[i].get(s);
                    }
                }
            }
            int max = 0;
            for (int i: score) {
                if (i > max) {
                    max = i;
                }
            }
            for (int i = 0; i < score.length; ++i) {
                if (max == score[i]) {
                    return art[i];
                }
            }
            return "南方科技大学，简称“南科大”，";
        }

        public static String handle(HttpExchange httpExchange) {
            URI url = httpExchange.getRequestURI();
            String decodeStr=null;
            try {
                decodeStr = URLDecoder.decode(url.toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.err.println(decodeStr);
            String u = decodeStr;
            int pos = 0;
            assert u != null;
            while (pos < u.length() && u.charAt(pos) != 'q') {
                ++pos;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = pos + 1; i < u.length(); ++i){
                sb.append(u.charAt(i));
            }
            System.err.println(u);
            u = sb.toString();
            return work(u);
        }
    }

}

