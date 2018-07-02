package com.example.mail.service.impl;

import com.example.mail.configuration.WebConfig;
import com.example.mail.service.MailService;
import com.example.mail.service.WebSocketService;
import com.example.mail.utils.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：邮件服务实现
 *
 * @author LT
 * @date 2018/2/22
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Value("${mail.host}")
    private String host;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;
    @Autowired
    private WebSocketService webSocketService;


    @Override
    public boolean sendMail(String email, String subject, String content) {
        boolean status = Mail.send(host, username, email, subject, content, username, password);
        return status;
    }

    @Override
    public void sendMail(List<String> emailList, String subject, String content) {
        int total = emailList.size();
        int i = 1;
        log.info("总数为==" + total);
        for(String email:emailList){
            if(WebConfig.stopFlag){
                log.info("发送邮件中断停止!!");
                this.webSocketService.sendMessage("发送邮件中断停止!!");
                break;
            }
            long start = System.currentTimeMillis();
            boolean flag = this.sendMail(email, subject, content);
            String message="进度:" + i + "/" + total + "===发送结果:" + flag + "===耗时:" + (System.currentTimeMillis() - start) + "ms===邮箱地址:"+email;
            log.info(message);
            this.webSocketService.sendMessage(message);
            i++;
        }
        WebConfig.sendFlag=false;
    }
}
