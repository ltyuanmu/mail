package com.example.mail.controller;

import com.example.mail.configuration.WebConfig;
import com.example.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 描述：
 *
 * @author LT
 * @date 2018/2/22
 */
@RestController
@Slf4j
public class MailController {

    @Autowired
    private MailService mailService;
    @Autowired
    private ExecutorService executorService;

    @PostMapping(value = "/mail/send")
    public ResponseEntity sendMail(@RequestParam("email") MultipartFile email, @RequestParam("subject") String subject, @RequestParam("content") MultipartFile content){
        if(WebConfig.sendFlag){
            return ResponseEntity.ok("邮件正在发送 请先停止发送再进行发送!!");
        }
        try {
            List<String> emailList = IOUtils.readLines(email.getInputStream());
            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = content.getInputStream();
            for (int i; (i = is.read(buf)) != -1;) {
                baos.write(buf, 0, i);
            }
            String contentStr = baos.toString("UTF-8");
            WebConfig.stopFlag=false;
            executorService.execute(() -> this.mailService.sendMail(emailList,subject,contentStr));
            WebConfig.sendFlag=true;
            return ResponseEntity.ok("SUCCESS");
        }  catch (Exception e){
            log.error(e.getMessage(),e);
            return ResponseEntity.status(500).body("ERROR");
        }
    }


    @GetMapping(value = "/mail/stop")
    public ResponseEntity stopSendMail(){
        try {
            WebConfig.stopFlag = true;
            return ResponseEntity.ok("SUCCESS");
        }  catch (Exception e){
            log.error(e.getMessage(),e);
            return ResponseEntity.status(500).body("ERROR");
        }
    }

}
