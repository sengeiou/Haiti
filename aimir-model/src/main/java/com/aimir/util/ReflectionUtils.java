package com.aimir.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReflectionUtils
 * @author YeonKyoung Park(goodjob)
 *
 */
public class ReflectionUtils {

	public static Object getFieldValue(Object instance, String fieldName) {
		final Class<?> theClass = instance.getClass();
		final Object fieldValue = getFieldValue(instance, fieldName, theClass);
		return fieldValue;
	}

	private static Object getFieldValue(Object instance, String fieldName,
			final Class<?> theClass) {
		try {
			final Field field = theClass.getDeclaredField(fieldName);
			boolean oldAccessible = field.isAccessible();
			try {
				field.setAccessible(true);
				return field.get(instance);
			} catch (IllegalArgumentException ex) {
				return null;
			} catch (IllegalAccessException ex) {
				return null;
			} finally {
				field.setAccessible(oldAccessible);
			}
		} catch (NoSuchFieldException nsfex) {
			if (theClass.getSuperclass() == null) {
				return null;
			} else {
				final Object fieldValue = getFieldValue(instance, fieldName,
						theClass.getSuperclass());
				return fieldValue;
			}
		}
	}
	
	/**
	 * Conversion Method
	 * List<DefineClass Object> to List<Map Object>
	 * @author 양철민
	 */
	public static List<Map<String, Object>> getDefineListToMapList(List list) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		
		for(Object obj: list) {
			java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			for(int i=0 ; i < fields.length ; i++ ) {
				fields[i].setAccessible(true);
				try {
					map.put(fields[i].getName(), fields[i].get(obj));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			resultList.add(map);
		}
		
		return resultList;
	}
}
