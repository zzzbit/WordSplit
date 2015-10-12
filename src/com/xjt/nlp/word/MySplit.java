package com.xjt.nlp.word;


public class MySplit {
	/**
	 * @param args
	 */
	private String dealString; 
	MySplit(String dealString){
		this.dealString = dealString;
	}
	public String startSplit(){
		ICTCLAS ict = ICTCLAS.getInstance();
		int count = 0;
		String str = ict
				.paragraphProcess(dealString);
		String[] allWords = str.split(" ");
		String wordString;
		String attributeString;
		Word[] word = new Word[allWords.length];
		boolean flag;
		System.out.println(allWords.length);
		for (int i = 0; i < allWords.length; i++) {
			flag = true;
			int pos = allWords[i].lastIndexOf("/");
			if (pos > 0) {
				wordString = allWords[i].substring(0, pos);
				attributeString = allWords[i].substring(pos + 1);
				for (int j = 0; j < count; j++) {
					if (wordString.equals(word[j].getWord())) {
						word[j].addNum();
						flag = false;
					}
				}
				if (!flag || !attributeString.equals("n")){
					continue;
				}
				word[count++] = new Word(wordString, attributeString);
				word[count-1].addNum();
			}
		}
		System.out.println(count);
		Word tmp;
		for (int i = 0; i < count -1; i++){
			flag = true;
			for (int j = 0; j < count - i - 1; j++){
				if (word[j].getNum()<word[j+1].getNum()){
					tmp = word[j];
					word[j] = word[j+1];
					word[j+1] = tmp;
					flag = false;
				}
			}
			if (flag){
				break;
			}
		}
		String resultString = null;
		for (int i = 0; i < count; i++){
			resultString += (word[i].toString() + "\n");
		}
		return resultString;
	}
	public static void main(String[] args) {
		
		
	}

}
