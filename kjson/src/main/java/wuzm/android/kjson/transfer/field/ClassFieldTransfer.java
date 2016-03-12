package wuzm.android.kjson.transfer.field;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import wuzm.android.kjson.annotation.JsonIgnore;
import wuzm.android.kjson.annotation.JsonName;
import wuzm.android.kjson.transfer.FieldInfo;
import wuzm.android.kjson.transfer.FieldTransfer;
import wuzm.android.kjson.transfer.FieldTransferFactory;
import wuzm.android.kjson.utils.StringCheckUtil;

/**
 * 支持自定义类的转换
 *
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public class ClassFieldTransfer extends FieldInfo implements FieldTransfer {

    public ClassFieldTransfer(Class fieldClass, Type fieldType, Field field) {
        super(fieldClass, fieldType, field);
    }

    @Override
    public Object transfer(String json) {
        if(StringCheckUtil.empty(json)) {
            return null;
        }
        Class fieldClass = getFieldClass();
        Object mObj = null;
        try {
            mObj = fieldClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObj = new JSONObject(json);
            for(Field f : fieldClass.getDeclaredFields()) {

                // 找到是否标志了忽略,是则不进行解析
                JsonIgnore jsonIgnoreAnnotation = f.getAnnotation(JsonIgnore.class);
                if(jsonIgnoreAnnotation != null) {
                    continue;
                }

                // 获得json键值对的名称
                String jsonName = null;
                JsonName jsonNameAnnotation = f.getAnnotation(JsonName.class);
                if(jsonNameAnnotation == null) {
                    jsonName = f.getName();
                }else {
                    jsonName = jsonNameAnnotation.value();
                }

                // 获得json键值对的值
                Object jsonValue = null;
                FieldTransfer fieldTransfer = FieldTransferFactory
                        .createFieldTransfer(f.getType(), f.getGenericType(), f);
                jsonValue = fieldTransfer.transfer(jsonObj.getString(jsonName));

                // 给该field赋值
                String fieldName = f.getName();
                String setFieldMethodName = "set" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1, fieldName.length());
                Method setFieldMethod = fieldClass.getMethod(setFieldMethodName, f.getType());
                if(setFieldMethod != null) {
                    setFieldMethod.invoke(mObj, jsonValue);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mObj;
    }
}
