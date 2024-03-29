
package com.example.mail.utils;

import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * 發送郵件的的類
 * 
 * @author huangl
 *
 */
@Slf4j
public class Mail {

	
	private static final String mailSmtp="mail.smtp.host";

	private MimeMessage mimeMsg; // MIME邮件对象
	private Session session; // 邮件会话对象
	private Properties props; // 系统属性
	// private boolean needAuth = false; // smtp是否需要认证
	// smtp认证用户名和密码
	private String username;
	private String password;
	private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

	/**
	 * Constructor
	 * 
	 * @param smtp
	 *            邮件发送服务器
	 */
	public Mail(String smtp) {
		setSmtpHost(smtp);
		createMimeMessage();
	}

	/**
	 * 设置邮件发送服务器
	 * 
	 * @param hostName
	 *            String
	 */
	public void setSmtpHost(String hostName) {
		log.info("设置系统属性：mail.smtp.host = " + hostName);
		if (props == null)
			props = System.getProperties(); // 获得系统属性对象
		props.put(mailSmtp, hostName); // 设置SMTP主机
	}

	/**
	 * 创建MIME邮件对象
	 * 
	 * @return
	 */
	public boolean createMimeMessage() {
		try {
			log.info("准备获取邮件会话对象！");
			session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
		} catch (Exception e) {
			System.err.println("获取邮件会话对象时发生错误！" + e);
			return false;
		}

		log.info("准备创建MIME邮件对象！");
		try {
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			mp = new MimeMultipart();

			return true;
		} catch (Exception e) {
			log.error("创建MIME邮件对象失败！", e);
			return false;
		}
	}

	/**
	 * 设置SMTP是否需要验证
	 * 
	 * @param need
	 */
	public void setNeedAuth(boolean need) {

		if (props == null){
			props = System.getProperties();
		}
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}

	/**
	 * 设置用户名和密码
	 * 
	 * @param name
	 * @param pass
	 */
	public void setNamePass(String name, String pass) {
		username = name;
		password = pass;
	}

