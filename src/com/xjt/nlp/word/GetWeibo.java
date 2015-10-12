package com.xjt.nlp.word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Position;

public class GetWeibo {
	private String strHomePage = null; // ��ҳ��ַ
	private String strHost = null; // ������ַ
	private ArrayList<String> WaitingList = new ArrayList<String>(); // �洢δ����URL
	private ArrayList<String> AllUrlList = new ArrayList<String>(); // ����ɵ�URL
	private String charset = null;
	private int index = 0; // �ļ�����������
	private int count = 0; // �Ѽ����ҳ��Url����
	private String regDomain = ".*//.*/"; // ��������ʽ
	private String regUrl = "([\"\']http(s)?://|href=).+?[\"\']"; // URL��ַƥ������ʽ
	private String fileDirectory = "MyFile/";
	// ����Ĳ�����Ҫ���õ�
	private int intThreadNum = 10; // �߳���
	private StringBuffer dealString = new StringBuffer();

	// �ѹ�mp3
	private String regTitle = "articalContent[\\s\\S]+?</div"; // ��������ʽ
	private String regContentUrl = "<span class=\"atc_title\">\n.+?/a"; // ������ҳ��ַ����ʽ
	private String regNextUrl = "<span class=\"atc_title\">\n.+?/a"; // ��һҳ��ҳ��ַ����ʽ

	public GetWeibo(String s) {
		this.strHomePage = s;
	}

	public static void main(String[] args) {
		String arg0 = "http://weibo.com/1869768143";
		GetWeibo gw = new GetWeibo(arg0);
		gw.addHtmlFile(arg0);
		// gw.startSpider();
	}

