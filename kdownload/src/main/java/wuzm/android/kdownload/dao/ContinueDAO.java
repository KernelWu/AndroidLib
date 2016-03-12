package wuzm.android.kdownload.dao;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import wuzm.android.kdownload.bean.DownloadBlockBean;
import wuzm.android.kdownload.bean.TaskBean;
import wuzm.android.kdownload.bean.factory.TaskBeanFactory;

public class ContinueDAO {
	
	private static final String TAG = ContinueDAO.class.getSimpleName();
	private Context mContext;
	private int version;
	
	public ContinueDAO(Context context,int version) {
		this.mContext = context;
		this.version = version;
	}
	
	/*downloaded bytes*/
	public long readDownloadedBytes(String id) {
		if(TextUtils.isEmpty(id)) {
			return 0;
		}
		
		try {
		    Cursor cursor = ContinueDB.getInstance(mContext, version).getReadDb().
		    		query(ContinueDbHelper.TABLE_NAME,
		    		new String[] {DbColumnConstans.DOWNLOADED_SIZE}, "id=?", new String[] {id},
		    		null, null, null);
			return cursor.getLong(cursor.getColumnIndex(DbColumnConstans.DOWNLOADED_SIZE));
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	/**
	 * 
	 * @param id
	 * @param offSet the current offset
	 * @param bytes the downloaded size
	 * @return
	 */
	public boolean writeDownloadedBytes(String id,long offSet,long bytes) {
		if(TextUtils.isEmpty(id)) {
			return false;
		}
		try {
			ContentValues values = new ContentValues();
			values.put(DbColumnConstans.CUR_POS, offSet);
			values.put(DbColumnConstans.DOWNLOADED_SIZE, bytes);
			return ContinueDB.getInstance(mContext, version).getWriteDb().
			           update(ContinueDbHelper.TABLE_NAME, values, 
			        		   "id=?", new String[] {id}) > 0 ? true : false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*record*/
	public RecordValue readRecord(String id) {
		if(TextUtils.isEmpty(id)) {
			return null;
		}
		try {
			Cursor cursor = ContinueDB.getInstance(mContext, version).getReadDb().
					query(ContinueDbHelper.TABLE_NAME,
							null,DbColumnConstans.ID + "=?",
							new String[] {id},null, null, null,"1");
			RecordValue value = new RecordValue();
			value.id = cursor.getString(cursor.getColumnIndex(DbColumnConstans.ID));
			value.url = cursor.getString(cursor.getColumnIndex(DbColumnConstans.URL));
			value.filePath = cursor.getString(cursor.getColumnIndex(DbColumnConstans.FILE_PATH));
			value.fileSuffix = cursor.getString(cursor.getColumnIndex(DbColumnConstans.FILE_SUFFIX));
			value.startPos = cursor.getLong(cursor.getColumnIndex(DbColumnConstans.START_POS));
			value.curPos = cursor.getLong(cursor.getColumnIndex(DbColumnConstans.CUR_POS));
			value.taskSize = cursor.getLong(cursor.getColumnIndex(DbColumnConstans.TASK_SIZE));
			value.blockSize = cursor.getLong(cursor.getColumnIndex(DbColumnConstans.BLOCK_SIZE));
			value.downloadedSize = cursor.getLong(cursor.getColumnIndex(DbColumnConstans.DOWNLOADED_SIZE));
			cursor.close();
			return value;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean addRecord(RecordValue value) {
		if(value == null) {
			return false;
		}
		try {
			ContentValues cv = new ContentValues();
			cv.put(DbColumnConstans.ID, value.id);
			cv.put(DbColumnConstans.URL, value.url);
			cv.put(DbColumnConstans.FILE_PATH, value.filePath);
			cv.put(DbColumnConstans.FILE_SUFFIX, value.filePath);
			cv.put(DbColumnConstans.START_POS, value.startPos);
			cv.put(DbColumnConstans.CUR_POS, value.curPos);
			cv.put(DbColumnConstans.TASK_SIZE, value.taskSize);
			cv.put(DbColumnConstans.BLOCK_SIZE, value.blockSize);
			cv.put(DbColumnConstans.DOWNLOADED_SIZE, value.downloadedSize);
			return ContinueDB.getInstance(mContext, version).getWriteDb().
			          insert(ContinueDbHelper.TABLE_NAME, null, cv) == -1 ? false : true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addRecords(ArrayList<RecordValue> values) {
		if(values == null || values.size() == 0) {
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(ContinueDbHelper.TABLE_NAME);
		sb.append("(");
		sb.append(DbColumnConstans.ID);
		sb.append(",");
		sb.append(DbColumnConstans.URL);
		sb.append(",");
		sb.append(DbColumnConstans.FILE_PATH);
		sb.append(",");
		sb.append(DbColumnConstans.FILE_SUFFIX);
		sb.append(",");
		sb.append(DbColumnConstans.START_POS);
		sb.append(",");
		sb.append(DbColumnConstans.CUR_POS);
		sb.append(",");
		sb.append(DbColumnConstans.TASK_SIZE);
		sb.append(",");
		sb.append(DbColumnConstans.BLOCK_SIZE);
		sb.append(",");
		sb.append(DbColumnConstans.DOWNLOADED_SIZE);
		sb.append(")");
		sb.append(" values(?,?,?,?,?,?,?,?,?)");
		try {
			SQLiteStatement sqls = ContinueDB.getInstance(mContext, version).getWriteDb().
					compileStatement(sb.toString());
			        ContinueDB.getInstance(mContext, version).getWriteDb().beginTransaction();
			        RecordValue value = null;
			for (int i = 0, len = values.size(); i < len; i++) {
				value = values.get(i);
				sqls.bindString(1, value.id);
				sqls.bindString(2, value.url);
				sqls.bindString(3, value.filePath);
				sqls.bindString(4, value.fileSuffix);
				sqls.bindLong(5, value.startPos);
				sqls.bindLong(6, value.curPos);
				sqls.bindLong(7, value.taskSize);
				sqls.bindLong(8, value.blockSize);
				sqls.bindLong(9, value.downloadedSize);
				sqls.executeInsert();
			}
			ContinueDB.getInstance(mContext, version).getWriteDb()
					.setTransactionSuccessful();
			ContinueDB.getInstance(mContext, version).getWriteDb()
					.endTransaction();
			return true;
			        
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteRecord(String id) {
		try {
			return ContinueDB.getInstance(mContext, version).getWriteDb().
			           delete(ContinueDbHelper.TABLE_NAME, DbColumnConstans.ID + "=", new String[] {id})
			           > 0 ? true : false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*task*/
	public boolean isExistTask(String url) {
		if(TextUtils.isEmpty(url)) {
			return false;
		}
		try {
			Cursor cursor = ContinueDB.getInstance(mContext, version).getReadDb().
			        query(ContinueDbHelper.TABLE_NAME, new String[] {DbColumnConstans.ID},
			        		DbColumnConstans.URL + "=?",new String[] {url},
			        		null, null, null, "1");
			return cursor.getCount() > 0 ? true : false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public TaskBean readTask(String url) {
		if(TextUtils.isEmpty(url)) {
			return null;
		}
		
		TaskBean taskBean = null;
		ArrayList<DownloadBlockBean> blocks = new ArrayList<DownloadBlockBean>();
		
		try {
			Cursor cursor = ContinueDB.getInstance(mContext, version).getReadDb().
	                  query(ContinueDbHelper.TABLE_NAME,
	                   null,DbColumnConstans.URL + "=?", new String[] {url},
	                   null, null, null);
			DownloadBlockBean block = null;
			
			for(int i = 0 , len = cursor.getColumnCount() ; i < len ; i ++ ) {
				Log.d("DAO",cursor.getColumnName(i) + ":" + cursor.getColumnName(i));
			}
			if(cursor.getCount() == 0) {
				return null;
			}
			for(int i = 0 , len = cursor.getCount() ; i < len; i ++) {
				cursor.moveToPosition(i);
				block = new DownloadBlockBean(url, cursor.getString(cursor.getColumnIndex(DbColumnConstans.ID)),
						cursor.getLong(cursor.getColumnIndex(DbColumnConstans.START_POS)),
						cursor.getLong(cursor.getColumnIndex(DbColumnConstans.TASK_SIZE)),
						cursor.getLong(cursor.getColumnIndex(DbColumnConstans.BLOCK_SIZE)));
				block.setCurPos(cursor.getLong(cursor.getColumnIndex(DbColumnConstans.CUR_POS)));
				block.setDownloadedSize(cursor.getLong(cursor.getColumnIndex(DbColumnConstans.DOWNLOADED_SIZE)));
				block.setFilePath(cursor.getString(cursor.getColumnIndex(DbColumnConstans.FILE_PATH)));
				block.setFileSuffix(cursor.getString(cursor.getColumnIndex(DbColumnConstans.FILE_SUFFIX)));
				blocks.add(block);
			}
			cursor.close();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		taskBean = TaskBeanFactory.create(blocks);
		return taskBean;
	}
	
	@SuppressLint("NewApi")
	public ArrayList<TaskBean> readTasks() {
		try {
			Cursor cursor = ContinueDB.getInstance(mContext, version).getReadDb().
					query(true, ContinueDbHelper.TABLE_NAME,
							new String[] {DbColumnConstans.URL},
							null, null, null, null, null, null, null);
			if(cursor.getCount() == 0) {
				return null;
			}
			ArrayList<String> urls = new ArrayList<String>();
			for(int i = 0 , len = cursor.getCount() ; i < len ; i ++ ) {
				cursor.moveToPosition(i);
				urls.add(cursor.getString(0));
			}
			cursor.close();
			ArrayList<TaskBean> taskBeans = new ArrayList<TaskBean>();
			DownloadBlockBean block = null;
			RecordValue value = null;
			for(int i = 0 ,len = urls.size() ; i < len ; i ++ ) {
				Log.d(TAG,"url-> " + urls.get(i));
				Cursor cs = ContinueDB.getInstance(mContext, version).getReadDb().
						query(ContinueDbHelper.TABLE_NAME, null,
								DbColumnConstans.URL + "=?", new String[] {urls.get(i)},
								null, null, "id asc");
				if(cs.getCount() == 0) {
					Log.d(TAG,"query blocks is empty");
					continue;
				}
				ArrayList<DownloadBlockBean> blocks = new ArrayList<DownloadBlockBean>();
				for(int j = 0 ,count = cs.getCount() ; j < count ; j ++ ) {
					cs.moveToPosition(j);
					for(String key: cs.getColumnNames()) {
						Log.d(TAG,"name->" + key + " value->" + cs.getString(cs.getColumnIndex(key)));
					}
					value = new RecordValue();
					value.id = cs.getString(cs.getColumnIndex(DbColumnConstans.ID));
					value.url = urls.get(i);
					value.filePath = cs.getString(cs.getColumnIndex(DbColumnConstans.FILE_PATH));
					value.fileSuffix = cs.getString(cs.getColumnIndex(DbColumnConstans.FILE_SUFFIX));
					value.startPos = cs.getLong(cs.getColumnIndex(DbColumnConstans.START_POS));
					value.curPos = cs.getLong(cs.getColumnIndex(DbColumnConstans.CUR_POS));
					value.blockSize = cs.getLong(cs.getColumnIndex(DbColumnConstans.BLOCK_SIZE));
					value.taskSize = cs.getLong(cs.getColumnIndex(DbColumnConstans.TASK_SIZE));
					value.downloadedSize = cs.getLong(cs.getColumnIndex(DbColumnConstans.DOWNLOADED_SIZE));
					block = new DownloadBlockBean(value.url, value.id, value.startPos, value.taskSize,
							value.blockSize);
					block.setFilePath(value.filePath);
					block.setFileSuffix(value.fileSuffix);
					block.setCurPos(value.curPos);
					block.setDownloadedSize(value.downloadedSize);
					blocks.add(block);
				}
				taskBeans.add(TaskBeanFactory.create(blocks));
			}
			return taskBeans;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean addTask(TaskBean bean) {
		ArrayList<RecordValue> values = new ArrayList<RecordValue>();
		DownloadBlockBean block = null;
		RecordValue value = null;
		
		try {
			for(int i = 0 , len = bean.getBlockCount() ; i < len ; i ++) {
				block = bean.getBlockBean(i);
				value = new RecordValue();
				value.id = block.getId();
				value.url = block.getUrl();
				value.filePath = block.getFilePath();
				value.fileSuffix = block.getFileSuffix();
				value.startPos = block.getStartPos();
				value.curPos = block.getCurPos();
				value.taskSize = block.getTaskSize();
				value.blockSize = block.getBlockSize();
				value.downloadedSize = block.getDownloadedSize();
				values.add(value);
			}
			return addRecords(values);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteTask(String url) {
		if(TextUtils.isEmpty(url)) {
			return false;
		}
		try {
			return ContinueDB.getInstance(mContext, version).getWriteDb().delete(ContinueDbHelper.TABLE_NAME,
					DbColumnConstans.URL + "=?", new String[] {url}) > 0 ? true : false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/*database*/
	public void close() {
		ContinueDB.getInstance(mContext, version).close();
	}
}