	/**
	 * 设置邮件主题
	 * 
	 * @param mailSubject
	 * @return
	 */
	public boolean setSubject(String mailSubject) {
		log.info("设置邮件主题！");
		try {
			mimeMsg.setSubject(mailSubject);
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * 设置邮件正文
	 * 
	 * @param mailBody
	 *            String
	 */
	public boolean setBody(String mailBody) {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setContent("" + mailBody, "text/html;charset=utf-8");
			mp.addBodyPart(bp);

			return true;
		} catch (Exception e) {
			log.error("设置邮件正文时发生错误！", e);
			return false;
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param filename
	 *            String
	 */
	public boolean addFileAffix(String filename) {

		log.info("增加邮件附件：" + filename);
		try {
			BodyPart bp = new MimeBodyPart();
			FileDataSource fileds = new FileDataSource(filename);
			bp.setDataHandler(new DataHandler(fileds));
			bp.setFileName(fileds.getName());

			mp.addBodyPart(bp);

			return true;
		} catch (Exception e) {
			log.error("增加邮件附件：" + filename + "发生错误！", e);
			return false;
		}
	}

	/**
	 * 设置发信人
	 * 
	 * @param from
	 *            String
	 */
	public boolean setFrom(String from) {
		log.info("设置发信人！");
		try {
			String nick = "新致云";
//			if(from.contains("test")){
//				nick=ResultMessageServer.getMessage("NEWTOUCH_MAIL_NAME_TEST", null, language);
//			}else{
//				nick=ResultMessageServer.getMessage("NEWTOUCH_MAIL_NAME", null, language);
//			}
			nick=javax.mail.internet.MimeUtility.encodeText(nick);
			mimeMsg.setFrom(new InternetAddress(nick+"< "+from+">")); // 设置发信人
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/**
	 * 设置收信人
	 * 
	 * @param to
	 *            String
	 */
	public boolean setTo(String to) {
		if (to == null){
			return false;
		}
		try {
			mimeMsg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/**
	 * 设置抄送人
	 * 
	 * @param copyto
	 *            String
	 */
	public boolean setCopyTo(String copyto) {
		if (copyto == null){
			return false;
		}
		try {
			mimeMsg.setRecipients(Message.RecipientType.CC,
					(Address[]) InternetAddress.parse(copyto));
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/**
	 * 发送邮件
	 */
	@SuppressWarnings("static-access")
	public boolean sendOut() {
		Transport transport = null;
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			//设置优先级
			//mimeMsg.setHeader("X-Priority", "1");
			log.info("正在发送邮件....");
			String host = (String) props.get(mailSmtp);
			Authenticator mailAuth=null;			  
			if (host.equals("mail.newtouch.cn")) {

			} else if (host.equals("mail.newtouch.com")) {
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.port", "465");
				props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				props.put("mail.smtp.starttls.enable","true");
				mailAuth=new MailAuthenticator(username,password);
			} else{
				props.put("mail.smtp.socketFactory.port", "465");
				props.put("mail.smtp.port", "465");
				props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				props.put("mail.smtp.starttls.enable","true");
				mailAuth=new MailAuthenticator(username,password);
			}
			Session mailSession = Session.getInstance(props, mailAuth);

			transport = mailSession.getTransport("smtp");
			transport.connect((String) props.get(mailSmtp), username,
					password);
			//transport.connect();
			transport.sendMessage(mimeMsg,
					mimeMsg.getRecipients(Message.RecipientType.TO));

			log.info("发送邮件成功！");

			return true;
		} catch (Exception e) {
			log.info("邮件发送失败！", e);
		//	e.printStackTrace();
			return false;
		} finally {
			try {
				transport.close();
			} catch (MessagingException e) {
				log.info("邮件发送失败！", e);
		//		e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * 调用sendOut方法完成邮件发送
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public static boolean send(String smtp, String from, String to,
			String subject, String content, String username, String password) {
		Mail theMail = new Mail(smtp);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(subject)){
			return false;
		}
		if (!theMail.setBody(content)){
			return false;
		}
		if (!theMail.setTo(to)){
			return false;
		}

		if (!theMail.setFrom(from)){
			return false;
		}
		theMail.setNamePass(username, password);

		if (!theMail.sendOut()){
			return false;
		}
		return true;
	}

	/**
	 * 调用sendOut方法完成邮件发送,带抄送
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param copyto
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public static boolean sendAndCc(String smtp, String from, String to,
			String copyto, String subject, String content, String username,
			String password,String language) {
		Mail theMail = new Mail(smtp);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(subject)){
			return false;
		}
		if (!theMail.setBody(content)){
			return false;
		}
		if (!theMail.setTo(to)){
			return false;
		}
		if (!theMail.setCopyTo(copyto)){
			return false;
		}
		if (!theMail.setFrom(from)){
			return false;
		}
		theMail.setNamePass(username, password);

		if (!theMail.sendOut()){
			return false;
		}
		return true;
	}

	/**
	 * 调用sendOut方法完成邮件发送,带附件
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @param filename
	 *            附件路径
	 * @return
	 */
	public static boolean send(String smtp, String from, String to,
			String subject, String content, String username, String password,
			String filename,String language) {
		Mail theMail = new Mail(smtp);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(subject)){
			return false;
		}
		if (!theMail.setBody(content)){
			return false;
		}
		if (!theMail.addFileAffix(filename)){
			return false;
		}
		if (!theMail.setTo(to)){
			return false;
		}
		if (!theMail.setFrom(from)){
			return false;
		}
		theMail.setNamePass(username, password);

		if (!theMail.sendOut()){
			return false;
		}
		return true;
	}

	/**
	 * 调用sendOut方法完成邮件发送,带附件和抄送
	 * 
	 * @param smtp
	 * @param from
	 * @param to
	 * @param copyto
	 * @param subject
	 * @param content
	 * @param username
	 * @param password
	 * @param filename
	 * @return
	 */
	public static boolean sendAndCc(String smtp, String from, String to,
			String copyto, String subject, String content, String username,
			String password, String filename,String language) {
		Mail theMail = new Mail(smtp);
		theMail.setNeedAuth(true); // 需要验证

		if (!theMail.setSubject(subject)){
			return false;
		}
		if (!theMail.setBody(content)){
			return false;
		}
		if (!theMail.addFileAffix(filename)){
			return false;
		}
		if (!theMail.setTo(to)){
			return false;
		}
		if (!theMail.setCopyTo(copyto)){
			return false;
		}
		if (!theMail.setFrom(from)){
			return false;
		}
		theMail.setNamePass(username, password);

		if (!theMail.sendOut()){
			return false;
		}
		return true;
	}

	class MailAuthenticator extends Authenticator {

		private String userName;
		private String password;

		public MailAuthenticator(String userName, String password) {
			super();
			this.userName = userName;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password);
		}
	}

}