	// ����Ϣд��txt�ļ�
	private synchronized boolean add2File(String s) {
		try {
			// ���������ļ�Ŀ¼
			if (!new File(fileDirectory).isDirectory()) {
				new File(fileDirectory).mkdir();
			}
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					fileDirectory + "@" + System.currentTimeMillis() + ".html",
					true), charset);
			w.write(s);
			w.flush();
			w.close();
			return true;
		} catch (Exception e) {
			System.out.println("������Ϣ�ļ�ʧ��!");
			return false;
		}
	}

	// ��Դ��д���ļ������û�д����ʽ��
	public synchronized boolean addHtmlFile(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection conn;
			BufferedReader bReader;
			String rLine;
			StringBuffer stringBuffer = new StringBuffer();

			// �õ���վ����
			if (charset == null) {
				conn = url.openConnection();
				conn.setDoOutput(true);
				bReader = new BufferedReader(new InputStreamReader(
						url.openStream()));
				while ((rLine = bReader.readLine()) != null) {
					Matcher m = Pattern.compile("charset.+?\".+?\"").matcher(
							rLine);
					if (m.find()) {
						charset = m.group(0).substring(
								m.group(0).indexOf('\"') + 1,
								m.group(0).length() - 1);
						System.out.println(charset);
						break;
					}
				}
			}

			// �õ�Դ��
			conn = url.openConnection();
			conn.setDoOutput(true);
			bReader = new BufferedReader(new InputStreamReader(
					url.openStream(), charset));
			while ((rLine = bReader.readLine()) != null) {
				stringBuffer.append(rLine + "\n");
			}

			if (bReader != null) {
				bReader.close();
			}

			// �����ļ���
			if (!new File(fileDirectory).isDirectory()) {
				new File(fileDirectory).mkdir();
			}

			// д���ļ�
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					fileDirectory + "@" + System.currentTimeMillis() + ".html",
					true), charset);
			w.write(stringBuffer.toString());
			w.flush();
			w.close();
			return true;
		} catch (Exception e) {
			System.out.println("������Ϣ�ļ�ʧ��!");
			return false;
		}
	}

	// �ӵȴ�������ȡ��һ��
	private synchronized String getWaitingUrl() {
		String tmpAUrl = WaitingList.get(0);
		WaitingList.remove(0);
		return tmpAUrl;
	}

	// ��ƥ�䵽���ַ�����õ�URL��ַ��������Ҫ��ȫ
	private String checkUrl(String string) {
		Pattern p = Pattern.compile(regUrl, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(string);
		if (m.find()) {
			String tmpString = m.group(0);
			if (tmpString.indexOf('\"') != -1) {
				tmpString = tmpString.substring(tmpString.indexOf('\"') + 1,
						tmpString.lastIndexOf('\"'));
			} else {
				tmpString = tmpString.substring(tmpString.indexOf('\'') + 1,
						tmpString.lastIndexOf('\''));
			}
			if (tmpString.contains("http")) {
				System.out.println(tmpString);
				return tmpString;
			} else {
				tmpString = strHost + tmpString;
				System.out.println(tmpString);
				return tmpString;
			}
		} else {
			System.out.println("Error:" + string);
			return null;
		}
	}

	public void startSpider() { // ���û��ṩ������վ�㿪ʼ������������ҳ�����ץȡ

		// ����List��
		WaitingList.add(strHomePage);
		AllUrlList.add(strHomePage);

		// ��������
		Pattern p = Pattern.compile(regDomain, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(strHomePage);
		if (m.find()) {
			strHost = m.group(0);
		} else {
			strHost = strHomePage + "/";
		}

		// ����URL����Ӧ����ҳ����ץȡ
		String tmp = getWaitingUrl();
		this.getWebByUrl(tmp);

		// ���̵߳��ô������
		for (int i = 0; i < intThreadNum; i++) {
			new Thread(new Processer()).start();
		}

		// �ж����߳���ֹ����
		while (true) {
			if (WaitingList.isEmpty() && Thread.activeCount() == 1) {
				String dealStr = dealString.toString()
						.replaceAll("<[^>]+>", "");
				dealStr = dealStr.replaceAll(" ", "");
				dealStr = dealStr.replaceAll("&nbsp", "");
				MySplit mySplit = new MySplit(dealStr);
				add2File(mySplit.startSplit());
				System.out.println("Finished!");
				break;
			}
		}
	}

	// �Ժ�����������url����ץȡ
	public void getWebByUrl(String strUrl) {
		try {
			// ��URL�ж�������ҳ
			URL url = new URL(strUrl);
			URLConnection conn;
			BufferedReader bReader;
			String rLine;
			StringBuffer stringBuffer = new StringBuffer();

			// �õ���վ����
			if (charset == null) {
				conn = url.openConnection();
				conn.setDoOutput(true);
				bReader = new BufferedReader(new InputStreamReader(
						url.openStream()));
				while ((rLine = bReader.readLine()) != null) {
					Matcher m = Pattern.compile("charset.+?\".+?\"").matcher(
							rLine);
					if (m.find()) {
						charset = m.group(0).substring(
								m.group(0).indexOf('\"') + 1,
								m.group(0).length() - 1);
						System.out.println(charset);
						break;
					}
				}
			}

			// �õ�Դ��
			conn = url.openConnection();
			conn.setDoOutput(true);
			bReader = new BufferedReader(new InputStreamReader(
					url.openStream(), charset));
			while ((rLine = bReader.readLine()) != null) {
				stringBuffer.append(rLine + "\n");
			}

			if (bReader != null) {
				bReader.close();
			}

			if (!getUrlByString(stringBuffer.toString())) {
				getContentByString(strUrl, stringBuffer.toString());
			}
			stringBuffer = null;
		} catch (Exception e) {
			System.out.println("getWebByUrl error");
		}
	}

	// �����µ���ҳ����ȡ���к��е�������Ϣ������true��ʾ����ȡURLҳ�棬�����ʾ������ҳ��
	public boolean getUrlByString(String inputArgs) {
		boolean pageFlag = false;
		Pattern p;
		Matcher m;
		// �õ�����Ƭ��ҳ��URL
		String tmpStr = inputArgs;
		p = Pattern.compile(regContentUrl, Pattern.CASE_INSENSITIVE);
		m = p.matcher(tmpStr);
		boolean blnp = m.find();
		while (blnp == true) {
			String url = checkUrl(m.group(0));
			if (!AllUrlList.contains(url)) {
				WaitingList.add(url);
				AllUrlList.add(url);
				count++;
			}
			tmpStr = tmpStr.substring(m.end(), tmpStr.length());
			m = p.matcher(tmpStr);
			blnp = m.find();
			pageFlag = true;
		}

		if (!pageFlag) {
			return false;
		}
		// �ٴ�����һҳ�����
		p = Pattern.compile(regNextUrl, Pattern.CASE_INSENSITIVE);
		m = p.matcher(inputArgs);
		if (m.find()) {
			String url = checkUrl(m.group(0));
			WaitingList.add(url);
			AllUrlList.add(url);
			pageFlag = true;
		}
		return pageFlag;
	}

	public boolean getContentByString(String strUrl, String inputArgs) { // �����µ���ҳ����ȡ���к��е�������Ϣ
		String titleString = null;
		Pattern p;
		Matcher m;

		// �õ�����
		p = Pattern.compile(regTitle, Pattern.CASE_INSENSITIVE);
		m = p.matcher(inputArgs);
		if (m.find()) {
			titleString = m.group(0).substring(4, m.group(0).length() - 2);
		}
		dealString.append(titleString + "\n");
		return false;
	}

	class Processer implements Runnable { // ������ץȡ�߳�
		public void run() {
			while (!WaitingList.isEmpty()) {
				getWebByUrl(getWaitingUrl());
			}
		}
	}
}
