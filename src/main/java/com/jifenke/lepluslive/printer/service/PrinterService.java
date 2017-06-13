package com.jifenke.lepluslive.printer.service;

import com.jifenke.lepluslive.global.util.MD5Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lss on 16-12-22.
 */
@Service
@Transactional(readOnly = false)
public class PrinterService {

    @Value("${printer.apiKey}")
    private String apiKey;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void addReceipt(String orderSid) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderSid", orderSid);
        params.put("apiKey", apiKey);
        String sign = MD5Util.MD5Encode(apiKey + orderSid,"utf-8").toUpperCase();
        params.put("sign", sign);
        addR(params);
    }


    private boolean addR(Map<String, String> params) {
        try {
            byte[] data = ("orderSid=" + params.get("orderSid") + "&sign=" + params.get("sign")).getBytes();
            URL url = new URL("http://www.lepluslife.com/manage/addReceipt");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5 * 1000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            OutputStream outStream = conn.getOutputStream();
            outStream.write(data);
            outStream.flush();
            outStream.close();
            InputStream is = conn.getInputStream();
            if (conn.getResponseCode() == 200) {
                int i = -1;
                byte[] b = new byte[1024];
                StringBuffer result = new StringBuffer();
                while ((i = is.read(b)) != -1) {
                    result.append(new String(b, 0, i));
                }
                String sub = result.toString();
                if (sub.equals("1")) {//数据已经发送到客户端
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
