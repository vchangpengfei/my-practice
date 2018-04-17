package com.dang.practice.bdb;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Created by pfchang on 2018/3/29.
 */
@com.sleepycat.persist.model.Entity
public class Entity {


    private static DataAccessor dataAccessor;



    public static class DataAccessor{
        PrimaryIndex<Integer, Entity> envStatsByHotelid;
        SecondaryIndex<Long, Integer, Entity> envStatsBytime;
        public DataAccessor(EntityStore entityStore) {
            envStatsByHotelid = entityStore.getPrimaryIndex(Integer.class,Entity.class);
            envStatsBytime = entityStore.getSecondaryIndex(envStatsByHotelid, Long.class, "dataChange_CreateTime");
        }
    }


    @PrimaryKey
    private int hotelid;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private long dataChange_CreateTime;

    private String hotelname;

    public Entity() {

    }

    public Entity(int hotelid, long dataChange_CreateTime, String hotelname) {
        this.hotelid = hotelid;
        this.dataChange_CreateTime = dataChange_CreateTime;
        this.hotelname = hotelname;
    }

    public int getHotelid() {
        return hotelid;
    }

    public void setHotelid(int hotelid) {
        this.hotelid = hotelid;
    }

    public long getDataChange_CreateTime() {
        return dataChange_CreateTime;
    }

    public void setDataChange_CreateTime(long dataChange_CreateTime) {
        this.dataChange_CreateTime = dataChange_CreateTime;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
    }

    @Override
    public String toString() {
        return hotelname+":"+hotelid+":"+dataChange_CreateTime;
    }
}
