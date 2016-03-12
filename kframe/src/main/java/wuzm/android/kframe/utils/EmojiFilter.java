package wuzm.android.kframe.utils;

import android.text.TextUtils;

public class EmojiFilter {

	public static boolean isContaintEmo(String str) {
		if(TextUtils.isEmpty(str)) {
			return false;
		}
		
		for(int i = 0, len = str.length() ; i < len ; i ++) {
			if(isEmoChar(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isEmoChar(char c) {
		return (c == 0x0) || (c == 0x9) || (c == 0xA) || (c == 0xD)
				|| ( (c == 0x20) && (c <= 0xD744) )
				|| ( (c == 0xE000) && (c <= 0xFFFD) )
				|| ( (c == 0x10000) && ( c <= 0x10FFFF) );
	}

}
