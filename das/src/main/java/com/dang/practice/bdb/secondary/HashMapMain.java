package com.dang.practice.bdb.secondary;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.*;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据动态绑定
 */

public class HashMapMain {
  
    /** 
     * @param args 
     */  
    static Environment env = null;
      
    public static void main(String[] args) {  
        BDBUtil<Integer, Student> bDB = new BDBUtil<Integer, Student>("testDB",Student.class);
        Student s1 = new Student();
        s1.put("key","1");
        s1.put("key1","val1");

        Student s2 = new Student();
        s2.put("key","2");
        s2.put("key1","val2");

        Student s3 = new Student();
        s3.put("key","4");
        s3.put("key1","val3");

        Student s4 = new Student();
        s4.put("key","4");
        s4.put("key1","val4");

        Student s5 = new Student();
        s5.put("key","5");
        s5.put("key1","val5");

          
        bDB.put(1, s1);  
        bDB.put(2, s2);  
        bDB.put(3, s3);
        bDB.put(4, s4);
        bDB.put(5, s5);

        Student s = new Student();
        Student tt=bDB.get(3);
        System.out.println("my name is "+tt.get("key")+" no is "+tt.get("key1"));

        System.out.println("=========================");
        for(Student obj:bDB.getsecond("4")){
            System.out.println(obj.get("key1"));
        }
        System.out.println("=========================");

        System.out.println("=========================");
        for(Student obj:bDB.getSecoundRange("2","5")){
            System.out.println(obj.get("key"));
        }
        System.out.println("=========================");


        System.out.println(bDB.sizesecond());
        System.out.println(bDB.size());  
        bDB.close();  
    }

    /**
     * 我们的BDB工具
     * 目前对外提供添加数据和取得数据,删除数据3个接口
     * 类似HashMap的使用方法
     * 注意：
     * 这里的K 和 V两个类都必须实现来Serializable
     * 而且也实现来toString
     * 使用结束记得调用close()
     * @author ylf
     *
     */
    static class BDBUtil<K, V>{
        private Environment env = null;
        private EnvironmentConfig envCfig = null;
        private Database db = null;
        private DatabaseConfig dbCfig = null;

        private File file = null;
        private Database classDB = null;
        private StoredClassCatalog classCatalog = null;
        private EntryBinding valueBinding = null;
        private SecondaryConfig secCfig=null;
        private SecondaryDatabase secDb=null;
        public BDBUtil(String dbName, Class valueClass) {
            envCfig = new EnvironmentConfig();
            envCfig.setAllowCreate(true);
            file = new File("./test/");
            env = new Environment(file, envCfig);
            dbCfig = new DatabaseConfig();
            dbCfig.setAllowCreate(true);
            db = env.openDatabase(null, dbName, dbCfig);

            //首先创建一个我们类的数据库
            classDB = env.openDatabase(null, "classDB", dbCfig);
            //实例化一个Catalog
            classCatalog = new StoredClassCatalog(classDB);
            //实例化一个binding来转换
            valueBinding = new SerialBinding(classCatalog, valueClass);

            /***********二级索引库配置************/
            String secDbName = "secDbName";
            secCfig = new SecondaryConfig();
            secCfig.setAllowCreate(true);
            secCfig.setSortedDuplicates(true);//二级库可以有重复

            KeyCreator keyCreator = new KeyCreator(valueBinding);
            secCfig.setKeyCreator(keyCreator);
            secDb = env.openSecondaryDatabase(null, secDbName, db, secCfig);

        }

        public boolean put(K key, V value){
            DatabaseEntry keyEntry = new DatabaseEntry(key.toString().getBytes());
            DatabaseEntry valueEntry = new DatabaseEntry();
            valueBinding.objectToEntry(value, valueEntry);

            db.put(null, keyEntry, valueEntry);
            return true;
        }

        public V get(K key){
            DatabaseEntry keyEntry;
            V value = null;
            try {
                keyEntry = new DatabaseEntry(key.toString().getBytes("gb2312"));
                DatabaseEntry valueEntry = new DatabaseEntry();
                if(db.get(null,keyEntry,valueEntry,LockMode.DEFAULT) == OperationStatus.SUCCESS){
                    value = (V)valueBinding.entryToObject(valueEntry);
                    return value;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return value;

        }

        public List<V> getsecond(String key){

            SecondaryCursor sCursor=secDb.openCursor(null, null);

            DatabaseEntry keyEntry;
            V value = null;
            List<V> results=new ArrayList<V>();
            try {
                keyEntry = new DatabaseEntry(key.toString().getBytes("gb2312"));
                DatabaseEntry valueEntry = new DatabaseEntry();

                OperationStatus val = sCursor.getSearchKey(keyEntry, valueEntry, LockMode.DEFAULT);
                while(OperationStatus.SUCCESS == val){
                    results.add((V)valueBinding.entryToObject(valueEntry));
                    val = sCursor.getNextDup(keyEntry, valueEntry, LockMode.DEFAULT);//游标下一个的副本
//                    val = sCursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT);//游标下一个的副本
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sCursor.close();
            return results;

        }


        public List<V> getSecoundRange(String key1,String key2){
            SecondaryCursor sCursor=secDb.openCursor(null, null);

            DatabaseEntry keyEntry1;
            DatabaseEntry keyEntry2;
            DatabaseEntry keyEntry=new DatabaseEntry();
            List<V> results=new ArrayList<V>();
            try {
                keyEntry1 = new DatabaseEntry(key1.toString().getBytes("gb2312"));
                keyEntry2 = new DatabaseEntry(key2.toString().getBytes("gb2312"));
                DatabaseEntry valueEntry = new DatabaseEntry();

                OperationStatus val = sCursor.getSearchKeyRange(keyEntry1, keyEntry2,valueEntry, LockMode.DEFAULT);

                while(OperationStatus.SUCCESS == val){
                    results.add((V)valueBinding.entryToObject(valueEntry));
                    val = sCursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT);
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sCursor.close();
            return results;
        }


        public boolean del(K keyStr){
            DatabaseEntry key;
            try {
                key = new DatabaseEntry(keyStr.toString().getBytes("gb2312"));
                if(OperationStatus.SUCCESS == db.delete(null, key))
                    return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return false;
        }

        public long size(){
            return db.count();
        }

        public long sizesecond(){
            return secDb.count();
        }

        public void close(){
            if(secDb!=null)
                secDb.close();
            if(db!=null)
            db.close();
            if(classDB!=null)
            classDB.close();
            if(env!=null)
            env.cleanLog();
            if(env!=null)
            env.close();
        }
    }

    /**
     * 序列化了的类
     * 实现toString()
     * @author ylf
     *
     */
     static class Student implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 7333239714054069867L;

        private static final String key="key";

        private HashMap<String,Object> map=new HashMap<String, Object>();


        public void put(String key, Object value){
            map.put(key,value);
        }

        public Object get(String key){
            return map.get(key);
        }

    }
    }