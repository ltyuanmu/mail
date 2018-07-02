package com.example.mail.service;

/**
 * websocket 主动发送服务封装
 * Created by haijiang.mo on 2017/3/2.
 */
public interface WebSocketService {

    /**
     * 发送信息到默认路由
     * @param message    发送的消息体
     */
    void sendMessage(String message);

}
