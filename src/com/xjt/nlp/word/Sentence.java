package com.xjt.nlp.word;

/**
 * <p>Title: Java中文分词组件</p>
 * <p>Description: 本组件以中科院ICTCLAS系统为基础，在其基础之上改编，本组件仅供学习和研究用途，任何商业用途将自行承担法律后果，与组件编写人无关。</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: 北京师范大学</p>
 * @author 陈天
 * @version 1.0
 */
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
public class Sentence {
    private List sentence = new LinkedList();
    public Sentence() {
    }

    /**
     * 得到某个词汇
     * @param index 第几个词汇
     * @return 词汇对象
     */
    public Word getWord(int index){
        return (Word)sentence.get(index);
    }

    /**
     * 得到一句话的词汇数量
     * @return 词汇数量
     */
    public int totalWords(){
        return sentence.size();
    }

    /**
     * 在一句话的最后增加一个词
     * @param word 词对象
     */
    public void addWord(Word word){
        sentence.add(word);
    }

    /**
     * 从一句话中去掉某个词
     * @param word
     */
    public void removeWord(Word word){
        sentence.remove(word);
    }

    /**
     * 从一句话中去掉某个词
     * @param index
     */
    public void removeWord(int index){
        sentence.remove(index);
    }

    /**
     * 合成一个完整的句子，不带任何词性标注
     * @return
     */
    public String toSentence(){
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<sentence.size();i++){
            Word word = (Word)sentence.get(i);
            buffer.append(word.getWord());
        }
        return buffer.toString();
    }

    /**
     * 合成一个带词性标注的句子
     * @return
     */
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<sentence.size();i++){
            Word word = (Word)sentence.get(i);
            buffer.append(word.toString());
        }
        return buffer.toString();

    }

    /**
     * 将句子中的若干个单词和并
     * @param startIndex 开始要合并的单词的位置
     * @param endIndex 最后一个要合并的单词的位置
     * @param newAttr 合并后的新词性
     */
    public void mergeWord(int startIndex, int endIndex, String newAttr){
        if (startIndex<0 || endIndex>=sentence.size())
            throw new IllegalArgumentException("词汇合并的开始位置和结束位置不正确");
        if (startIndex>=endIndex)
            throw new IllegalArgumentException("词汇合并的开始位置不能大于或者等于结束位置");
        Word startWord=(Word)sentence.get(startIndex);
        for (int i=startIndex+1;i<=endIndex;i++){
            Word word = (Word) sentence.get(startIndex+1);
            startWord.setWord(startWord.getWord()+word.getWord());
            sentence.remove(startIndex+1);
        }
        startWord.setAttribute(newAttr);
    }

    public static void main(String[] args) {
        Sentence sentence=new Sentence();
        Word word1 = new Word("中华","n");
        Word word2 = new Word("人民","nr");
        Word word3 = new Word("共和国","n");
        sentence.addWord(word1);
        sentence.addWord(word2);
        sentence.addWord(word3);
        sentence.removeWord(word2);
        System.out.println(sentence.toSentence());
        sentence.mergeWord(0,1,"nz");
        System.out.println(sentence.toString());
    }



}