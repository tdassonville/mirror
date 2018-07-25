package com.genymobile.mirror;

import com.genymobile.mirror.annotation.SetInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helper class to find internal fields and methods via reflection.
 */
class ReflectionFinder {

    private ClassLoader classLoader;

    private Unwrapper unwrapper;

    ReflectionFinder(ClassLoader classLoader, Unwrapper unwrapper) {
        this.classLoader = classLoader;
        this.unwrapper = unwrapper;
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name, true, classLoader);
    }

    public Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        return clazz.getDeclaredField(name);
    }

    public Method findMethod(Class<?> clazz, Method mirrorMethod) throws NoSuchMethodException {
        return findMethod(clazz, mirrorMethod.getName(), retrieveParameters(mirrorMethod));
    }

    public Method findSetInstanceMethod(java.lang.Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (isSetInstanceMethods(method)) {
                return method;
            }
        }
        return null;
    }

    private boolean isSetInstanceMethods(Method method) {
        return method.getAnnotation(SetInstance.class) != null;
    }

    private Method findMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(name, params);
    }

    public Constructor<?> findConstructor(Class<?> clazz, Method mirrorMethod) throws NoSuchMethodException {
        return findConstructor(clazz, retrieveParameters(mirrorMethod));
    }

    private Constructor<?> findConstructor(Class<?> clazz, Class<?>... params) throws NoSuchMethodException {
        return clazz.getDeclaredConstructor(params);
    }

    private java.lang.Class[] retrieveParameters(Method method) {
        java.lang.Class[] genuineTypes = method.getParameterTypes();
        java.lang.Class[] types = new java.lang.Class[genuineTypes.length];

        for (int i = 0; i < genuineTypes.length; ++i) {
            types[i] = unwrapper.unwrapClass(genuineTypes[i]);
        }
        return types;
    }

}
