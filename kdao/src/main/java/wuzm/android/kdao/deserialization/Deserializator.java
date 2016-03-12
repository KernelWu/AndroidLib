package wuzm.android.kdao.deserialization;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

public class Deserializator {
	
	public static ArrayList<String> getPropertiesName (Class<?> baseClass) {
		ArrayList<String> names = new ArrayList<String>();
		Field[] fields = baseClass.getDeclaredFields();
		for(Field f : fields) {
			names.add(f.getName());
		}
		return names;
	}
	
	public static Object parse(Class<?> baseClass, Map<String,Object> column) {
		try {
			Object obj = baseClass.newInstance();
			Field[] fields = baseClass.getDeclaredFields();
			for(Field f : fields) {
				f.setAccessible(true);
				f.set(obj, column.get(f.getName()) );
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
