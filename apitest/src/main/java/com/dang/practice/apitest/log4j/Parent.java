package com.dang.practice.apitest.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pfchang on 2018/6/8.
 */
public class Parent {
    private  Logger logger = LoggerFactory.getLogger(this.getClass());
    public Parent() {
        logger.info("init Parent");
    }
}
