package wuzm.android.kframe.email;


/**
 * current only support SMTP protocol
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 * @see
 * <ul>
 * <li><strong>must use {@link #setServerHost(String)} , {@link #setUserName(String)} , {@link #setPassword(String)}</strong></li>
 * <li><strong>Some Email Server Imformation :</strong></li>
 * <ul>
 * <li><strong>QQ Email</strong>  host: smtp.qq.com;port: 465 or 587</li>
 * </ul>
 * </ul>
 */
public class EmailSenderConfig {
	//mail server
	private String serverHost;
	private int serverPort;
	
	//user
	private String userName;
	private String password;
	
	private boolean isValid = true;
	
	public EmailSenderConfig setServerHost(String host) {
		this.serverHost = host;
		return this;
	}
	
	public String getServerHost() {
		return this.serverHost;
	}
	
	public EmailSenderConfig setServerPort(int port) {
		this.serverPort = port;
		return this;
	}
	
	public int getServerPort() {
	    return this.serverPort;
	}
	
	public EmailSenderConfig setUserName(String userName) {
		this.userName = userName;
		return this;
	}
	
	public String getUsername() {
		return this.userName;
	}
	
	public EmailSenderConfig setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public EmailSenderConfig setIsValid(boolean isValid) {
		this.isValid = isValid;
		return this;
	}
	
	public boolean isValid() {
		return this.isValid;
	}
}
