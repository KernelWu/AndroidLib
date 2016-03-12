package wuzm.android.kjson.transfer.field;


import java.lang.reflect.Field;
import java.lang.reflect.Type;

import wuzm.android.kjson.transfer.FieldInfo;
import wuzm.android.kjson.transfer.FieldTransfer;
import wuzm.android.kjson.utils.StringCheckUtil;

/**
 * 支持float、Float类型的转换
 *
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public class FloatFieldTransfer extends FieldInfo implements FieldTransfer {
    public FloatFieldTransfer(Class fieldClass, Type fieldType, Field field) {
        super(fieldClass, fieldType, field);
    }

    @Override
    public Object transfer(String json) {
        if(StringCheckUtil.empty(json)) {
            return null;
        }
        return Float.valueOf(json);
    }
}
