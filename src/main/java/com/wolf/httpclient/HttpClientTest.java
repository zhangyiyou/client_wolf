package com.wolf.httpclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

public class HttpClientTest {

	@Test
	public void jUnitTest(){
		
	}
	
	//HttpClient连接SSL 
	public void ssl(){
		CloseableHttpClient httpClient = null;
		FileInputStream is = null;
		KeyStore keyStore = null;
		try {
			// 获得密匙�?
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			is = new FileInputStream(new File("d:\\tomcat.keystore"));
			// 加载keyStore d:\\tomcat.keystore  // 密匙库的密码
			keyStore.load(is,"123".toCharArray());
			
//			X509TrustManager tm = new X509TrustManager() {
//				@Override
//				public X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//				@Override
//				public void checkServerTrusted(X509Certificate[] chain, String authType)
//						throws CertificateException {
//				}
//				@Override
//				public void checkClientTrusted(X509Certificate[] chain, String authType)
//						throws CertificateException {
//				}
//			};
			// 相信自己的CA和所有自签名的证�?
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore,new TrustSelfSignedStrategy()).build();
//			sslContext.init(null, new TrustManager[]{tm}, null);
			//只允许使用TLSv1协议  
			SSLConnectionSocketFactory  sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" },null,SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
			// 创建http请求(get方式)  
			HttpGet httpGet = new HttpGet("");
			System.out.println("executing request" + httpGet.getRequestLine());

			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			System.out.println("executing response" + response.getStatusLine());
			if(entity != null){
				System.out.println("Response content length: " + entity.getContentLength());
				System.out.println(EntityUtils.toString(entity));
				EntityUtils.consume(entity);//关闭�? if(entity.isStreaming()) {final InputStream instream = entity.getContent();if (instream != null) {instream.close();}}
			}
			response.close();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException e) {
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//post方式提交表单（模拟用户登录请求） 
	public void postForm(){
		// 创建默认的httpClient实例.
		CloseableHttpClient  httpClient = HttpClients.createDefault();
		// 创建httppost
		HttpPost httpPost = new HttpPost("");
		// 创建参数队列  
		List<NameValuePair> formParms = new ArrayList<NameValuePair>();
		formParms.add(new BasicNameValuePair("name", "admin	"));
		formParms.add(new BasicNameValuePair("pwd", "123456"));
		try {
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParms,"UTF-8");//Post方式提交的乱码处�?
			httpPost.setEntity(urlEncodedFormEntity);
			System.out.println("executing request " + httpPost.getURI());
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			if(httpEntity != null){
				 System.out.println("--------------------------------------");  
                 System.out.println("Response content: " + EntityUtils.toString(httpEntity, "UTF-8"));//内容中文乱码处理
                 System.out.println("--------------------------------------");  
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			// 关闭连接,释放资源
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 发�?? post请求访问本地应用并根据传递参数不同返回不同结�? 
	public void post(){
		// 创建默认的httpClient实例.
		CloseableHttpClient httpClient = HttpClients.createDefault();
//		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);//连接超时
		// 创建httppost
		HttpPost httpPost = new HttpPost("http://www.sogou.com/web");
		// 创建参数队列  
		List<NameValuePair> formParms = new ArrayList<NameValuePair>();
//		formParms.add(new BasicNameValuePair("action","mobile"));
		formParms.add(new BasicNameValuePair("query", "15010576910"));
		
		try {
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formParms,"UTF-8");
			httpPost.setEntity(urlEncodedFormEntity);
			System.out.println("executing request " + httpPost.getURI());
			CloseableHttpResponse response = httpClient.execute(httpPost);
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {  
                System.out.println("--------------------------------------");  
                InputStreamReader isr = new InputStreamReader(entity.getContent());
                char buff[] = new char[2048];
    			int len = 0;
    			while((len = isr.read(buff)) != -1){
    				System.out.println(new String(buff,0,len));
    			}
//                System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));//以String形式获取内容
                System.out.println(response.getStatusLine());
                System.out.println("--------------------------------------");
                System.out.println("Response content: " + EntityUtils.toByteArray(entity));//以二进制byte流形�?
                isr.close();
            }
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			// 关闭连接,释放资源    
            try {  
                httpClient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
		}
	}
	
	// 发�?? get请求 
	public void get(){
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("");
		System.out.println("executing request " + httpGet.getURI()); 
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine());
			if (entity != null) {  
                // 打印响应内容长度    
                System.out.println("Response content length: " + entity.getContentLength());  
                // 打印响应内容    
                System.out.println("Response content: " + EntityUtils.toString(entity));  
            }  
			response.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {  
            // 关闭连接,释放资源    
            try {  
                httpClient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}
	
	//上传文件 
	public void upload(){
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("");
		FileBody fileBody = new FileBody(new File("F:\\image\\sendpix0.jpg"));
		StringBody stringBody =  new StringBody("", ContentType.TEXT_PLAIN);
		HttpEntity reqEntity  = MultipartEntityBuilder.create().addPart("bin", fileBody).addPart("comment", stringBody).build();
		httpPost.setEntity(reqEntity );
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				System.out.println("Response content length: " + resEntity.getContentLength());
			}
			EntityUtils.consume(resEntity);
			response.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//不指定参数名的方式来POST数据
	public void postXML(){
		CloseableHttpClient  httpClient =  HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://localhost:8080/waitsrv/GenXmlServlet");
		httpPost.addHeader("Content-Type",ContentType.TEXT_XML.getMimeType());//text/xml
		StringEntity stringEntity = new StringEntity("<html>你好啊啊</html>",Consts.UTF_8);
		httpPost.setEntity(stringEntity);
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			InputStreamReader isr = new InputStreamReader(resEntity.getContent(),Consts.ISO_8859_1);//以流的形�?
			char buff[] = new char[2048];
			int len = 0;
			while((len = isr.read(buff)) != -1){
				System.out.println(new String(buff,0,len));
			}
			response.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		HttpClientTest hct = new HttpClientTest();
		hct.post();
	}
}
