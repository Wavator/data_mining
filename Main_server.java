import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class FinalServer {
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
            //System.err.println(httpExchange.getRequestURI());
//            System.err.println(httpExchange.getHttpContext());
            String requestMethod = httpExchange.getRequestMethod();
            //System.err.println(requestMethod);
            if (requestMethod.equalsIgnoreCase("GET")) {
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/html;charset=utf-8");
                //System.out.println(httpExchange.getRequestURI().toString());
                //String response="hey";
                String response = null;
                try {
                    //System.out.println("ininininini");
                    response = new Gao().gao(httpExchange);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.err.println("response"+response);
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream responseBody = httpExchange.getResponseBody();
                OutputStreamWriter writer = new OutputStreamWriter(responseBody, StandardCharsets.UTF_8);
                writer.write(response);
                writer.close();
                responseBody.close();
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                String tmp = null;
                while ((tmp=br.readLine())!= null) {
                    System.out.println(tmp);
                }
            }

        }

    }

    static class HttpGet {
        protected static final int SOCKET_TIMEOUT = 10000; // 10S
        protected static final String GET = "GET";

        public static String get(String host, Map<String, String> params) {
            try {
                // 设置SSLContext
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

                String sendUrl = getUrlWithQueryString(host, params);

                // System.out.println("URL:" + sendUrl);

                URL uri = new URL(sendUrl); // 创建URL对象
                HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
                if (conn instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
                }

                conn.setConnectTimeout(SOCKET_TIMEOUT); // 设置相应超时
                conn.setRequestMethod(GET);
                int statusCode = conn.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    System.out.println("Http错误码：" + statusCode);
                }

                // 读取服务器的数据
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }

                String text = builder.toString();

                close(br); // 关闭数据流
                close(is); // 关闭数据流
                conn.disconnect(); // 断开连接

                return text;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static String getUrlWithQueryString(String url, Map<String, String> params) {
            if (params == null) {
                return url;
            }

            StringBuilder builder = new StringBuilder(url);
            if (url.contains("?")) {
                builder.append("&");
            } else {
                builder.append("?");
            }

            int i = 0;
            String[] keys = {"q", "from","to","appid","salt","sign"};
            for (String key : keys) {
                String value = params.get(key);
                if (value == null) { // 过滤空的key
                    continue;
                }

                if (i != 0) {
                    builder.append('&');
                }

                builder.append(key);
                builder.append('=');
                builder.append(encode(value));

                i++;
            }
            String key;
            key = "q";
            return builder.toString();
        }

        protected static void close(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 对输入的字符串进行URL编码, 即转换为%20这种形式
         *
         * @param input 原文
         * @return URL编码. 如果编码失败, 则返回原文
         */
        public static String encode(String input) {
            if (input == null) {
                return "";
            }

            try {
                return URLEncoder.encode(input, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return input;
        }

        private static TrustManager myX509TrustManager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        };

    }



    private static class TransApi {
        private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

        private String appid;
        private String securityKey;

        public TransApi(String appid, String securityKey) {
            int res = 0;
            for (int j = 0; j < 10; ++j) {
                for (int i = 0; i < 2 * 100000000; ++i) {
                    res += i * i;
                }
            }
            System.out.println(res);
            this.appid = appid;
            this.securityKey = securityKey;
        }

        public String getTransResult(String query, String from, String to) {
            Map<String, String> params = buildParams(query, from, to);
            return HttpGet.get(TRANS_API_HOST, params);
        }

        private Map<String, String> buildParams(String query, String from, String to) {
            Map<String, String> params = new TreeMap<String, String>();
            params.put("q", query);
            params.put("from", from);
            params.put("to", to);

            params.put("appid", appid);

            // 随机数
            String salt = String.valueOf(System.currentTimeMillis());
            params.put("salt", salt);

            // 签名
            String src = appid + query + salt + securityKey; // 加密前的原文
            params.put("sign", MD5.md5(src));

            return params;
        }

    }


    static class MD5 {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };

        /**
         * 获得一个字符串的MD5值
         *
         * @param input 输入的字符串
         * @return 输入字符串的MD5值
         *
         */
        public static String md5(String input) {
            if (input == null)
                return null;

            try {
                // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                // 输入的字符串转换成字节数组
                byte[] inputByteArray = new byte[0];
                try {
                    inputByteArray = input.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // inputByteArray是输入字符串转换得到的字节数组
                messageDigest.update(inputByteArray);
                // 转换并返回结果，也是字节数组，包含16个元素
                byte[] resultByteArray = messageDigest.digest();
                // 字符数组转换成字符串返回
                return byteArrayToHex(resultByteArray);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }

        /**
         * 获取文件的MD5值
         *
         * @param file
         * @return
         */
        public static String md5(File file) {
            try {
                if (!file.isFile()) {
                    System.err.println("文件" + file.getAbsolutePath() + "不存在或者不是文件");
                    return null;
                }

                FileInputStream in = new FileInputStream(file);

                String result = md5(in);

                in.close();

                return result;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static String md5(InputStream in) {

            try {
                MessageDigest messagedigest = MessageDigest.getInstance("MD5");

                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = in.read(buffer)) != -1) {
                    messagedigest.update(buffer, 0, read);
                }

                in.close();

                String result = byteArrayToHex(messagedigest.digest());

                return result;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private static String byteArrayToHex(byte[] byteArray) {
            // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
            char[] resultCharArray = new char[byteArray.length * 2];
            // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
            int index = 0;
            for (byte b : byteArray) {
                resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
                resultCharArray[index++] = hexDigits[b & 0xf];
            }

            // 字符数组组合成字符串返回
            return new String(resultCharArray);

        }


    }


    private static class Gao {

        private String trans(String text, String from, String to) {//"zh","en","auto"
            String APP_ID = "20190531000303593";
            String SECURITY_KEY = "xHaR2_tgTYYvjZuF97oh";
            TransApi api = new TransApi(APP_ID, SECURITY_KEY);
            return api.getTransResult(text, from, to);
        }

        String p, q;

        private String getAns(String p, String q) {

            return null;
        }


        public String gao(HttpExchange httpExchange) throws Exception {
            String url = null;
            try {
                url = URLDecoder.decode(httpExchange.getRequestURI().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.err.println(url);
            assert url != null;
            p = "";
            q = "";
            int i;
            for (i = 0; i < url.length() && url.charAt(i) != 'p'; ++i);
            i += 2;
            for (; !(url.charAt(i) == '&' && url.charAt(i + 1) == 'q'); ++i) {
                p += url.charAt(i);
            }
            i += 3;
            for (; i < url.length(); ++i) {
                q += url.charAt(i);
            }
            System.err.println("p=" + p);
            System.err.println("q=" + q);
            String tp = trans(p, "zh", "en");
            String tq = trans(q, "zh", "en");
            System.err.println(tp);
            System.err.println(tq);
            String res = getAns(tp, tq);
            System.err.println(res);
            res = trans(res, "en", "zh");
            System.err.println(res);
            return res;
        }

    }

}
