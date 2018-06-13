package com.dang.practice.apitest.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 *
 * 项目根目录下运行如下命令
 *  protoc.exe -I=src/main/resources/ --java_out=src/main/java src/main/resources/protobuf.proto
 *
 *
 */

public class TestGpsProtobuf {
    public static void main(String[] args) {
        System.out.println("===== 构建一个GPS模型开始 =====");
        GpsData.gps_data.Builder gps_builder = GpsData.gps_data.newBuilder();
        gps_builder.setAltitude(1);
        gps_builder.setDataTime("2017-12-17 16:21:44");
        gps_builder.setGpsStatus(1);
        gps_builder.setLat(39.123);
        gps_builder.setLon(120.112);
        gps_builder.setDirection(30.2F);
        gps_builder.setId(100L);
        gps_builder.addTestrepeated("10");
        gps_builder.addTestrepeated("20s");

        GpsData.gps_data gps_data = gps_builder.build();
        System.out.println(gps_data.toString());
        System.out.println("===== 构建GPS模型结束 =====");

        System.out.println("===== gps Byte 开始=====");
        for(byte b : gps_data.toByteArray()){
            System.out.print(b);
        }
        System.out.println("\n" + "bytes长度" + gps_data.toByteString().size());
        System.out.println("===== gps Byte 结束 =====");

        System.out.println("===== 使用gps 反序列化生成对象开始 =====");
        GpsData.gps_data gd = null;
        try {
            gd = GpsData.gps_data.parseFrom(gps_data.toByteArray());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        System.out.print(gd.toString());
        System.out.println("===== 使用gps 反序列化生成对象结束 =====");

    }
}