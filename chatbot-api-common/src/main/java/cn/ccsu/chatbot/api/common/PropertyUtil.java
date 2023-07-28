package cn.ccsu.chatbot.api.common;


import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description: 根据springboot版本，解析配置文件，获取指定前缀的键值对
 * @author：wufengning
 * @date: 2023/7/28
 */
public class PropertyUtil {
    private static int springBootVersion=1;
    static {
        try {
            Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
        }catch (ClassNotFoundException e){
            springBootVersion=2;
        }
    }

    @SuppressWarnings("unchecked")
    public static<T> T handle(final Environment environment, final String prefix, final Class<T> targetClass){
        switch (springBootVersion){
            case 1:
                return (T) v1(environment,prefix);
            default:
                return (T) v2(environment,prefix,targetClass);
        }
    }

    private static Object v1(final Environment environment,final String prefix){
        try{
            //该类是加载解析yml等配置文件的
            Class<?> resolverClass = Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
            Constructor<?> resolverConstructor = resolverClass.getDeclaredConstructor(PropertyResolver.class);
            Method getSubProperties = resolverClass.getDeclaredMethod("getSubProperties", String.class);
            Object resolverObject = resolverConstructor.newInstance(environment);
            String prefixParam = prefix.endsWith(".") ? prefix : prefix + ".";
            return getSubProperties.invoke(resolverObject, prefixParam);
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                       | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private static Object v2(final  Environment environment, final String prefix,final Class<?> targetClass){
        try{
            Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
            Method getMethod = binderClass.getDeclaredMethod("get", Environment.class);
            Method bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);
            Object binderObject = getMethod.invoke(null,environment);
            String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
            Object bindObjectResult = bindMethod.invoke(binderObject, prefixParam,targetClass);
            Method resultGetMethod = bindObjectResult.getClass().getDeclaredMethod("get");
            return resultGetMethod.invoke(bindObjectResult);
        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                       | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
