package wuzm.android.kdownload.bean.factory;


import java.util.ArrayList;

import wuzm.android.kdownload.bean.DownloadBlockBean;
import wuzm.android.kdownload.bean.TaskBean;

public class TaskBeanFactory {
       
	public static TaskBean create(ArrayList<DownloadBlockBean> beans) {
		return new TaskBean(beans);
	}
	
	public static TaskBean create(String url,String dir,String fileName,String fileSuffix) {
		return new TaskBean(url, dir + "/" + fileName + "." + fileSuffix, fileSuffix);
	}
	
	public static TaskBean create(String url,String dir,String fileName,String fileSuffix,
			long taskSize) {
		return new TaskBean(url, dir, fileName, fileSuffix, taskSize);
	}
}
