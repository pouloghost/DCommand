package gt.research.dc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ayi.zty on 2016/2/1.
 */
public class ReflectUtils {
    public static Object getFieldValue(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            LogUtils.exception(e);
        }
        return null;
    }

    public static Object invokeMethod(Object object, String methodName) {
        try {
            Class<?> clazz = object.getClass();
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(object);
        } catch (Exception e) {
            LogUtils.exception(e);
        }
        return null;
    }

    public static Object invokeMethod(Object object, String methodName, Class[] argsClass, Object[] args) {
        try {
            Class<?> clazz = object.getClass();
            Method method = clazz.getDeclaredMethod(methodName, argsClass);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            LogUtils.exception(e);
        }
        return null;
    }

    public static Object invokeStaticMethod(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(null);
        } catch (Exception e) {
            LogUtils.exception(e);
        }
        return null;
    }

    public static Object invokeStaticMethod(String className, String methodName, Class[] argsClass, Object[] args) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, argsClass);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (Exception e) {
            LogUtils.exception(e);
        }
        return null;
    }
}
