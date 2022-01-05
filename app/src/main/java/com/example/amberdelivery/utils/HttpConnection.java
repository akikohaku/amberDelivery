package com.example.amberdelivery.utils;

import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class HttpConnection {

    /**
     * 升级版接口
     *
     * @param url
     * @param method
     * @param successCallback
     * @param failCallback
     * @param map
     */
    public HttpConnection(final String url, final HttpMethod method,
                          final SuccessCallback successCallback,
                          final FailCallback failCallback, final Map<String, String> map) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                StringBuffer sb = new StringBuffer();
                String realString = "";
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    sb.append(entry.getKey()).append("=")
                            .append(entry.getValue()).append("&");
                }
                realString = sb.toString().substring(0,
                        sb.toString().length() - 1);

                try {
                    URLConnection uc;
                    switch (method) {
                        case POST:
                            uc = new URL(url).openConnection();
                            uc.setDoOutput(true);
                            uc.setConnectTimeout(5000);
                            BufferedWriter bw = new BufferedWriter(
                                    new OutputStreamWriter(uc.getOutputStream(),
                                            "utf-8"));
                            bw.write(realString);
                            bw.flush();
                            break;

                        default:
                            uc = new URL(url + "?" + realString).openConnection();

                            uc.setConnectTimeout(5000);
                            uc.setRequestProperty("apikey",
                                    "2f50fafc573e93a725e277b073d9c5dd");
                            break;
                    }
                    System.out.println("Request url:" + uc.getURL());
                    System.out.println("Request date:" + realString);
                    System.out.println("Result status:"
                            + ((HttpURLConnection) uc).getResponseCode());
                    if (((HttpURLConnection) uc).getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(uc.getInputStream(),
                                        "utf-8"));
                        StringBuffer result = new StringBuffer();
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        System.out.println("Result:" + result);
                        return result.toString();
                    } else {
                        System.out.println("Result:"
                                + ((HttpURLConnection) uc).getResponseCode());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (successCallback != null) {
                            successCallback.onSuccess(result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }
                super.onPostExecute(result);

            }
        }.execute();
    }

    /**
     * json型接口升级版
     *
     * @param url
     * @param successCallback
     * @param failCallback
     * @param json map
     */
    public HttpConnection(final String url,
                          final SuccessCallback successCallback,
                          final FailCallback failCallback, final String json) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    HttpURLConnection uc = (HttpURLConnection) new URL(url)
                            .openConnection();
                    //打开输出流
                    uc.setDoOutput(true);
                    //打开输入流
                    uc.setDoInput(true);
                    uc.setInstanceFollowRedirects(true);
                    //post方式
                    uc.setRequestMethod("POST");
                    //超时
                    uc.setConnectTimeout(5000);
                    //禁止使用缓存
                    uc.setUseCaches(false);
                    //接收字符串类型数据用json
                    uc.setRequestProperty("Accept", "application/json");
                    //请求的内容为表单类型数据
                    uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //设置utf8
                    uc.setRequestProperty("Charset", "UTF-8");

//                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(uc.getOutputStream(),"utf-8"));
//                    bw.write(json);
//                    bw.flush();
                    /**
                     * 请求数据（表单类型的或json类型的数据）
                     */
                    OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream());
                    //发送参数
                    writer.write(json);
                    //清理当前编辑器的左右缓冲区，并使缓冲区数据写入基础流
                    writer.flush();



                    System.out.println("Request url:" + uc.getURL());
                    System.out.println("Request date:" + json);

                    //判断接收数据，为json格式
                    if (uc.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(uc.getInputStream(),
                                        "utf-8"));
                        StringBuffer result = new StringBuffer();
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }
                        System.out.println("Result:" + result);
                        return result.toString();
                    } else {
                        System.out.println("uc.getResponseCode()=="
                                + uc.getResponseCode());
                        System.out.println("uc.getResponseMessage()=="
                                + uc.getResponseMessage());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    if (successCallback != null) {
                        successCallback.onSuccess(result);
                    }
                } else {
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }
                super.onPostExecute(result);

            }
        }.execute();
    }



    public static interface SuccessCallback {
        void onSuccess(String result);
    }

    public static interface FailCallback {
        void onFail();
    }

}

enum HttpMethod {
    POST,GET
}
