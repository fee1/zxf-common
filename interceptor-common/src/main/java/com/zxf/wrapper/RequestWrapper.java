package com.zxf.wrapper;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 请求包装类
 * @author zhuxiaofeng
 * @date 2021/7/4
 */
@Slf4j
public abstract class RequestWrapper extends HttpServletRequestWrapper {

    /**
     * 请求内容
     */
    private String bodyStr;

    public RequestWrapper(HttpServletRequest request) {
        super(request);
        BufferedReader bufferedReader = null;
        try (InputStream inputStream = request.getInputStream()){
            StringBuilder bodyBuilder = new StringBuilder();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                char[] charsBuffer = new char[128];
                int bytesLocation = -1;
                while ((bytesLocation = bufferedReader.read()) > 0){
                    bodyBuilder.append(charsBuffer, 0, bytesLocation);
                }
            }else {
                bodyBuilder.append("");
            }
            this.bodyStr = bodyBuilder.toString();
        }catch (Exception e){
            log.error("RequestWrapper exception message: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.bodyStr.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBodyStr() {
        return this.bodyStr;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }
}
