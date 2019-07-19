package com.revolut.common;

import java.time.ZoneId;

public class AppUtility {

    public static final ZoneId LOCAL_ZONE_ID = ZoneId.of("get from props");
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

   /*public static String getTimeId(){
        return ZONE_ID
    }*/
}
