package com.journalpro.sdp.journalpro;

import java.io.Serializable;
import java.util.HashMap;

class JE_Detail implements Serializable {

    String uuid;
    String name;
    String description;
    String date;

    JE_Detail(String uuid, String name, String description, String date) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    JE_Detail() {

    }

    HashMap<String, String> passingToHashMap() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("name", name);
        result.put("date", date);
        result.put("description", description);
        result.put("uuid", uuid);
        return result;
    }
}
