package com.jifenke.lepluslive.global;

import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.WeixinPayUtil;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

/**
 * Created by wcg on 16/9/9.
 */
public class NIOServer {

  //通道管理器
  private Selector selector;

  /**
   * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
   *
   * @param port 绑定的端口号
   */
  public void initServer(int port) throws IOException {
    // 获得一个ServerSocket通道
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    // 设置通道为非阻塞
    serverChannel.configureBlocking(false);
    // 将该通道对应的ServerSocket绑定到port端口
    serverChannel.socket().bind(new InetSocketAddress(port));
    // 获得一个通道管理器
    this.selector = Selector.open();
    //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
    //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
  }

  /**
   * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
   */
  @SuppressWarnings("unchecked")
  public void listen() throws IOException {
    System.out.println("服务端启动成功！");
    // 轮询访问selector
    while (true) {
      //当注册的事件到达时，方法返回；否则,该方法会一直阻塞
      selector.select();
      // 获得selector中选中的项的迭代器，选中的项为注册的事件
      Iterator ite = this.selector.selectedKeys().iterator();
      while (ite.hasNext()) {
        SelectionKey key = (SelectionKey) ite.next();
        // 删除已选的key,以防重复处理
        ite.remove();
        // 客户端请求连接事件
        if (key.isAcceptable()) {
          ServerSocketChannel server = (ServerSocketChannel) key
              .channel();
          // 获得和客户端连接的通道
          SocketChannel channel = server.accept();
          // 设置成非阻塞
          channel.configureBlocking(false);

          //在这里可以给客户端发送信息哦
          channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));
          //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
          channel.register(this.selector, SelectionKey.OP_READ);

          // 获得了可读的事件
        } else if (key.isReadable()) {
          read(key);
        }

      }

    }
  }

  /**
   * 处理读取客户端发来的信息 的事件
   */
  public void read(SelectionKey key) throws IOException {
    // 服务器可读取消息:得到事件发生的Socket通道
    SocketChannel channel = (SocketChannel) key.channel();
    // 创建读取的缓冲区
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    channel.read(buffer);
    byte[] data = buffer.array();
    String msg = new String(data).trim();
    System.out.println("服务端收到信息：" + msg);
    ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
    channel.write(outBuffer);// 将消息回送给客户端
  }

  /**
   * 启动服务端测试
   */
  public static void main2(String[] args) throws IOException {
//    Calendar calendar = Calendar.getInstance();
//    calendar.set(Calendar.DAY_OF_MONTH, 1);
//    calendar.set(Calendar.SECOND, 0);
//    calendar.set(Calendar.MINUTE, 0);
//    calendar.set(Calendar.HOUR_OF_DAY, 0);
//    System.out.println(calendar.getTime());
  }



}
