package com.dang.practice.avro;

import com.dang.practice.avro.entity.User;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfchang on 2018/3/20.
 * http://blog.csdn.net/hu2010shuai/article/details/53010350
 */
public class AvroTest {


    public static void main(String args[]) throws IOException {
//        //将schema从StringPair.avsc文件中加载
//        Schema.Parser parser = new Schema.Parser();
//        Schema schema = parser.parse(AvroTest.class.getClassLoader().getResourceAsStream("avro/user.avsc"));
//
//        //根据schema创建一个record示例
//        GenericRecord datum = new GenericData.Record(schema);
//        datum.put("name", "chang");
//        datum.put("favorite_number", 10);
//        datum.put("favorite_color", "red");
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        //DatumWriter可以将GenericRecord变成edncoder可以理解的类型
//        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
//        //encoder可以将数据写入流中，binaryEncoder第二个参数是重用的encoder，这里不重用，所用传空
//        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
//        writer.write(datum,encoder);
//        encoder.flush();
//        out.close();
//
//        DatumReader<GenericRecord> reader=new GenericDatumReader<GenericRecord>(schema);
//        Decoder decoder=DecoderFactory.get().binaryDecoder(out.toByteArray(),null);
//        GenericRecord result=reader.read(null,decoder);
//        System.out.println(result.get("name").toString());
//        System.out.println(result.get("favorite_number").toString());
//        System.out.println(result.get("favorite_color").toString());


        dataw2f();
        datar2f();
//        serializeAvroToFile();
//
//        deserializeAvroFromFile();

    }


    public static void serializeAvroToFile() throws IOException {

        List<User> userList=new ArrayList<User>();
        for(int i=0;i<50000000;i++){
            userList.add(new User("chang"+i,i,"red"+i));
        }
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(userList.get(0).getSchema(), new File("userlist.txt"));
        for (User user: userList) {
            dataFileWriter.append(user);
        }
        dataFileWriter.close();

    }


    public static void deserializeAvroFromFile() throws IOException {
        File file = new File("userlist.txt");
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader);
        User user = null;
        System.out.println("----------------deserializeAvroFromFile-------------------");
        while (dataFileReader.hasNext()) {
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }



    public static void dataw2f() throws IOException {
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(AvroTest.class.getClassLoader().getResourceAsStream("avro/user.avsc"));




        File diskFile = new File("userlist1.txt");
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
        dataFileWriter.create(schema, diskFile);

        for(int i=0;i<50000000;i++){
            //根据schema创建一个record示例
            GenericRecord datum = new GenericData.Record(schema);
            datum.put("name", "chang"+i);
            datum.put("favorite_number", i);
            datum.put("favorite_color", "red"+i);
            dataFileWriter.append(datum);
        }
        dataFileWriter.close();



//        ByteArrayOutputStream out = FileUtil.getByteArrayOutputStream("userlist1.txt");
//        //DatumWriter可以将GenericRecord变成edncoder可以理解的类型
//        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
//        //encoder可以将数据写入流中，binaryEncoder第二个参数是重用的encoder，这里不重用，所用传空
//        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
//        writer.write(datum,encoder);
//        encoder.flush();
//        out.close();
    }

    public static void datar2f() throws IOException {
//        ByteArrayOutputStream out = FileUtil.getByteArrayOutputStream("userlist1.txt");
//        Schema.Parser parser = new Schema.Parser();
//        Schema schema = parser.parse(AvroTest.class.getClassLoader().getResourceAsStream("avro/user.avsc"));
//        DatumReader<GenericRecord> reader=new GenericDatumReader<GenericRecord>(schema);
//        Decoder decoder=DecoderFactory.get().binaryDecoder(out.toByteArray(),null);
//        GenericRecord result=reader.read(null,decoder);
//        System.out.println(result.get("name").toString());
//        System.out.println(result.get("favorite_number").toString());
//        System.out.println(result.get("favorite_color").toString());
//        out.close();

        File diskFile = new File("userlist1.txt");
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(AvroTest.class.getClassLoader().getResourceAsStream("avro/user.avsc"));
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(diskFile, datumReader);
        GenericRecord _current = null;
        while (dataFileReader.hasNext()) {
            _current = dataFileReader.next(_current);
            System.out.println(_current);
        }

        dataFileReader.close();
    }

}
