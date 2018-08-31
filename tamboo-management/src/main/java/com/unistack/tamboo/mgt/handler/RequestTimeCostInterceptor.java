package com.unistack.tamboo.mgt.handler;

import com.unistack.tamboo.mgt.utils.TMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RequestTimeCostInterceptor <br>
 * 记录处理请求花费时间 
 */

@Component
public class RequestTimeCostInterceptor extends HandlerInterceptorAdapter {

	private  Logger logger = LoggerFactory.getLogger(getClass());
	private static  String START_TIME_LABEL = "Start Time: ";
	private static  String START_TIME_VAR = "rpStartTime";
	private static  String TIME_TAKEN_LABEL = "Request Time Taken: ";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		long startTime = System.currentTimeMillis();

		logRequestStart(request, startTime);
		request.setAttribute(START_TIME_VAR, startTime);

		return true;

	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		logRequestEnd(request);
		populateResponseHeader(response);
		
	}
	
	/**
	 * set customized header 
	 * now we don't use head so tested header is put
	 * 
	 * @param response
	 */
	private void populateResponseHeader(HttpServletResponse response){
		response.setHeader("ABC", "DEF");
	}


	private void logRequestStart(HttpServletRequest request, long startTime) {
		String message = TMUtil.getHttpClientInfo(request);
		logger.debug(message + "\t " + START_TIME_LABEL + startTime );
	}

	private void logRequestEnd(HttpServletRequest request) {

		long startTime = (Long) request.getAttribute(START_TIME_VAR);
		long cost = System.currentTimeMillis() - startTime;
		
		// 经验值
        if (cost >= 300) {
            String message = TMUtil.getHttpClientInfo(request);
            logger.info(TIME_TAKEN_LABEL + cost +" ms" + "\t " + message);
        }

	}

}