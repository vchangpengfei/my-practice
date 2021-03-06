package com.dang.practice.bdb;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public class DataAccessor {
    PrimaryIndex<Integer, Entity> envStatsByHotelid;
    SecondaryIndex<Long, Integer, Entity> envStatsBytime;
    public DataAccessor(EntityStore entityStore) {
        envStatsByHotelid = entityStore.getPrimaryIndex(Integer.class,Entity.class);
        envStatsBytime = entityStore.getSecondaryIndex(envStatsByHotelid, Long.class, "dataChange_CreateTime");
    }
}