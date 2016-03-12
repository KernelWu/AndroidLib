package wuzm.android.kframe.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobilePhoneUtil {
	
	public static  boolean isMobilePhone(String number) {
		String regulaStr = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		Pattern p = Pattern.compile(regulaStr);
		Matcher m = p.matcher(number);
		return m.matches();
	}

}
