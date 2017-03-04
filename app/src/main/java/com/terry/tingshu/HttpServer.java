package com.terry.tingshu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by terry on 2017/3/2.
 * TingShu
 */

public class HttpServer extends NanoHTTPD {
    public enum Status implements NanoHTTPD.Response.IStatus{
        SWITCH_PROTOCOL(101,"Switching Protocols"),NOT_USE_POST(100,"not use post");

        private final int requestStatus;
        private final String description;


        Status(int requestStatus, String description){
            this.requestStatus = requestStatus;
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public int getRequestStatus() {
            return requestStatus;
        }
    }

    public HttpServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        /*我在这里做了一个限制，只接受POST请求。这个是项目需求*/
        if(Method.POST.equals(session.getMethod())){
            Map<String,String> files = new HashMap<>();
            /*获取header信息，NanoHttp的Header不仅仅是HTTP的Header，还包括其他信息.*/
            Map<String,String> header = session.getHeaders();
            try{
                /*这句尤为重要的就是将body的数据写入files中，大家可以看看parseBody具体实现.*/
                session.parseBody(files);
                /*看就是这里,POST请求的body数据可以完整读出来*/
                String body = session.getQueryParameterString();
                System.out.println("Header:" + header);
                System.out.println("Body:" + body);
                /*这里是从header里面获取客户端的IP地址。NanoHttpd的header包含的东西不止是HTTP Header的内容.*/
                header.get("http-client-ip");
            } catch (ResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(Response.Status.OK,"text/html","Hello world!");
        }
        else{
            return newFixedLengthResponse(Status.NOT_USE_POST,"text/html","use post");
        }

//        return super.serve(session);
    }
}
