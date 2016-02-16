package gt.research.dc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LogUtils.exception(e);
        }
        return null;
    }

    public static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Class<?> clazz = object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LogUtils.exception(e);
        }
    }

    public static void setFieldValueAsClass(Object object, Class clazz, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LogUtils.exception(e);
        }
    }

    public static Object invokeMethod(Object object, String methodName) throws Throwable {
        try {
            Class<?> clazz = object.getClass();
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LogUtils.exception(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return null;
    }

    public static Object invokeMethod(Object object, String methodName, Class[] argsClass, Object[] args) throws Throwable {
        try {
            Class<?> clazz = object.getClass();
            Method method = clazz.getDeclaredMethod(methodName, argsClass);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            LogUtils.exception(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return null;
    }

    public static Object invokeStaticMethod(String className, String methodName) throws Throwable {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            LogUtils.exception(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return null;
    }

    public static Object invokeStaticMethod(String className, String methodName, Class[] argsClass, Object[] args) throws Throwable {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, argsClass);
            method.setAccessible(true);
            return method.invoke(null, args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            LogUtils.exception(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
        return null;
    }
}
