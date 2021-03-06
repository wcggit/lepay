package com.jifenke.lepluslive.global.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import sun.nio.ch.IOUtil;

import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;

/**
 * Created by wcg on 16/5/13.
 */
@Configuration
public class BaseConfig {

  @Inject
  private ResourceLoader resourceLoader;


  @Bean
  CurrentTimeDateTimeService currentTimeDateTimeService() {
    return new CurrentTimeDateTimeService();
  }

  @Bean
  public SSLContext getTxSSLContext() {
    KeyStore keystore = null;
    SSLContext sslcontext = null;
    try {
      keystore = KeyStore.getInstance("PKCS12");
      keystore.load(resourceLoader.getResource("classpath:apiclient_cert.p12").getInputStream(),
                    Constants.LE_PAY_MCH_ID.toCharArray());
      sslcontext = SSLContexts.custom()
          .loadKeyMaterial(keystore, "1363466902".toCharArray())
          .build();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sslcontext;
  }

  @Bean(name = "private_ali")
  public String getAliPrivate() throws IOException {
    return IOUtils.toString(resourceLoader.getResource("classpath:private.txt").getInputStream());

  }

  @Bean(name = "public_ali")
  public String getAliPublic() throws IOException {
   return IOUtils.toString(resourceLoader.getResource("classpath:public.txt").getInputStream());
  }


}
