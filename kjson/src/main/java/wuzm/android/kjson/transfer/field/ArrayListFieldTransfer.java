package wuzm.android.kjson.transfer.field;


import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;

import wuzm.android.kjson.transfer.FieldInfo;
import wuzm.android.kjson.transfer.FieldTransfer;
import wuzm.android.kjson.transfer.FieldTransferFactory;
import wuzm.android.kjson.utils.StringCheckUtil;

/**
 * 支持ArrayList类型的转换
 *
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public class ArrayListFieldTransfer extends FieldInfo implements FieldTransfer {
    // 单项元素的类型
    private Type mElementType;

    public ArrayListFieldTransfer(Class fieldClass, Type fieldType, Field field) {
        super(fieldClass, fieldType, field);
        if(fieldType != null && fieldType instanceof ParameterizedType) {
            mElementType = ((ParameterizedType)fieldType)
                    .getActualTypeArguments()[0];
        }else {
            mElementType = Object.class;
        }
    }

    @Override
    public Object transfer(String json) {
        if(StringCheckUtil.empty(json)) {
            return  null;
        }
        ArrayList mObj = new ArrayList();
        Class elementClass = null;
        if(mElementType instanceof TypeVariable) {

        }else if(mElementType instanceof GenericArrayType) {

        }else if(mElementType instanceof ParameterizedType) {
            elementClass = (Class)((ParameterizedType) mElementType).getRawType();
        }else {
            elementClass = (Class)mElementType;
        }
        FieldTransfer fieldTransfer = FieldTransferFactory
                .createFieldTransfer(elementClass, mElementType, null);
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0, len = jsonArray.length(); i < len; i++) {
                mObj.add(fieldTransfer.transfer(jsonArray.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mObj;
    }

}
