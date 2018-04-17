package com.dang.practice.apitest.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by pfchang on 2018/4/3.
 */
public class Test {

    public static void test1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        FastClass fastClass = FastClass.create(SampleBean.class);
//        FastMethod fastMethod = fastClass.getMethod(SampleBean.class.getMethod("echo", String.class));
        SampleBean myBean = new SampleBean();
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000000; i++) {
//            fastMethod.invoke(myBean, new Object[]{"haha"+i});
//            //fastMethod.invoke(myBean, new Object[]{"haha"});
//        }
//        System.out.println("fastmethod:" + (System.currentTimeMillis() - start));


        Method m = SampleBean.class.getMethod("echo", String.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {

//            m.invoke(myBean, "haha"+i);
            m.invoke(myBean, "haha");
        }
        System.out.println("reflect:"+(System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
//            myBean.echo("haha"+i);
            myBean.echo("haha");
        }
        System.out.println("normal:"+(System.currentTimeMillis() - start));
    }

    public static void test2(){

    }

    public static void main(String args[]) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        test1();
    }
}
