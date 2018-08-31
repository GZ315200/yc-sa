package com.unistack.tamboo.mgt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unistack.tamboo.commons.utils.Const;
import com.unistack.tamboo.commons.utils.errors.*;
import com.unistack.tamboo.mgt.common.RestCode;
import com.unistack.tamboo.mgt.common.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * 处理更新文件异常
     *
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(MultipartException.class)
    public void handleMultipartException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.UPLOAD_SIZE_ERROR;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.OPERATION_ERROR, ex);
    }


    /**
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public void handleAuthenticateException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.PASSWORD_IS_NOT_CORRECT;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.ACCESS_DENIED;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.UNAUTHORIZED, ex);
    }

    /**
     * 处理数据库异常
     *
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler({SQLException.class})
    public void handleSQLException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.DATABASE_ERROR;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    @ExceptionHandler({NullPointerException.class})
    public void handleNullPointerException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.NULL_PARAM_INPUT;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    @ExceptionHandler({DataException.class})
    public void handleDataException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.NO_DATA_RETURN;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    /**
     * 处理运行异常
     *
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(GeneralServiceException.class)
    public void handleRuntimeException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logger.error(getHttpClientInfo(request), ex);
        handleAllException(ex, request, response);
    }


    /**
     * CollectOffsetException
     * DataException
     * DataFormatErrorException
     * GeneralServiceException
     * InvalidValueException
     * NoAvailableUrlException
     * NoLoginSession
     * NotFoundException
     * SerializationException
     *
     * @param ex
     * @param request
     * @param response
     */

    @ExceptionHandler(NoLoginSession.class)
    public void handlerNoLoginSession(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.NOT_LOGIN_ERROR;
        logger.error(getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.NEED_LOGIN, ex);
    }


    @ExceptionHandler(DataFormatErrorException.class)
    public void handlerDataFormatErrorException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.FORMAT_DATA_ERROR;
        logger.error(getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    @ExceptionHandler(NoAvailableUrlException.class)
    public void handlerNoAvailableUrlException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.NO_AVAILABLE_URL;
        logger.error(getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    @ExceptionHandler(NotFoundException.class)
    public void handlerNotFoundException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.NO_FOUND_DATA;
        logger.error(getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }


    /**
     * 处理所有异常
     *
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(Exception.class)
    public void handleAllException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.INTERNAL_SERVER_UNKNOWN_ERROR;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }

    /**
     * 启动数据源异常
     *
     * @param ex
     * @param request
     * @param response
     */
    @ExceptionHandler(DataSourceStartException.class)
    public void handleMultipartException(DataSourceStartException ex, HttpServletRequest request, HttpServletResponse response) {
        Const.ErrorMessage error = Const.ErrorMessage.START_DATASOURCE_ERROR;
        logger.error(error + ":" + getHttpClientInfo(request), ex);
        populateExceptionResponse(response, error.toString(), RestCode.ERROR, ex);
    }

    /**
     * 填充异常
     *
     * @param response
     * @param errorMessage
     * @param restCode
     * @param exception
     */
    private void populateExceptionResponse(HttpServletResponse response, String errorMessage, RestCode restCode,
                                           Exception exception) {
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/com.unistack.tamboo.commons.json; charset=utf-8");


            ServerResponse serverResponse = new ServerResponse(restCode.getStatus(), errorMessage);

            PrintWriter out = response.getWriter();
            out.append(jsonMapper.writeValueAsString(serverResponse));

        } catch (IOException e) {
            logger.error("failed to populate response error", e);
        }
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

    /**
     * 将HttpServletRequest转化为Map
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        // 参数Map
        Map<String, String[]> properties = request.getParameterMap();
        //返回值Map
        Map<String, String> returnMap = new HashMap<String, String>();
        Set<String> keySet = properties.keySet();
        for (String key : keySet) {
            String[] values = properties.get(key);
            String value = "";
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null && !"".equals(values[i])) {
                        value = values[i] + ",";
                    }
                }
                if (value != null && !"".equals(value)) {
                    value = value.substring(0, value.length() - 1);
                }
            }

            returnMap.put(key, value);
        }
        return returnMap;
    }

}
