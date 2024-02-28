package com.cinntra.roomdb;

import androidx.room.TypeConverter;

import com.cinntra.ledger.newapimodel.SubGroupItemStock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SubGroupItemConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<SubGroupItemStock> fromString(String value) {
        return gson.fromJson(value, new TypeToken<ArrayList<SubGroupItemStock>>() {}.getType());
    }

    @TypeConverter
    public static String fromList(ArrayList<SubGroupItemStock> list) {
        return gson.toJson(list);
    }
}
