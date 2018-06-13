package com.dang.practice.bdb.secondary;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

import java.io.UnsupportedEncodingException;

public class KeyCreator implements SecondaryKeyCreator {
        private EntryBinding valueBinding = null;
        public KeyCreator(EntryBinding binding) {
            valueBinding = binding;//用来实现Entry--object 转换  
        }  

        public boolean createSecondaryKey(SecondaryDatabase sdb, DatabaseEntry keyEntry, DatabaseEntry valueEntry, DatabaseEntry resultEntry) {
            if(valueEntry!=null){
                HashMapMain.Student value = (HashMapMain.Student) valueBinding.entryToObject(valueEntry);
                try {
                    resultEntry.setData(value.get("key").toString().getBytes("gb2312"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//            resultEntry.setData(value.get("key2").toString().getBytes());
            }

            return true;
        }   
          
    }  