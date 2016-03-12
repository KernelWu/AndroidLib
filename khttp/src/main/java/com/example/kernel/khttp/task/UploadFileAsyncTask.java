package com.example.kernel.khttp.task;

import android.os.AsyncTask;

import com.example.kernel.khttp.callback.UploadFileCallback;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UploadFileAsyncTask extends AsyncTask<Void, Void, Void>{
	
	private File[] uploadFiles;
	private String urlS;
	private String fileMark;
	private int filesCount;
	private int sucFilesCount;
	private int faiFilesCount;
	private UploadFileCallback mCallback;
	
	/**
	 * 
	 * @param files
	 * @param url
	 * @param fileMark   服务端规定的文件上传的标识文件的字符串
	 * @param callback
	 */
	public UploadFileAsyncTask(File[] files,String url,String fileMark,UploadFileCallback callback) {
		this.uploadFiles = files;
		this.mCallback = callback;
		this.urlS = url;
		this.fileMark = fileMark;
		this.faiFilesCount = 0;
		this.sucFilesCount = 0;
		this.filesCount = files.length;
	}

	@Override
	protected void onPreExecute() {
		if(mCallback != null) {
			mCallback.uploadStart();
		}
 		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
		
		for(int i = 0 ; i < filesCount ; i ++ ) {
			
			File f = uploadFiles[i];
	        try
	        {
	          URL url =new URL(this.urlS);
	          HttpURLConnection con=(HttpURLConnection)url.openConnection();
	          /* 允许Input、Output，不使用Cache */
	          con.setDoInput(true);
	          con.setDoOutput(true);
	          con.setUseCaches(false);
	          /* 设置传送的method=POST */
	          con.setRequestMethod("POST");
	          /* setRequestProperty */
	          con.setRequestProperty("Connection", "Keep-Alive");
	          con.setRequestProperty("Charset", "UTF-8");
	          con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
	          con.setRequestProperty("Content-Type",
	                             "multipart/form-data;boundary="+boundary);
	          /* 设置DataOutputStream */
	          try {
	        	  con.connect();
	          }catch(Exception e) {
	        	  e.printStackTrace();
	          }
	          DataOutputStream ds =
	            new DataOutputStream(con.getOutputStream());
	          ds.writeBytes(twoHyphens + boundary + end);
	          ds.writeBytes("Content-Disposition: form-data; "+
	                        "name=\"" + fileMark +  "\";filename=\""+
	                        f.getName() +"\""+ end);
	          ds.writeBytes(end);  
	          /* 取得文件的FileInputStream */
	          FileInputStream fStream =new FileInputStream(f);
	          /* 设置每次写入1024bytes */
	          int bufferSize =1024;
	          byte[] buffer =new byte[bufferSize];
	          int length =-1;
	          /* 从文件读取数据至缓冲区 */
	          while((length = fStream.read(buffer)) !=-1)
	          {
	            /* 将资料写入DataOutputStream中 */
	            ds.write(buffer, 0, length);
	          }
	          ds.writeBytes(end);
	          ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
	          /* close streams */
	          fStream.close();
	          ds.flush();
	          /* 取得Response内容 */
	          InputStream is = con.getInputStream();
	          int ch;
	          StringBuffer b =new StringBuffer();
	          while( ( ch = is.read() ) !=-1 )
	          {
	            b.append( (char)ch );
	          }
	          /* 将Response显示于Dialog */
	          /* 关闭DataOutputStream */
	          ds.close();
	          sucFilesCount ++;
	        }catch(Exception e) {
	        	e.printStackTrace();
	        	faiFilesCount ++;
	        }finally {
	        	if(i != filesCount - 1) {
//	        		onProgressUpdate();
	        	}
	        }
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(mCallback != null) {
			mCallback.uploadComplete(sucFilesCount, faiFilesCount, filesCount);
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		if(mCallback != null) {
			mCallback.uploading(sucFilesCount, faiFilesCount, filesCount);
		}
	 }

	@Override
	protected void onCancelled(Void result) {
//		super.onCancelled(result);
//		if(mCallback != null ) {
//			mCallback.uploadCancel(sucFilesCount, faiFilesCount, filesCount);
//		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if(mCallback != null ) {
			mCallback.uploadCancel(sucFilesCount, faiFilesCount, filesCount);
		}
	}

}
