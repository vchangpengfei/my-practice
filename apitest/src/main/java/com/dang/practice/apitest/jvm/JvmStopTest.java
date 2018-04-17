package com.dang.practice.apitest.jvm;

/**
 * Created by pfchang on 2018/3/19.
 * 虚拟机启动多线程时，其中一个线程报error，虚拟机不会停止
 * jvm option:-Xms2m -Xmx2m
 *
 */
public class JvmStopTest {
    public static void main(String args[]){
        System.out.println("--------begin----------");
        new Thread(new Runnable() {
            public void run() {
                while(true){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {


                    }

                    System.out.println("thread");
                }
            }
        }).start();
        double[] myList=new double[100000];
        System.out.println(myList+"--------end----------");
    }
}
