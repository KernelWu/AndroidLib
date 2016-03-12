package wuzm.android.kjson.transfer.field;


import java.lang.reflect.Field;
import java.lang.reflect.Type;

import wuzm.android.kjson.transfer.FieldInfo;
import wuzm.android.kjson.transfer.FieldTransfer;

/**
 * 支持String类型的转换
 *
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public class StringFieldTransfer extends FieldInfo implements FieldTransfer {
    public StringFieldTransfer(Class fieldClass, Type fieldType, Field field) {
        super(fieldClass, fieldType, field);
    }

    @Override
    public Object transfer(String json) {
        return json;
    }
}
