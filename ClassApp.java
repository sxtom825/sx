package com.taobao.tc.wms.op.biz.service;



import com.google.common.collect.Sets;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.Loader.loadClass;

public class ClassApp {

    public static void main(String[] args) throws ClassNotFoundException {
        test();
    }

    private static void test() throws ClassNotFoundException {
        Set<Class<?>> classes = Sets.newHashSet();
        findClassesByFile("com.taobao.tc.wms.op",
                "/Users/sx/Desktop/111111/tc-wms-op/tc-wms-op-client/target/classes/com/taobao/tc/wms/op", classes);

        Set<String> filterMethods = Sets.newHashSet("wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll");

        String ss = classes.stream().map(e -> Arrays.stream(e.getMethods()))
                .flatMap(e -> e)
                .filter(e -> !filterMethods.contains(e.getName()))
                .map(e -> e.getDeclaringClass().getSimpleName() + "_" + e.getName())
                .collect(Collectors.joining(","));
        System.out.println(ss);
    }

    private static void findClassesByFile(String pkgName, String pkgPath, Set<Class<?>> classes) throws ClassNotFoundException {

        File dir = new File(pkgPath);

        File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"));

        String className;
        Class clz;
        for (File f : dirfiles) {
            if (f.isDirectory()) {
                findClassesByFile(pkgName + "." + f.getName(), pkgPath + "/" + f.getName(), classes);
                continue;
            }

            // 获取类名，干掉 ".class" 后缀
            className = f.getName();
            className = className.substring(0, className.length() - 6);

            // 加载类
            clz = loadClass(pkgName + "." + className);
            if (clz != null) {
                classes.add(clz);
            }
        }
    }

}




