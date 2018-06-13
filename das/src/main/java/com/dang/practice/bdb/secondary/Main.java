package com.dang.practice.bdb.secondary;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.*;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Main {
  
    /** 
     * @param args 
     */  
    static Environment env = null;
      
    public static void main(String[] args) {
        BDBUtil<Integer, Student> bDB = new BDBUtil<Integer, Student>("testDB",Student.class);
        Student s1 = new Student(1,"ylf");
        Student s2 = new Student(2,"dsb");
        Student s3 = new Student(3,"dbc");

        bDB.put(1, s1);
        bDB.put(2, s2);
        bDB.put(3, s3);

        Student s = new Student();
        s.fromString(bDB.get(3).toString());
        System.out.println("my name is "+s.getName()+" no is "+s.getNo());

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

        public void close(){
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
        private String name;
        private int no;

        public Student() {
        }

        public Student(int no, String name) {
            this.no = no;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        @Override
        public String toString() {
            return "Student" + no + ":" + name;
        }

        public void fromString(String str) {
            int i = str.indexOf(':');
            String noStr = str.substring(7, i);
            this.no = Integer.parseInt(noStr);
            this.name = str.substring(i + 1);
        }
    }
    }