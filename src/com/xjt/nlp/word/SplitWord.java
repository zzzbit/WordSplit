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

public class SplitWord {
    public SplitWord() {
    }
    /**
     * 在ICTCLAS的基础上做进一步的分词。
     * @param sentence 输入句子
     * @return 一个LinkedList链表，每一个项是一个Word对象
     */
    public static Sentence splitWord(String sSentence) {
        Sentence sentence = new Sentence();
        ICTCLAS ict = ICTCLAS.getInstance();
        String str = ict.paragraphProcess(sSentence.trim());
        String[] allWords = str.split(" ");
        for (int i = 0; i < allWords.length; i++) {
            int pos = allWords[i].lastIndexOf("/");
            if (pos > 0) {
                Word word = new Word(allWords[i].substring(0, pos),allWords[i].substring(pos + 1));
                sentence.addWord(word);
            }
        }
        return sentence;
    }

    public static void main(String[] args) {
        Sentence all = SplitWord.splitWord("巴拿马和美国都是国家地区，汉族是一个民族。");
        for (int i=0;i<all.totalWords();i++){
            Word word = (Word) all.getWord(i);
            System.out.println(word.toString());
        }
    }

}