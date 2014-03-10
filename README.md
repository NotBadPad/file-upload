file-upload
===========

HTTP文件上传的小例子，主要实现了后台对HTTP中input:file文件上传请求（RFC1867）的处理。HTTP接收处理使用JDK自带的httpserver。

使用方法
===========
1、项目clone到本地，导入eclipse(项目使用maven构建)\n\r
2、运行项目org.eh.fileupload.EHHttpServer.Main\n\r
3、浏览器打开项目下的testpage/upload.html\n\r
4、选择文件并提交表单\n\r
5、文件将上传至项目temp/下\n\r

类介绍
===========
file-upload\n\r
 --org.eh.fileupload\n\r
   --Analysis.java   HTTP请求content分析类，从content中提取出file信息\n\r
   --EHHttpServer.java   主服务类，启动服务并接收HTTP请求\n\r
   --FileInfo.java   file封装\n\r
