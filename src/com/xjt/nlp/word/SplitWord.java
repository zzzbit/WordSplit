package com.xjt.nlp.word;

/**
 * <p>Title: Java���ķִ����</p>
 * <p>Description: ��������п�ԺICTCLASϵͳΪ�������������֮�ϸı࣬���������ѧϰ���о���;���κ���ҵ��;�����ге����ɺ�����������д���޹ء�</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ����ʦ����ѧ</p>
 * @author ����
 * @version 1.0
 */
import java.util.LinkedList;
import java.util.List;

public class SplitWord {
    public SplitWord() {
    }
    /**
     * ��ICTCLAS�Ļ���������һ���ķִʡ�
     * @param sentence �������
     * @return һ��LinkedList����ÿһ������һ��Word����
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
        Sentence all = SplitWord.splitWord("��������������ǹ��ҵ�����������һ�����塣");
        for (int i=0;i<all.totalWords();i++){
            Word word = (Word) all.getWord(i);
            System.out.println(word.toString());
        }
    }

}