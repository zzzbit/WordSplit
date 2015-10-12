package com.xjt.nlp.word;

/**
 * <p>Title: Java中文分词组件</p>
 * <p>Description: 本组件以中科院ICTCLAS系统为基础，在其基础之上改编，本组件仅供学习和研究用途，任何商业用途将自行承担法律后果，与组件编写人无关。</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: 北京师范大学</p>
 * @author 陈天
 * @version 1.0
 */

public class ThreadTest extends Thread {
  private long sleepTime=1000;
  private String word = null;
  public ThreadTest() {

  }

  public void init(long sleepTime,String word){
    this.word = word;
    this.sleepTime = sleepTime;
  }

  public void run(){
    ICTCLAS split1 = ICTCLAS.getInstance();
    try{
      while (true) {
        System.out.println(split1.paragraphProcess(word));
        //System.out.println(split1.fileProcess("c:\\1.txt","c:\\2.txt"));
        sleep(sleepTime);
      }
    }catch(InterruptedException e){
      System.out.print("退出线程"+sleepTime);
    }
  }

  public static void main(String[] args) {
    ThreadTest test1 = new ThreadTest();
    test1.init(10,"巴拿马和美国都是国家地区，汉族是一个民族。");
    ThreadTest test2 = new ThreadTest();
    test2.init(15,"凡是由“专有名词+普通名词”(“专名+通名”)方式构成的地名全部统一处理,先切分再组合，已列出的特例除外。");
    ThreadTest test3 = new ThreadTest();
    test3.init(13,"麦克尔乔丹是篮球飞人，秦始皇和康熙还有乾隆皇帝是北京师范大学的学生，他们都痛痛快快的学习着。");
    test1.start();
    test2.start();
    test3.start();
  }
}