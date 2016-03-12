package wuzm.android.kjson.transfer.field;


import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import wuzm.android.kjson.transfer.FieldInfo;
import wuzm.android.kjson.transfer.FieldTransfer;
import wuzm.android.kjson.transfer.FieldTransferFactory;

/**
 * Created by kernel on 15/3/21.
 * Email: 372786297@qq.com
 */
public class ArrayFieldTransfer extends FieldInfo implements FieldTransfer {
    private Class mElementClass;

    public ArrayFieldTransfer(Class fieldClass, Type fieldType, Field field) {
        super(fieldClass, fieldType, field);
        if( !fieldClass.isArray()) {
            throw new IllegalStateException("fieldClass isn't array");
        }
        mElementClass = fieldClass.getComponentType();
    }

    @Override
    public Object transfer(String json) {
        Object mObj = null;
        try {
            FieldTransfer fieldTransfer = FieldTransferFactory
                    .createFieldTransfer(mElementClass, null, null);
            JSONArray jsonArray = new JSONArray(json);

            int jsonArrayLenth = jsonArray.length();
            mObj = Array.newInstance(mElementClass, jsonArrayLenth);
            for(int i = 0; i < jsonArrayLenth; i ++) {
                Array.set(mObj, i, fieldTransfer.transfer(jsonArray.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mObj;
    }
}
