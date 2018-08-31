package com.unistack.tamboo.mgt.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 工具类 
 *
 */
public class TMUtil {
    
	
    private TMUtil(){
        
    }
    
    
	/**
	 * 判断object是否为NULL
	 */
	@SuppressWarnings("rawtypes")
	public static Boolean isNullOrBlank(Object obj){
		if( obj == null) {
		    return true;
		}
		if(obj instanceof Collection){
			if( ((Collection)obj).isEmpty() ){
				return true;
			}
		}else{
			if( "".equals(obj.toString().trim())){
				return true;
			}
		}
		return false;
	}
	
	public static Boolean isNotNullOrBlank(Object obj){
		return !isNullOrBlank(obj);
	}


	/**
	 * 将HttpServletRequest转化为Map
	 * @param request
	 * @return
	 */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map<String, String[]> properties = request.getParameterMap();
        //返回值Map
        Map<String, String> returnMap = new HashMap<String, String>();
        Set<String> keySet = properties.keySet();
        for(String key: keySet){
            String[] values = properties.get(key);
            String value = "";
            if(values != null){
                for(int i=0;i<values.length;i++){
                    if(values[i] != null && !"".equals(values[i])){
                        value = values[i] + ",";
                    }
                }
                if(value != null && !"".equals(value)){
                    value = value.substring(0, value.length()-1);
                }
            }

            returnMap.put(key, value);
        }
        return returnMap;
    }
    /**
     * 将时间戳转为整10秒的时间戳
     * @param source
     * @return
     */
    public static long formatTimestamp(long source) {
		// source : Thu Apr 07 21:53:13 CST 2016
		// return : Thu Apr 07 21:53:10 CST 2016
		// 返回整10秒
		return source / (1000 * 10) * (1000 * 10);
	}

    /**
     * 获取参数的值
     * @param parameters
     * @param parameter
     * @return
     */
    public static String getValue(String parameters, String parameter) {
        String[] parameterArr = parameters.split(" ");
        for(int i=0;i<parameterArr.length;i++){
            String p = parameterArr[i];
            if(parameter.equals(p)){
                return parameterArr[i+1];
            }
        }
        return null;
    }
    
    
    /**
     * 返回HttpClient的信息
     * 
     * @param request
     * @return
     */
    public static String getHttpClientInfo(HttpServletRequest request) {
        String mark = "::";
        String seperator = "\t";
        String labelRequestUrl = "Request URL";
        String labelRequestMethod = "Request Method";
        String labelClientIp = "Client IP";
        String labelUserAgent = "User Agent";
        String labelRequestParams = "Request Params";

        String clientIp = null;
        if (request.getRemoteAddr() != null) {
            clientIp = request.getRemoteAddr();
        } else if (request.getRemoteHost() != null) {
            clientIp = request.getRemoteHost();
        } else if (request.getHeader("X-FORWARDED-FOR") != null) {
            clientIp = request.getHeader("X-FORWARDED-FOR");
        }
        String restURL = request.getRequestURL().toString();
        String restMethod = request.getMethod();
        String userAgent = request.getHeader("user-agent");

        StringBuilder sb = new StringBuilder();
        sb.append(labelRequestUrl).append(mark).append(restURL).append(seperator);
        sb.append(labelRequestMethod).append(mark).append(restMethod).append(seperator);
        sb.append(labelRequestParams).append(mark).append(getParameterMap(request)).append(seperator);
        sb.append(labelClientIp).append(mark).append(clientIp).append(seperator);
        sb.append(labelUserAgent).append(mark).append(userAgent).append(seperator);
        

        return sb.toString();
    }
}
