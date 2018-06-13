package com.dang.practice.apitest.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by pfchang on 2018/6/8.
 */
public class MyAppender extends AppenderSkeleton {

    private String account ;

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    protected void append(LoggingEvent event) {
        System.out.println("Hello, " + account + " : "+ event.getMessage());
    }

    public void close() {
        // TODO Auto-generated method stub

    }

    public boolean requiresLayout() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
