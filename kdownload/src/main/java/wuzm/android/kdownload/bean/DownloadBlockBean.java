package wuzm.android.kdownload.bean;

public class DownloadBlockBean {
	   
       private long startPos;
       private long curPos;
       private long taskSize;
       private long blockSize;
       private long downloadedSize;
       private String filePath;
       private String fileSuffix;
       private String id;
       private String url;
       
       public DownloadBlockBean(String url,String id,long startPos,long taskSize,long blockSize) {
    	   this.url = url;
    	   this.id = id;
    	   this.startPos = startPos;
    	   this.taskSize = taskSize;
    	   this.blockSize = blockSize;
       }
       
       public void setCurPos(long pos) {
    	   this.curPos = pos;
       }
       
       public long getCurPos() {
    	   return this.curPos;
       }
       
       public long getStartPos() {
    	   return this.startPos;
       }
       
       public void setDownloadedSize(long size) {
    	   this.downloadedSize = size;
       }
       
       public long getDownloadedSize() {
    	   return this.downloadedSize;
       }
       
       public long getTaskSize() {
    	   return this.taskSize;
       }
       
       public long getBlockSize() {
    	   return this.blockSize;
       }
       
       public String getUrl() {
    	   return this.url;
       }
       
       public String getId() {
    	   return this.id;
       }
       
       public void setFilePath(String path) {
    	   this.filePath = path;
       }
       
       public String getFilePath() {
    	   return this.filePath;
       }
       
       public void setFileSuffix(String suffix) {
    	   this.fileSuffix = suffix;
       }
       
       public String getFileSuffix() {
    	   return this.fileSuffix;
       }
}

