package secure;

import org.json.JSONArray;

import java.util.HashMap;

public abstract class EntityContainer<T extends Entity> extends HashMap<Integer, T> {
    public String toSave() { return toJSON().toString(1); }

    public JSONArray toJSON() {
        JSONArray arr = new JSONArray();
        for (T t : values())
            arr.put(t.toJSON());

        return arr;
    }
}

class Users extends EntityContainer<UserEntity> {}
class Messages extends EntityContainer<Message> {}
class MasterKeys extends HashMap<Integer, SecretKey> {}