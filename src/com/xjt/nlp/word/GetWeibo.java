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
	private String strHomePage = null; // 主页地址
	private String strHost = null; // 主机地址
	private ArrayList<String> WaitingList = new ArrayList<String>(); // 存储未处理URL
	private ArrayList<String> AllUrlList = new ArrayList<String>(); // 已完成的URL
	private String charset = null;
	private int index = 0; // 文件按递增命名
	private int count = 0; // 已加入的页面Url总数
	private String regDomain = ".*//.*/"; // 域名正则式
	private String regUrl = "([\"\']http(s)?://|href=).+?[\"\']"; // URL网址匹配正则式
	private String fileDirectory = "MyFile/";
	// 下面的参数是要设置的
	private int intThreadNum = 10; // 线程数
	private StringBuffer dealString = new StringBuffer();

	// 搜狗mp3
	private String regTitle = "articalContent[\\s\\S]+?</div"; // 标题正则式
	private String regContentUrl = "<span class=\"atc_title\">\n.+?/a"; // 内容网页网址正则式
	private String regNextUrl = "<span class=\"atc_title\">\n.+?/a"; // 下一页网页网址正则式

	public GetWeibo(String s) {
		this.strHomePage = s;
	}

	public static void main(String[] args) {
		String arg0 = "http://weibo.com/1869768143";
		GetWeibo gw = new GetWeibo(arg0);
		gw.addHtmlFile(arg0);
		// gw.startSpider();
	}

	// 将信息写入txt文件
	private synchronized boolean add2File(String s) {
		try {
			// 创建接收文件目录
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
			System.out.println("加入信息文件失败!");
			return false;
		}
	}

	// 将源码写入文件，供用户写正则式用
	public synchronized boolean addHtmlFile(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection conn;
			BufferedReader bReader;
			String rLine;
			StringBuffer stringBuffer = new StringBuffer();

			// 得到网站编码
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

			// 得到源码
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

			// 创建文件夹
			if (!new File(fileDirectory).isDirectory()) {
				new File(fileDirectory).mkdir();
			}

			// 写入文件
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					fileDirectory + "@" + System.currentTimeMillis() + ".html",
					true), charset);
			w.write(stringBuffer.toString());
			w.flush();
			w.close();
			return true;
		} catch (Exception e) {
			System.out.println("加入信息文件失败!");
			return false;
		}
	}

	// 从等待队列里取出一个
	private synchronized String getWaitingUrl() {
		String tmpAUrl = WaitingList.get(0);
		WaitingList.remove(0);
		return tmpAUrl;
	}

	// 从匹配到的字符串里得到URL地址，如有需要则补全
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

	public void startSpider() { // 由用户提供的域名站点开始，对所有链接页面进行抓取

		// 加入List中
		WaitingList.add(strHomePage);
		AllUrlList.add(strHomePage);

		// 查找域名
		Pattern p = Pattern.compile(regDomain, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(strHomePage);
		if (m.find()) {
			strHost = m.group(0);
		} else {
			strHost = strHomePage + "/";
		}

		// 对新URL所对应的网页进行抓取
		String tmp = getWaitingUrl();
		this.getWebByUrl(tmp);

		// 多线程调用处理程序
		for (int i = 0; i < intThreadNum; i++) {
			new Thread(new Processer()).start();
		}

		// 判断主线程终止条件
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

	// 对后续解析出的url进行抓取
	public void getWebByUrl(String strUrl) {
		try {
			// 从URL中读整个网页
			URL url = new URL(strUrl);
			URLConnection conn;
			BufferedReader bReader;
			String rLine;
			StringBuffer stringBuffer = new StringBuffer();

			// 得到网站编码
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

			// 得到源码
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

	// 解析新的网页，提取其中含有的链接信息，返回true表示是提取URL页面，否则表示是内容页面
	public boolean getUrlByString(String inputArgs) {
		boolean pageFlag = false;
		Pattern p;
		Matcher m;
		// 得到内容片网页的URL
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
		// 再处理下一页的情况
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

	public boolean getContentByString(String strUrl, String inputArgs) { // 解析新的网页，提取其中含有的链接信息
		String titleString = null;
		Pattern p;
		Matcher m;

		// 得到标题
		p = Pattern.compile(regTitle, Pattern.CASE_INSENSITIVE);
		m = p.matcher(inputArgs);
		if (m.find()) {
			titleString = m.group(0).substring(4, m.group(0).length() - 2);
		}
		dealString.append(titleString + "\n");
		return false;
	}

	class Processer implements Runnable { // 独立的抓取线程
		public void run() {
			while (!WaitingList.isEmpty()) {
				getWebByUrl(getWaitingUrl());
			}
		}
	}
}
