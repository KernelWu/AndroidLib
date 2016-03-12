package wuzm.android.kjson;


import wuzm.android.kjson.transfer.JsonTransferEntrance;

/**
 * Created by kernel on 15/3/21.
 * Email: 372786297@qq.com
 */
public class JSON {

    /**
     * 通过给的json字符串，转换成对应的类的实例
     * @param clas 要转换成的相应的类
     * @param json 要转换的json格式的字符串
     * @return
     */
    public static Object stringToBean(Class clas, String json) {
        return JsonTransferEntrance.transfer(clas, json);
    }
}
