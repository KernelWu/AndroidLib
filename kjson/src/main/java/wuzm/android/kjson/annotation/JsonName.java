package wuzm.android.kjson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */

/**
 * json键值对的键的注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonName {
    String value();
}
