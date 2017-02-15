package com.jifenke.lepluslive.printer.service;

import com.jifenke.lepluslive.merchant.repository.MerchantRepository;
import com.jifenke.lepluslive.order.repository.OffLineOrderRepository;
import com.jifenke.lepluslive.printer.domain.MD5;
import com.jifenke.lepluslive.printer.domain.entities.MeasurementUrl;
import com.jifenke.lepluslive.printer.repository.MeasurementRepository;
import com.jifenke.lepluslive.printer.repository.PrinterRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
    @Inject
    private PrinterRepository printerRepository;

    @Inject
    private MerchantRepository merchantRepository;

    @Inject
    private OffLineOrderRepository offLineOrderRepository;

    @Inject
    private MeasurementRepository measurementRepository;

    @Value("${printer.apiKey}")
    private String apiKey;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void addReceipt(String orderSid) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderSid", orderSid);
        params.put("apiKey", apiKey);
        String sign = MD5.MD5Encode(apiKey + orderSid).toUpperCase();
        params.put("sign", sign);
        addR(params);
    }


    public boolean addR(Map<String, String> params) {
        try {
            MeasurementUrl measurementUrl=measurementRepository.findUrlByName("printerUrl");
            byte[] data = ("orderSid=" + params.get("orderSid") + "&sign=" + params.get("sign")).getBytes();
            URL url = new URL(measurementUrl.getUrl());
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
