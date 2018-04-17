package com.dang.practice.avro;

import java.io.*;

/**
 * Created by pfchang on 2018/3/22.
 */
public class FileUtil {
    public static byte[] getBytes(String filePath){
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {

                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] s = out.toByteArray();
        return s;

    }


    public static ByteArrayOutputStream getByteArrayOutputStream(String filePath) throws IOException {
        File file = new File(filePath);
        if(!file.exists()){
            file.createNewFile();
        }
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {

                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return out;

    }
}
