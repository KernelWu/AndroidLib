package com.example.kernel.khttp.callback;

public interface UploadFileCallback {
	
	public void uploadStart();
	public void uploading(int sucFileCount, int faiFileCount, int totFileCount);
	public void uploadComplete(int sucFileCount, int faiFileCount, int totFileCount);
	public void uploadCancel(int sucFileCount, int faiFileCount, int totFileCount);

}
