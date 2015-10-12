package com.xjt.nlp.word;

/**
 * <p>Title: Java中文分词组件</p>
 * <p>Description: 本组件以中科院ICTCLAS系统为基础，在其基础之上改编，
 * 本组件仅供学习和研究用途，任何商业用途将自行承担法律后果，与组件编写人无关。</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: 北京师范大学</p>
 * @author 陈天
 * @version 1.0
 */

public class ICTCLAS {
  private static ICTCLAS instance=null;
  private ICTCLAS() {
    init(0,2);
  }

  public synchronized static ICTCLAS getInstance(){
    if (instance==null){
      instance = new ICTCLAS();
    }
    return instance;
  }

  public synchronized native boolean init(int i, int j);
  public synchronized native String paragraphProcess(String sParagraph);
  public synchronized native boolean fileProcess(String source,String target);
  public static void main(String[] args) {
    ICTCLAS split1 = new ICTCLAS();
    for (int i=0;i<=100;i++)
      System.out.println(split1.paragraphProcess("巴拿马和美国都是国家地区，汉族是一个民族。/"));
      System.out.println(split1.fileProcess("1.txt","2.txt"));
  }

  static{
    System.loadLibrary("ICTCLAS");
  }
}