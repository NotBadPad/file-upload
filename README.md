file-upload
===========

HTTP文件上传的小例子，主要实现了后台对HTTP中input:file文件上传请求（RFC1867）的处理。HTTP接收处理使用JDK自带的httpserver。

使用方法
===========
<ul>
<li>1、项目clone到本地，导入eclipse(项目使用maven构建)</li>
<li>2、运行项目org.eh.fileupload.EHHttpServer.Main</li>
<li>3、浏览器打开项目下的testpage/upload.html</li>
<li>4、选择文件并提交表单</li>
<li>5、文件将上传至项目temp/下</li>
</ul>
类介绍
===========
<p>file-upload<br />&nbsp;--org.eh.fileupload<br />&nbsp; &nbsp; --Analysis.java   HTTP请求content分析类，从content中提取出file信息<br />&nbsp; &nbsp; --EHHttpServer.java   主服务类，启动服务并接收HTTP请求<br />&nbsp; &nbsp; --FileInfo.java   file封装</p>
