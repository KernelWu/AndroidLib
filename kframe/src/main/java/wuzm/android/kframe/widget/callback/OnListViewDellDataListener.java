package wuzm.android.kframe.widget.callback;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public interface OnListViewDellDataListener {
	public void onGetData(int page);
	/** 返回true表示使用自定义的填充数据方案，false使用默认的数据填充方案*/
	public boolean onFillData(int page, Object obj);
}
