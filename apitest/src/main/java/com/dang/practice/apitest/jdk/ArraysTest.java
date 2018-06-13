package com.dang.practice.apitest.jdk;

import java.util.Arrays;

/**
 * Created by pfchang on 2018/5/18.
 */
public class ArraysTest {
    public static void main(String args[]){
        int cc[]={10,20,25,30};
        System.out.println(Arrays.binarySearch(cc,20));
        System.out.println(Arrays.binarySearch(cc,21));
    }
}
