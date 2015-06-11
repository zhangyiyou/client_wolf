package com.wolf.webservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * http调用webservice示例，查询国内手机号码归属地
 * @author Administrator
 *
 */
public class HttpWebService {

	private static Logger logger = Logger.getLogger("HttpWebService");
	
	//wsdl 地址
	private static String wsdlUri = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
	//方法�?
	private static String methodName = "getMobileCodeInfo";
	//命名空间
	private static String tns = "http://WebXml.com.cn/";
	
	private static int invoke(Map<String,String> patameterMap){
		int statusCode = -1;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(wsdlUri);
		String soapRequestData = buildRequestDataSoap(patameterMap);
		System.out.println(soapRequestData);
		byte bytes[] = soapRequestData.getBytes(Consts.UTF_8);
		InputStream is = new ByteArrayInputStream(bytes);
//		HttpEntity reqEntity = new InputStreamEntity(is, bytes.length, ContentType.APPLICATION_XML);
		HttpEntity reqEntity = new StringEntity(soapRequestData,Consts.UTF_8);
		httpPost.setHeader("Content-Type", "application/soap+xml; charset=utf-8");
		httpPost.setEntity(reqEntity);
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			resEntity.getContent();
			String respXml = EntityUtils.toString(resEntity);
			System.out.println("返回结果�?"+respXml);
			System.out.println("返回状�?�："+response.getStatusLine().getStatusCode());
			statusCode = response.getStatusLine().getStatusCode();
			logger.info("测试日志。�?��?��?��?��?��?��?��??");
			response.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
		return statusCode;
	}
	
	/**
	 * 绑定soap协议
	 * @param params
	 * @return
	 */
	private static String buildRequestDataSoap(Map<String,String> params){
		StringBuffer soapRequestData = new StringBuffer();
		soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapRequestData.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">");
		soapRequestData.append("<soap:Body>");
		soapRequestData.append("<"+methodName +" xmlns=\"" + tns + "\">");
		Set<String> nameSet = params.keySet();
		for (String name : nameSet) {
			soapRequestData.append("<"+name+">"+params.get(name)+"</"+name+">");
		}
		soapRequestData.append("</"+methodName+">");
		soapRequestData.append("</soap:Body>");
		soapRequestData.append("</soap:Envelope>");
		return soapRequestData.toString();
	}
	
	public static void main(String[] args) {
		Map<String, String> patameterMap = new HashMap<String, String>();
		patameterMap.put("mobileCode","13005017141");
		HttpWebService.invoke(patameterMap);
	}
}
