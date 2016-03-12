package wuzm.android.kjson.transfer;


import wuzm.android.kjson.transfer.field.ClassFieldTransfer;

/**
 * Created by kernel on 15/3/18.
 * Email: 372786297@qq.com
 */
public class JsonTransferEntrance {

    public static Object transfer(Class clas, String json) {
        return new ClassFieldTransfer(clas, null, null)
                .transfer(json);
    }
}
