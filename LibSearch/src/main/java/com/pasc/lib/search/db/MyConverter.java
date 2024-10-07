package com.pasc.lib.search.db;

import com.raizlabs.android.dbflow.converter.TypeConverter;

//@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MyConverter extends TypeConverter<String, String> {
    @Override
    public String getDBValue(String model) {
        return model;
    }

    @Override
    public String getModelValue(String data) {
        return data;
    }
}
