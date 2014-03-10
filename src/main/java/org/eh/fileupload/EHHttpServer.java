package org.eh.fileupload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

/**
 *
 * @author guojing
 * @date 2014-3-10
 */
public class EHHttpServer {

	@SuppressWarnings("restriction")
	public void startServer() throws IOException {
		// 启动服务器
		HttpServerProvider provider = HttpServerProvider.provider();
		HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(8899), 100);
		httpserver.createContext("/", new EHHttpHandler());
		httpserver.setExecutor(null);
		httpserver.start();
	}

	@SuppressWarnings("restriction")
	static class EHHttpHandler implements HttpHandler {
		@SuppressWarnings({ "unused" })
		public void handle(HttpExchange httpExchange) throws IOException {
			Headers headers = httpExchange.getRequestHeaders();
			//获取ContentType
			String contentType = headers.get("Content-type").toString().replace("[", "")
					.replace("]", "");

			//获取content长度
			int length = Integer.parseInt(headers.get("Content-length").toString().replace("[", "")
					.replace("]", ""));

			FileInfo fileInfo = Analysis.parse(httpExchange.getRequestBody(),
					contentType, length);
			FileOutputStream fos = new FileOutputStream(
					System.getProperty("user.dir") + "\\temp\\"
							+ fileInfo.getFilename());
			fos.write(fileInfo.getBytes());
			fos.close();

		}
	}

	public static void main(String[] args) throws IOException {
		new EHHttpServer().startServer();
	}

}
