package wuzm.android.kframe.widget.callback;

import java.io.Serializable;

/**
 * 
 * @author wuzm
 * @version 0.1beta
 * @since 2014/10/17
 *
 */
public interface OnCacheListener {
       public boolean cache(Serializable cache);
       public Serializable readCache();
}
