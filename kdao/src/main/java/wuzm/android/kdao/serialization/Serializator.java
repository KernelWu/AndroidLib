package wuzm.android.kdao.serialization;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Serializator {

	private Map<String,Object> properties = new HashMap<String, Object>();
	private Class<?> mClass;
	
	public Serializator(Object obj) {
		mClass = obj.getClass();
		parse(obj);
	}
	
	private void parse(Object instance) {
		
		Field[] fields = mClass.getDeclaredFields();
		for(Field f : fields) {
			f.setAccessible(true);
			try {
				properties.put(f.getName(), f.get(instance));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public Map<String,Object> getProperties() {
		return properties;
	}
	
	public static Map<String,Object> getProperties(Class<?> c, Object instance) {
		
		if( c == null || instance == null ) {
			throw new IllegalArgumentException("paraments can't be null");
		}
		Map<String,Object> properties = new HashMap<String, Object>();
		Field[] fields = c.getDeclaredFields();
		for(Field f : fields) {
			f.setAccessible(true);
			try {
				properties.put(f.getName(), f.get(instance));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return properties;
	}
	
	public static ArrayList<String> getPropertyNames(Class<?> baseClass) {
		ArrayList<String> names = new ArrayList<String>();
		Field[] fields = baseClass.getDeclaredFields();
		for(Field f : fields) {
			f.setAccessible(true);
			try {
				names.add(f.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return names;
	}
 }
