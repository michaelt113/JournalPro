package sdp.journalpro;

import java.io.Serializable;
import java.util.HashMap;

class JE_Detail implements Serializable {

    // Entry variables
    String uuid;
    String name;
    String description;
    String date;

    // Constructor
    JE_Detail(String uuid, String name, String description, String date) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    JE_Detail() {
        // what is this for??
    }

    // Generates hash for Firebase
    HashMap<String, String> passingToHashMap() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("name", name);
        result.put("date", date);
        result.put("description", description);
        result.put("uuid", uuid);
        return result;
    }
}