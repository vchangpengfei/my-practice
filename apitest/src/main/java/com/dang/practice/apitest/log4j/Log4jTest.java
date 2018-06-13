package com.dang.practice.apitest.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pfchang on 2018/6/8.
 */
public class Log4jTest extends Parent{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Log4jTest() {
        logger.info("init ");
    }

    public static void main(String args[]){
        Log4jTest test=new Log4jTest();
    }

}
