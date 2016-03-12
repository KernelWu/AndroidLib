package wuzm.android.kjson.transfer;

/**
 * 把json格式的字符串转换成类的实例的接口
 *
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public interface FieldTransfer {
    /**
     * 把json格式的字符串转换成类的实例
     * @param json
     * @return
     */
    public Object transfer(String json);
}
