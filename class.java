package com.cainiao.was.op.job;

import com.cainiao.trace.log.method.TraceMethod;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.Loader.loadClass;

/**

 * @Created by wulong.yb

 * @Date 2019-05-09 11:10
 */
public class ClassApp {

    public static void main(String[] args) throws ClassNotFoundException {
        //        for (int i = 219; i >= 0; i--) {
        //            System.out.println("前" + i);
        //        }
        test();
    }
//
    private static void test() throws ClassNotFoundException {
        Set<Class<?>> classes = Sets.newHashSet();
        //        findClassesByFile("com.cainiao.was.op.client.wasbase", "/Users/yollock/work-zone/was/was-op/was-op-adapter/target/classes/com/cainiao/was/op/client/wasbase", classes);
        //        findClassesByFile("com.cainiao.was.op.client.wasjob", "/Users/yollock/work-zone/was/was-op/was-op-adapter/target/classes/com/cainiao/was/op/client/wasjob", classes);
        findClassesByFile("com.cainiao.was.op.client.wmp", "/Users/yollock/work-zone/was/was-op/was-op-adapter/target/classes/com/cainiao/was/op/client/wmp", classes);

        //        findClassesByFile("com.cainiao.was.op.biz.action.device", "/Users/yollock/work-zone/was/was-op/was-op-biz/target/classes/com/cainiao/was/op/biz/action/device", classes);

//        classes.forEach(System.out::println);

        Set<String> filterMethods = Sets.newHashSet("wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll");

        String ss = classes.stream().map(e -> Arrays.stream(e.getMethods()))
                .flatMap(e -> e)
                .filter(e -> !filterMethods.contains(e.getName()))
                .map(e -> e.getDeclaringClass().getSimpleName() + "_" + e.getName())
                .collect(Collectors.joining(","));
        System.out.println(ss);
    }

    // 一个类输出一行
    private static void singleClassLine(Set<Class<?>> classes, Set<String> filterMethods) {
        classes.stream().map(e -> Arrays.stream(e.getMethods()))
                .forEach(e -> {
                    String exp = e.filter(t -> !filterMethods.contains(t.getName()))
                            .filter(t -> t.getAnnotation(TraceMethod.class) != null)
                            .map(t -> t.getDeclaringClass().getSimpleName() + "." + t.getName())
                            .collect(Collectors.joining(","));

                    exp = optimize(exp);

                    System.out.println(exp);
                    System.out.println();
                    System.out.println();
                });
    }

    private static String optimize(String exp) {
        List<String> strs = Splitter.on(",").omitEmptyStrings().splitToList(exp);
        if (exp.length() < 1) {

//            System.out.println("exp=" + exp);
            return exp;
        }
        String firstKey = strs.get(0).substring(0, strs.get(0).indexOf("."));
        return strs.stream().map(e -> firstKey + e.substring(e.indexOf("."))).collect(Collectors.joining(","));
    }

    /**
     * 扫描包路径下的所有class文件
     *
     * @param pkgName 包名
     * @param pkgPath 包对应的绝对地址
     * @param classes 保存包路径下class的集合
     */
    private static void findClassesByFile(String pkgName, String pkgPath, Set<Class<?>> classes) throws ClassNotFoundException {
        File dir = new File(pkgPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 过滤获取目录，or class文件
        File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"));

        if (dirfiles == null || dirfiles.length == 0) {
            return;
        }

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







/Users/sx/Desktop/111111/tc-logistics-admin/tc-logistics-admin-service