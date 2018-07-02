package com.example.mail.service;

import java.util.List;

/**
 * 描述：邮件服务
 *
 * @author LT
 * @date 2018/2/22
 */
public interface MailService {


    /**
     * 单个发送邮件
     * @param email 邮件地址
     * @param subject 标题
     * @param content 内容
     * @return 是否成功
     */
    boolean sendMail(String email, String subject, String content);

    /**
     * 批量发送邮件
     * @param emailList 邮箱
     * @param subject 主题
     * @param content 内容
     */
    void sendMail(List<String> emailList, String subject, String content);


}
