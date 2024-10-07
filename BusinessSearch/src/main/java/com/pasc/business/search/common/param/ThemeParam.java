package com.pasc.business.search.common.param;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangzijian
 * @date 2019/3/7
 * @des
 * @modify
 **/
public class ThemeParam {
    @SerializedName ("entranceLocation")
    public String entranceLocation;

    @SerializedName ("entranceId")
    public String entranceId;

    public ThemeParam(String entranceLocation,String entranceId) {
        this.entranceLocation = entranceLocation;
        this.entranceId = entranceId;
    }
}
