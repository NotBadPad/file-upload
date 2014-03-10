package org.eh.fileupload;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Hashtable;

/**
 * 解析content获取文件信息
 * @author guojing
 * @date 2014-3-10
 */
public class Analysis {
	static final int NONE = 0;
	static final int DATAHEADER = 1; 
	static final int FILEDATA = 2;
	static final int FIELDDATA = 3;
	static final int MXA_SEGSIZE = 1000 * 1024 * 10; //最大文件长度
	
	public static FileInfo parse(InputStream ins, String contentType, int totalLength)
			throws IOException {

		FileInfo fileInfo =new FileInfo();
		String fieldname = ""; // 表单域的名称
		String fieldvalue = ""; // 表单域的值
		String filename = ""; // 文件名
		String boundary = ""; // 分界符
		String lastboundary = ""; // 结束符
		String filePath = "";
		Hashtable<String, String> formfields = new Hashtable<String, String>();
		int filesize = 0; // 文件长度

		int pos = contentType.indexOf("boundary=");

		if (pos != -1) { // 取得分界符和结束符
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastboundary = boundary + "--";
		}
		int state = NONE;
		// 得到数据输入流reqbuf
		DataInputStream in = new DataInputStream(ins);
		// 将请求消息的实体送到b变量中
		int totalBytes = totalLength;
		String message = "";
		if (totalBytes > MXA_SEGSIZE) {// 每批大于10m时
			message = "Each batch of data can not be larger than " + MXA_SEGSIZE / (1000 * 1024)
					+ "M";
			return null;
		}
		byte[] b = new byte[totalBytes];
		in.readFully(b);
		in.close();
		String reqContent = new String(b, "UTF-8");//
		BufferedReader reqbuf = new BufferedReader(new StringReader(reqContent));

		boolean flag = true;
		int i = 0;
		while (flag == true) {
			String s = reqbuf.readLine();
			if ((s == null) || (s.equals(lastboundary)))
				break;

			switch (state) {
			case NONE:
				if (s.startsWith(boundary)) {
					state = DATAHEADER;
					i += 1;
				}
				break;
			case DATAHEADER:
				pos = s.indexOf("filename=");
				if (pos == -1) { // 将表单域的名字解析出来
					pos = s.indexOf("name=");
					pos += "name=".length() + 1;
					s = s.substring(pos);
					int l = s.length();
					s = s.substring(0, l - 1);
					fieldname = s;
					state = FIELDDATA;
				} else { // 将文件名解析出来
					String temp = s;
					pos = s.indexOf("filename=");
					pos += "filename=".length() + 1;
					s = s.substring(pos);
					int l = s.length();
					s = s.substring(0, l - 1);// 去掉最后那个引号”
					filePath = s;
					pos = s.lastIndexOf("\\");
					s = s.substring(pos + 1);
					filename = s;
					// 从字节数组中取出文件数组
					pos = byteIndexOf(b, temp, 0);
					b = subBytes(b, pos + temp.getBytes().length + 2, b.length);// 去掉前面的部分
					int n = 0;
					/**
					 * 过滤boundary下形如 Content-Disposition: form-data; name="bin"; filename="12.pdf" Content-Type:
					 * application/octet-stream Content-Transfer-Encoding: binary 的字符串
					 */
					while ((s = reqbuf.readLine()) != null) {
						if (n == 1)
							break;
						if (s.equals(""))
							n++;

						b = subBytes(b, s.getBytes().length + 2, b.length);
					}
					pos = byteIndexOf(b, boundary, 0);
					if (pos != -1)
						b = subBytes(b, 0, pos - 1);

					filesize = b.length - 1;
					formfields.put("filesize", String.valueOf(filesize));
					state = FILEDATA;
				}
				break;
			case FIELDDATA:
				s = reqbuf.readLine();
				fieldvalue = s;
				formfields.put(fieldname, fieldvalue);
				state = NONE;
				break;
			case FILEDATA:
				while ((!s.startsWith(boundary)) && (!s.startsWith(lastboundary))) {
					s = reqbuf.readLine();
					if (s.startsWith(boundary)) {
						state = DATAHEADER;
						break;
					}
				}
				break;
			}
		}
		fileInfo.setBytes(b);
		fileInfo.setFilename(filename);
		fileInfo.setLength(b.length);
		return fileInfo;

	}

	// 字节数组中的INDEXOF函数，与STRING类中的INDEXOF类似
	public static int byteIndexOf(byte[] b, String s, int start) {
		return byteIndexOf(b, s.getBytes(), start);
	}

	// 字节数组中的INDEXOF函数，与STRING类中的INDEXOF类似
	public static int byteIndexOf(byte[] b, byte[] s, int start) {
		int i;
		if (s.length == 0) {
			return 0;
		}
		int max = b.length - s.length;
		if (max < 0)
			return -1;
		if (start > max)
			return -1;
		if (start < 0)
			start = 0;
		search: for (i = start; i <= max; i++) {
			if (b[i] == s[0]) {
				int k = 1;
				while (k < s.length) {
					if (b[k + i] != s[k]) {
						continue search;
					}
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	// 用于从一个字节数组中提取一个字节数组
	public static byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	// 用于从一个字节数组中提取一个字符串
	public static String subBytesString(byte[] b, int from, int end) {
		return new String(subBytes(b, from, end));
	}

}
