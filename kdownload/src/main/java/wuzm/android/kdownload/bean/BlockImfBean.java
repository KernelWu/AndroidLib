package wuzm.android.kdownload.bean;

public class BlockImfBean {
	   
	public static final int STATE_CONNECT_FAILD = 0x11;
	public static final int STATE_CREATE_TMP_FILE_FAILD = 0x12;
	public static final int STATE_OTHER_ERROR = 0x13;
	public static final int STATE_DOWNLOADING = 0x10;
	public static final int STATE_DOWNLOAD_OVER = 0x9;
	public static final int STATE_DOWNLOAD_PAUSE = 0x8;
	
	public String id;
	public int state;
	public long increaceSize;
	public long offset;
	public long downloadSize;
}
