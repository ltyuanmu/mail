package com.example.mail.service.impl;

import com.example.mail.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 *  web socket 服务实现
 * Created by haijiang.mo on 2017/3/2.
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendMessage(String message) {
        this.simpMessagingTemplate.convertAndSend("/custom-mail/topic/mail/record",message);
    }
}
