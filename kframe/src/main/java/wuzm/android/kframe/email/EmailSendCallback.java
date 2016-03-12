package wuzm.android.kframe.email;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public interface EmailSendCallback {
	   public void startSend();
	   public void onSend();
       public void onSendSuccess();
       public void onSendFaild();
       public void onSendCancel();
}
