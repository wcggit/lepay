package com.jifenke.lepluslive.global.util;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * 主要用于富友支付
 */
public class MapUtil {

  /**
   * Map key 排序
   */
  public static Map<String, String> order(Map<String, String> map) {
    HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
    List<Map.Entry<String, String>>
        infoIds =
        new ArrayList<Map.Entry<String, String>>(map.entrySet());

    Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
      public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
        return (o1.getKey()).toString().compareTo(o2.getKey());
      }
    });

    for (int i = 0; i < infoIds.size(); i++) {
      Map.Entry<String, String> item = infoIds.get(i);
      tempMap.put(item.getKey(), item.getValue());
    }
    return tempMap;
  }

  /**
   * 转换对象为map
   */
  public static Map<String, String> objectToMap(Object object, String... ignore) {
    Map<String, String> tempMap = new LinkedHashMap<String, String>();
    for (Field f : object.getClass().getDeclaredFields()) {
      if (!f.isAccessible()) {
        f.setAccessible(true);
      }
      boolean ig = false;
      if (ignore != null && ignore.length > 0) {
        for (String i : ignore) {
          if (i.equals(f.getName())) {
            ig = true;
            break;
          }
        }
      }
      if (ig) {
        continue;
      } else {
        Object o = null;
        try {
          o = f.get(object);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        tempMap.put(f.getName(), o == null ? "" : o.toString());
      }
    }
    return tempMap;
  }

  /**
   * url 参数串连
   */
  public static String mapJoin(Map<String, String> map, boolean keyLower, boolean valueUrlencode) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String key : map.keySet()) {
      if (map.get(key) != null && !"".equals(map.get(key))) {
        try {
          String
              temp =
              (key.endsWith("_") && key.length() > 1) ? key.substring(0, key.length() - 1) : key;
          stringBuilder.append(keyLower ? temp.toLowerCase() : temp).append("=")
              .append(valueUrlencode ? URLEncoder.encode(map.get(key), "utf-8").replace("+", "%20")
                                     : map.get(key)).append("&");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
    }
    if (stringBuilder.length() > 0) {
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }
    return stringBuilder.toString();
  }

  /**
   * url 参数串连
   */
  public static String mapBlankJoin(Map<String, Object> map, boolean keyLower,
                                    boolean valueUrlencode) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String key : map.keySet()) {
      if (map.get(key) != null) {
        try {
          String
              temp =
              (key.endsWith("_") && key.length() > 1) ? key.substring(0, key.length() - 1) : key;
          stringBuilder.append(keyLower ? temp.toLowerCase() : temp).append(
              "=")
              .append(valueUrlencode ? URLEncoder
                  .encode("" + map.get(key), "utf-8")
                  .replace("+", "%20") : map
                          .get(key)).append("&");
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
    }
    if (stringBuilder.length() > 0) {
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }
    return stringBuilder.toString();
  }


  public static Map<String, String> parseXml(HttpServletRequest request) {
    Map<String, String> map = new HashMap<String, String>();
    InputStream inputStream = null;
    try {
      inputStream = request.getInputStream();
      XMLInputFactory factory = XMLInputFactory.newInstance();
      XMLEventReader reader = factory.createXMLEventReader(inputStream);
      while (reader.hasNext()) {
        XMLEvent event = reader.nextEvent();
        if (event.isStartElement()) {
          String tagName = event.asStartElement().getName().toString();
          if ("xml".equals(tagName)) {

          } else {
            map.put(tagName, reader.getElementText());
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return map;
  }

  public static String checkNull(HashMap<String, String> resultMap) {
    Set<String> keySet = resultMap.keySet();
    for (String key : keySet) {
      String value = resultMap.get(key);
      if (StringUtils.isBlank(value)) {
        return key;
      } else {
        resultMap.put(key, StringUtils.trim(value));
      }
    }
    return null;
  }

  public static Map<String, String> parseFormData(String body) {
    Map<String, String> map = new HashMap<String, String>();
    if (body != null) {
      String[] contents = body.split("&");
      for (String content : contents) {
        if (content.contains("=")) {
          String[] temp = content.split("=");
          System.out.println(temp.length + "------" + content);
          if (temp.length == 2 && StringUtils.isNoneBlank(temp[0]) && StringUtils
              .isNoneBlank(temp[1])) {
            map.put(temp[0], temp[1]);
          }
        }
      }
    }
    return map;
  }


  public static void mapToXML(Map<String, Object> map, StringBuffer sb) {
    Set<String> set = map.keySet();
    for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
      String key = it.next();
      Object value = map.get(key);
      if (null == value) {
        value = "";
      }
      sb.append("<" + key + ">" + value + "</" + key + ">");
    }
  }

  public static Map<String, Object> Dom2Map(String xmlContent) throws DocumentException {
    Document doc = DocumentHelper.parseText(xmlContent);
    Map<String, Object> map = new HashMap<String, Object>();
    if (doc == null) {
      return map;
    }
    Element root = doc.getRootElement();
    for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext(); ) {
      Element e = (Element) iterator.next();
      //System.out.println(e.getName());
      List<Element> list = e.elements();
      if (list.size() > 0) {
        map.put(e.getName(), Dom2Map(e));
      } else {
        map.put(e.getName(), e.getText());
      }
    }
    return map;
  }


  public static Map<String, String> xmlStringToMap(String xmlContent) throws DocumentException {
    Document doc = DocumentHelper.parseText(xmlContent);
    Map<String, String> map = new HashMap<String, String>();
    if (doc == null) {
      return map;
    }
    Element root = doc.getRootElement();
    for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext(); ) {
      Element e = (Element) iterator.next();
      map.put(e.getName(), e.getText());
    }
    return map;
  }


  public static Map<String, Object> Dom2Map(Element e) {
    Map<String, Object> map = new HashMap<String, Object>();
    List<Element> list = e.elements();
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        Element iter = (Element) list.get(i);
        List<Object> mapList = new ArrayList<Object>();

        if (iter.elements().size() > 0) {
          Map<String, Object> m = Dom2Map(iter);
          if (map.get(iter.getName()) != null) {
            Object obj = map.get(iter.getName());
            if (!obj.getClass().getName().equals("java.util.ArrayList")) {
              mapList = new ArrayList<Object>();
              mapList.add(obj);
              mapList.add(m);
            }
            if (obj.getClass().getName().equals("java.util.ArrayList")) {
              mapList = (List) obj;
              mapList.add(m);
            }
            map.put(iter.getName(), mapList);
          } else {
            map.put(iter.getName(), m);
          }
        } else {
          if (map.get(iter.getName()) != null) {
            Object obj = map.get(iter.getName());
            if (!obj.getClass().getName().equals("java.util.ArrayList")) {
              mapList = new ArrayList<Object>();
              mapList.add(obj);
              mapList.add(iter.getText());
            }
            if (obj.getClass().getName().equals("java.util.ArrayList")) {
              mapList = (List) obj;
              mapList.add(iter.getText());
            }
            map.put(iter.getName(), mapList);
          } else {
            map.put(iter.getName(), iter.getText());
          }
        }
      }
    } else {
      map.put(e.getName(), e.getText());
    }
    return map;
  }

}
