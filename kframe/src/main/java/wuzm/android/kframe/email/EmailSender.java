package wuzm.android.kframe.email;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * A Simple EmailSender
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 */

public class EmailSender {
	private EmailSenderConfig mConfig;
	
	private Properties mProps;
	private MyAuthenticator mAuthenticator;
	private Session mSession;
	private Transport mTransport;
	
	private Address fromAddress;
	private ArrayList<Address> toAddress;
	private ArrayList<Address> copyAddress;
	
	private class MyAuthenticator extends Authenticator {
		private String userName;
		private String password;
		
		public MyAuthenticator(String userName,String password) {
			this.userName = userName;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.userName, this.password);
		}
		
	}
	
	private EmailSender() {
	}
	
	/**
	 * @param config
	 * @return
	 * @see <strong>read the EmailSenderConfig Class</strong>
	 */
	public static EmailSender create(EmailSenderConfig config) {
		EmailSender sender = new EmailSender();
		sender.mConfig = config;
		return sender;
	}
	
	public void init() {
		toAddress = new ArrayList<Address>();
		copyAddress = new ArrayList<Address>();
		mProps = new Properties();
		mProps.put("mail.smtp.host", mConfig.getServerHost());
		mProps.put("mail.smtp.port", mConfig.getServerPort());
		mProps.put("mail.smtp.auth", mConfig.isValid() ? "true" : "false"); 
		mAuthenticator = new MyAuthenticator(mConfig.getUsername(), mConfig.getPassword());
		mSession = Session.getInstance(mProps, mAuthenticator);
		
	    try {
			mTransport = mSession.getTransport("smtp");
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
	
	public void setFromAddress(String addr) {
			try {
				fromAddress = new InternetAddress(addr);
			} catch (AddressException e) {
				e.printStackTrace();
			}
	}
	
	public void sendMsg(ArrayList<String> toAddrs,String title,String content) {
		sendMsg(toAddrs, null, title, content);
	}
	
	public void sendMsg(String toAddr,String title,String content) {
		ArrayList<String> toAddrs = new ArrayList<String>();
		toAddrs.add(toAddr);
		sendMsg(toAddrs,null, title, content);
	}
	
	public void sendMsg(ArrayList<String> toAddrs,ArrayList<String> copyAddrs,String title,String content) {
		try {
			for(int i = 0 ; i < toAddrs.size() ; i ++) {
				toAddress.add( new InternetAddress(toAddrs.get(i)) );
			}
			if(copyAddrs != null) {
				for(int i = 0 ; i < copyAddrs.size() ; i ++ ) {
					copyAddress.add( new InternetAddress(copyAddrs.get(i)) );
				}
			}
		} catch (AddressException e1) { 
			e1.printStackTrace();
		}
		MimeMessage msg = new MimeMessage(mSession);
		try {
			msg.setSubject(title);
			msg.setFrom(fromAddress);
			for(int i = 0 ; i < toAddrs.size() ; i ++ ) {
				msg.addRecipient(RecipientType.TO, toAddress.get(i));
			}
			if(copyAddrs != null) { 
				for(int i = 0 ; i < copyAddrs.size() ; i ++ ) {
					msg.addRecipient(RecipientType.CC, copyAddress.get(i));
				}
			}
			msg.setContent(content,"text/html");
			msg.saveChanges();
			mTransport.connect(mConfig.getServerHost(),mConfig.getUsername(),
					mConfig.getPassword());
			mTransport.send(msg);
			mTransport.close();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendAttachment(ArrayList<String> toAddrs,String title,ArrayList<File> files) {
		sendAttachment(toAddrs, null, title, files);
	}
	
	public void sendAttachment(String toAddr,String title,ArrayList<File> files) {
		ArrayList<String> toAddrs = new ArrayList<String>();
		toAddrs.add(toAddr);
		sendAttachment(toAddrs, null, title, files);
	}
	
	public void sendAttachment(ArrayList<String> toAddrs,ArrayList<String> copyAddrs,String title,ArrayList<File> files) {
		try {
			for(int i = 0 ; i < toAddrs.size() ; i ++) {
				toAddress.add( new InternetAddress(toAddrs.get(i)) );
			}
			if(copyAddrs != null) {
				for(int i = 0 ; i < copyAddrs.size() ; i ++ ) {
					copyAddress.add( new InternetAddress(copyAddrs.get(i)) );
				}
			}
		} catch (AddressException e1) { 
			e1.printStackTrace();
		}
		MimeMessage msg = new MimeMessage(mSession);
		try {
			msg.setSubject(title);
			msg.setSentDate(new Date());
			msg.setFrom(fromAddress);
			for(int i = 0 ; i < toAddrs.size() ; i ++ ) {
				msg.addRecipient(RecipientType.TO, toAddress.get(i));
			}
			if(copyAddrs != null) { 
				for(int i = 0 ; i < copyAddrs.size() ; i ++ ) {
					msg.addRecipient(RecipientType.CC, copyAddress.get(i));
				}
			}
			Multipart attachPart = new MimeMultipart();
			if(files != null) {
				for (File file : files) {
					MimeBodyPart attachBody = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					attachBody.setDataHandler(new DataHandler(source));
					attachBody.setFileName(file.getName());
					attachPart.addBodyPart(attachBody);
				}
			}
			msg.setContent(attachPart);
			msg.saveChanges();
			mTransport.connect(mConfig.getServerHost(),mConfig.getUsername(),
					mConfig.getPassword());
			mTransport.send(msg);
			mTransport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void send(ArrayList<String> toAddrs,String title,String content,ArrayList<File> files) {
		send(toAddrs, null, title, content, files);
	}
	
	public void send(String toAddr,String title,String content,ArrayList<File> files) {
		ArrayList<String> toAddrs = new ArrayList<String>();
		toAddrs.add(toAddr);
		send(toAddrs,null,title,content,files);
	}
	
	public void send(ArrayList<String> toAddrs,ArrayList<String> copyAddrs,String title,String content,ArrayList<File> files) {
		try {
			for(int i = 0 ; i < toAddrs.size() ; i ++) {
				toAddress.add( new InternetAddress(toAddrs.get(i)) );
			}
			if(copyAddrs != null) {
				for(int i = 0 ; i < copyAddrs.size() ; i ++ ) {
					copyAddress.add( new InternetAddress(copyAddrs.get(i)) );
				}
			}
		} catch (AddressException e1) { 
			e1.printStackTrace();
		}
		MimeMessage msg = new MimeMessage(mSession);
		try {
			msg.setSubject(title);
			msg.setSentDate(new Date());
			msg.setFrom(fromAddress);
			for(int i = 0 ; i < toAddrs.size() ; i ++ ) {
				msg.addRecipient(RecipientType.TO, toAddress.get(i));
			}
			if(copyAddrs != null) { 
				for(int i = 0 ; i < copyAddrs.size() ; i ++ ) {
					msg.addRecipient(RecipientType.CC, copyAddress.get(i));
				}
			}
			Multipart mulPort = new MimeMultipart();
			
			//text/html body
			MimeBodyPart contentPart =  new MimeBodyPart();
			contentPart.setContent(content, "text/html");
			mulPort.addBodyPart(contentPart);
			
			//attachment body
			if(files != null) {
				for(File file : files) {
					MimeBodyPart attachPort = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					attachPort.setDataHandler(new DataHandler(source));
					attachPort.setFileName(file.getName());
					mulPort.addBodyPart(attachPort);
				}
			}
			msg.setContent(mulPort);
			msg.saveChanges();
			
			mTransport.connect(mConfig.getServerHost(),mConfig.getUsername(),
					mConfig.getPassword());
			mTransport.send(msg);
			mTransport.close();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
}
