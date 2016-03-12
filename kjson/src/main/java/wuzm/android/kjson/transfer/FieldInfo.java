package wuzm.android.kjson.transfer;


import java.lang.reflect.Field;
import java.lang.reflect.Type;

import wuzm.android.kjson.annotation.JsonIgnore;
import wuzm.android.kjson.annotation.JsonName;

/**
 * 存放field的相关信息
 *
 * Created by kernel on 15/3/20.
 * Email: 372786297@qq.com
 */
public abstract class FieldInfo {
    private Class mFieldClass;
    private Type mFieldType;
    private Field mField;
    private String mFieldName;

    /** 注解*/
    private JsonName mJsonNameAnnotation;
    private JsonIgnore mJsonIgnoreAnnotation;

    public FieldInfo(Class fieldClass, Type fieldType, Field field) {
        mFieldClass = fieldClass;
        mFieldType = fieldType;
        mField = field;
        if(field != null) {
            mFieldName = field.getName();
            mJsonIgnoreAnnotation = field.getAnnotation(JsonIgnore.class);
            mJsonNameAnnotation = field.getAnnotation(JsonName.class);
        }
    }

    public Class getFieldClass() {
        return mFieldClass;
    }

    public Type getFieldType() {
        return mFieldType;
    }

    public Field getField() {
        return mField;
    }

    public String getFieldName() {
        return mFieldName;
    }

    public JsonName getJsonNameAnnotation() {
        return mJsonNameAnnotation;
    }

    public JsonIgnore getJsonIgnoreAnnotation() {
        return mJsonIgnoreAnnotation;
    }

    /** 获取该字段的json名称*/
    public String getJsonName() {
        if(mJsonIgnoreAnnotation != null) {
            return null;
        }
        String jsonName = null;
        if(mJsonNameAnnotation != null) {
            jsonName = mJsonNameAnnotation.value();
        }else {
            jsonName = mFieldName;
        }
        return jsonName;
    }

    /** 是否要忽略该字段的json解析*/
    public boolean isIgnoreJson() {
        return mJsonIgnoreAnnotation == null;
    }

}
