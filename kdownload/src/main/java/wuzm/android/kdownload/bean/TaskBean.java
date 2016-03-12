package wuzm.android.kdownload.bean;

import java.util.ArrayList;


public class TaskBean {
	   
	   private static final String TEMP = ".temp";
	   //if the task size is smaller than SIZE_MUL_THREAD_CONDITION ,Do not need to separate block
	   public static long SIZE_MUL_THREAD_CONDITION = 4 * 1024 * 1024; //bytes
	   public static int MAX_BLOCK_COUNT = 2;
       private ArrayList<DownloadBlockBean> blockBeans;
       private int blockCount;
       private long taskSize;
       private long downloadedSize;
       private String url;
       private String savePath;
       private String fileSuffix;
       
       public TaskBean(ArrayList<DownloadBlockBean> blockBeans) {
			DownloadBlockBean blockBean = blockBeans.get(0);
			String tmpPath = blockBean.getFilePath();
			
			
			this.url = blockBean.getUrl();
			this.fileSuffix = blockBean.getFileSuffix();
			this.taskSize = blockBean.getTaskSize();
			this.blockCount = blockBeans.size();
			this.blockBeans = blockBeans;
			for(int i = 0 , len = blockBeans.size() ; i < len ; i ++) {
				this.downloadedSize += blockBeans.get(i).getDownloadedSize();
			}
			String taskfilePath = tmpPath.substring(0, tmpPath.indexOf( "." + this.fileSuffix)
					+ this.fileSuffix.length() + 1 );
			this.savePath = taskfilePath;
       }
       
       public TaskBean(String url, String savePath, String fileSuffix) {
    	   this.url = url;
    	   this.savePath = savePath;
    	   this.fileSuffix = fileSuffix;
       }
       
       public TaskBean(String url, String saveDir, String fileName, String fileSuffix ,
    		   long taskSize) {
    	   this.url = url;
    	   this.savePath = saveDir + "/" + fileName + "." + fileSuffix;
    	   this.fileSuffix = fileSuffix;
    	   this.taskSize = taskSize;
    	   resolveBlock(taskSize);
       }
       
       public void resolveBlock(long taskSize) {
    	   
		   ArrayList<DownloadBlockBean> blocks = new ArrayList<DownloadBlockBean>();
		   
		   if(taskSize <= SIZE_MUL_THREAD_CONDITION) {
			   //Do not need to separate block
			   DownloadBlockBean block = new DownloadBlockBean(url, this.savePath + "_" + "1",
					   0, taskSize,taskSize);
			   block.setCurPos(0);
			   block.setDownloadedSize(0);
			   block.setFilePath(block.getId() + TEMP);
			   block.setFileSuffix(fileSuffix);
			   blocks.add(block);
			   this.blockCount = 1;
			   this.downloadedSize = 0;
			   this.blockBeans = blocks;
		   }else if(taskSize < SIZE_MUL_THREAD_CONDITION * MAX_BLOCK_COUNT) {
			   //separate block that count not lager than MAX_BLOCK_COUNT 
			   //and every block size not larger than SIZE_MUL_THREAD_CONDITION
			   int blockCount =  (int) (taskSize / SIZE_MUL_THREAD_CONDITION) + 
					   (taskSize % SIZE_MUL_THREAD_CONDITION == 0 ? 0 : 1);
			   DownloadBlockBean block = null;
			   for(int i = 0 ; i < blockCount ; i ++ ) {
				   if( i != blockCount -1 || taskSize % SIZE_MUL_THREAD_CONDITION == 0) {
					   block = new DownloadBlockBean(url, this.savePath + "_" + i + TEMP ,
							   i * SIZE_MUL_THREAD_CONDITION,taskSize, SIZE_MUL_THREAD_CONDITION);
				   }else {
					   block = new DownloadBlockBean(url, this.savePath + "_" + i + TEMP,
							   i * SIZE_MUL_THREAD_CONDITION,taskSize, taskSize % SIZE_MUL_THREAD_CONDITION);
				   }
				   block.setCurPos(0);
				   block.setDownloadedSize(0);
				   block.setFilePath(block.getId() + TEMP);
				   block.setFileSuffix(fileSuffix);
				   blocks.add(block);
			   }
			   this.blockCount = blockCount;
			   this.downloadedSize = 0;
			   this.blockBeans = blocks;
		   }else {
			   //separete block that count equal MAX_BLOCK_COUNT
			   //and block size allow larger than SIZE_MUL_THREAD_CONDITION
			   long maxBlockSize = (taskSize / MAX_BLOCK_COUNT) + 
					   (taskSize % MAX_BLOCK_COUNT == 0 ? 0 : 1);
			   DownloadBlockBean block = null;
			   long tmpSize = taskSize;
			   for(int i = 0 ; tmpSize > 0 ; i ++ ) {
				   
				   if(tmpSize >= maxBlockSize) {
					   block = new DownloadBlockBean(url, this.savePath + "_" + i + TEMP,
							   i * maxBlockSize, taskSize, maxBlockSize);
				   }else if(tmpSize > 0) {
					   block = new DownloadBlockBean(url, this.savePath + "_" + i + TEMP,
							   i * maxBlockSize, taskSize, tmpSize);
				   }
				   block.setCurPos(0);
				   block.setDownloadedSize(0);
				   block.setFilePath(block.getId() + TEMP);
				   block.setFileSuffix(fileSuffix);
				   blocks.add(block);
				   tmpSize -= maxBlockSize;
			   }
			   this.blockCount = blocks.size();
			   this.downloadedSize = 0;
			   this.blockBeans = blocks; 
		   }
       }
       
