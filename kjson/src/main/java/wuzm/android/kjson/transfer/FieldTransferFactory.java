package wuzm.android.kjson.transfer;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

import wuzm.android.kjson.transfer.field.ArrayFieldTransfer;
import wuzm.android.kjson.transfer.field.ArrayListFieldTransfer;
import wuzm.android.kjson.transfer.field.BooleanFieldTransfer;
import wuzm.android.kjson.transfer.field.ClassFieldTransfer;
import wuzm.android.kjson.transfer.field.DoubleFieldTransfer;
import wuzm.android.kjson.transfer.field.FloatFieldTransfer;
import wuzm.android.kjson.transfer.field.IntegerFieldTransfer;
import wuzm.android.kjson.transfer.field.LongFieldTransfer;
import wuzm.android.kjson.transfer.field.StringFieldTransfer;

/**
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public class FieldTransferFactory {
    public static String booleanClassName = boolean.class.getName();
    public static String BooleanClassName = Boolean.class.getName();
    public static String intClassName = int.class.getName();
    public static String IntegerClassName = Integer.class.getName();
    public static String floatClassName = float.class.getName();
    public static String FloatClassName = Float.class.getName();
    public static String doubleClassName = double.class.getName();
    public static String DoubleClassName = Double.class.getName();
    public static String longClassName = long.class.getName();
    public static String LongClassName = Long.class.getName();
    public static String StringClassName = String.class.getName();
    public static String ArrayListClassName = ArrayList.class.getName();

    public static FieldTransfer createFieldTransfer(Class fieldClass, Type fieldType, Field field) {
        FieldTransfer fieldTransfer = null;
        if(fieldClass == null) {
            throw new NullPointerException("fieldClass must not be null");
        }
        String fieldClassName = fieldClass.getName();
        if(booleanClassName.equals(fieldClassName)
                || BooleanClassName.equals(fieldClassName)) {
            fieldTransfer = new BooleanFieldTransfer(fieldClass, fieldType, field);

        }else if(intClassName.equals(fieldClassName)
                || IntegerClassName.equals(fieldClassName)) {
            fieldTransfer = new IntegerFieldTransfer(fieldClass, fieldType, field);

        }else if(floatClassName.equals(fieldClassName)
                || FloatClassName.equals(fieldClassName)) {
            fieldTransfer = new FloatFieldTransfer(fieldClass, fieldType, field);

        }else if(doubleClassName.equals(fieldClassName)
                || DoubleClassName.equals(fieldClassName)) {
            fieldTransfer = new DoubleFieldTransfer(fieldClass, fieldType, field);

        }else if(longClassName.equals(fieldClassName)
                || LongClassName.equals(fieldClassName)) {
            fieldTransfer = new LongFieldTransfer(fieldClass, fieldType, field);

        }else if(StringClassName.equals(fieldClassName)) {
            fieldTransfer = new StringFieldTransfer(fieldClass, fieldType, field);

        }else if(ArrayListClassName.equals(fieldClassName)) {
            fieldTransfer = new ArrayListFieldTransfer(fieldClass, fieldType, field);

        }else if(fieldClass.isArray()){
            fieldTransfer = new ArrayFieldTransfer(fieldClass, fieldType, field);

        }else {
            fieldTransfer = new ClassFieldTransfer(fieldClass, fieldType, field);

        }
        return fieldTransfer;
    }
}
