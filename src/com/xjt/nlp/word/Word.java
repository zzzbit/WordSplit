package com.xjt.nlp.word;

/**
 * <p>Title: Java中文分词组件</p>
 * <p>Description: 本组件以中科院ICTCLAS系统为基础，在其基础之上改编，本组件仅供学习和研究用途，任何商业用途将自行承担法律后果，与组件编写人无关。</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: 北京师范大学</p>
 * @author 陈天
 * @version 1.0
 */

public class Word {
  private String word;
  private String attribute;
  private int num = 0;
  public Word() {
  }

  public Word(String word,String attribute){
    this.word = word;
    this.attribute = attribute;
  }

  public String getWord() {
    return word;
  }
  public void setWord(String word) {
    this.word = word;
  }
  public String getAttribute() {
    return attribute;
  }
  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public void addNum(){
	  num++;
  }
  public int getNum() {
	return num;
}
  public String toString(){
      return this.word+"/"+attribute+"/"+num;
  }

}