       public void setDownloadedSize(long downloadedSize) {
    	   this.downloadedSize = downloadedSize;
       }
       
       public long getDownloadedSize() {
    	   return downloadedSize;
       }
       
       public String getUrl() {
    	   return url;
       }
       
       public String getSavePath() {
    	   return savePath;
       }
       
       public void setFileSuffix(String suffix) {
    	   this.fileSuffix = suffix;
       }
       
       public String getFileSuffix() {
    	   return this.fileSuffix;
       }
       
       public int getBlockCount() {
    	   return blockCount;
       }
       
       public long getTaskSize() {
    	   return taskSize;
       }
       
       public void setTaskSize(long size) {
    	   if(size <= 0) {
    		   throw new IllegalArgumentException("size must bigger than 0");
    	   }
    	   this.taskSize = size;
       }
       
       
       
       public ArrayList<DownloadBlockBean> getBlockBeans() {
    	   return blockBeans;
       }
       
       public DownloadBlockBean getBlockBean(int pos) {
    	   try {
    		   return blockBeans == null ? null : blockBeans.get(pos);
    	   }catch(ArrayIndexOutOfBoundsException e) {
    		   e.printStackTrace();
    		   return null;
    	   }
       }
       
       public DownloadBlockBean getBlockBean(String id) {
    	   try {
        	   for(DownloadBlockBean bean : blockBeans) {
        		   if(id.equals(bean.getId())) {
        			   return bean;
        		   }
        	   } 
    	   }catch(NullPointerException e) {
    		   e.printStackTrace();
    		   return null;
    	   }
    	   return null;
       }
       
       public  void setMaxBlockCount(int count) {
    	   if(count <= 0) {
    		   throw new IllegalArgumentException("count must bigger than 0");
    	   }
    	   TaskBean.MAX_BLOCK_COUNT = count;
       }
       
       /**
        * 设置采用多线程下载的文件大小界限值,只有待下载的文件的大小大于设定的值的时候才会采用多线程
        * @param size
        */
       public  void setSizeMulThreadCondition(long size) {
    	   if(size <= 0) {
    		   throw new IllegalArgumentException("size must bigger than 0");
    	   }
    	   TaskBean.SIZE_MUL_THREAD_CONDITION = size;
       }
}
