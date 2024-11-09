package com.jsh.erp.utils;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.StringJoiner;

public class HttpUtil {
	
	private static final String sRequest = "request";

	public static InputStream getHttpInputStream(String requestUrl) {

		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();

			HttpGet httpGet = new HttpGet(requestUrl);

			CloseableHttpResponse response = httpClient.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();

				return entity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HttpServletRequest getHttpServletRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		return request;
	}

	public static JSONObject getRequest() {
		HttpServletRequest httpServletRequest = getHttpServletRequest();
		JSONObject jsonObject = (JSONObject) httpServletRequest.getAttribute(HttpUtil.sRequest);
		if(jsonObject==null) {
			jsonObject = new JSONObject();
			jsonObject.put("error", "cat not get request");
		}
		return jsonObject;
	}
	
	public static void recordRequest(String body) {
		
		 
	    
		String authKey = "X-API-Authorization";
		HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
		
		Enumeration<String> names = httpServletRequest.getHeaderNames();
		JSONObject jsonHeader = new JSONObject();
		 while (names.hasMoreElements()) {
		        String name = names.nextElement();
		        String value = httpServletRequest.getHeader(name);
		        jsonHeader.put(name, convert(value));
		}
		
		String reqequestUri = httpServletRequest.getRequestURI();
		String queryString = httpServletRequest.getQueryString();

		String clientAddr = getClientIpAddr(httpServletRequest);
		

		JSONObject json = new JSONObject();
		json.put("header", jsonHeader);
		json.put("reqequestURL", convert(reqequestUri));
		json.put("queryString", convert(queryString));
		json.put("clientIP", convert(clientAddr));
		
		json.put("body", convert(body));
		
		httpServletRequest.setAttribute(HttpUtil.sRequest, json);
	}

	

	public static <T> void invalidate(T object, Class<?>... groups) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		boolean bEmpty= violations.isEmpty();
		if(bEmpty) {
			return;
		}
		StringJoiner set = new StringJoiner(",");
		violations.stream().forEach(i->{
			String tempMessage = i.getMessage();
			set.add(tempMessage);	
		});
		String error = set.toString();
		throw new BusinessRunTimeException(error);
	}


	public static Object convert(Object object) {
		if (object == null) {
			return null;
		}
		return object;
	}

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static JSONObject addInfoForResponse(JSONObject json) {
		
		json.put("message", "ok");
		json.put("type", "success");
		json.put("code", 0);
//		 SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
//		 Date current = new Date();
//		json.put("responseTime", sdFormat.format(current));
		return json;
	}

	public static ResponseEntity<Object> generateOkResponse() {
		return generateOkResponse(new JSONObject());
	}

	public static ResponseEntity<Object> generateOkResponse(JSONObject json) {
		JSONObject tempJson = addInfoForResponse(json);
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(tempJson.toString(), HttpStatus.OK);//spring使用jsonObject作為序列化器和反序列化器，但是如果你的對象直接是jsonObject了，spring反而不知道怎麼去序列化它
		return responseEntity;
	}
}
