package com.xjt.nlp.word;

/**
 * <p>Title: Java���ķִ����</p>
 * <p>Description: ��������п�ԺICTCLASϵͳΪ�������������֮�ϸı࣬���������ѧϰ���о���;���κ���ҵ��;�����ге����ɺ�����������д���޹ء�</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ����ʦ����ѧ</p>
 * @author ����
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