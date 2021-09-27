package secure;

import org.json.JSONArray;
import org.json.JSONObject;

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

class Users extends EntityContainer<UserEntity> {
    public Users() { super(); }

    public Users(Client source, JSONArray arr) {
        super();
        int id = UserEntity.count;
        for (int i = 0; i < arr.length(); ++i) {
            JSONObject obj = arr.getJSONObject(i);
            id = obj.getInt("id");
            put(obj.getInt("id"), new UserEntity(source, obj));
            ++id;
        }
        UserEntity.count = id;
    }
}

class Messages extends EntityContainer<Message> {
    public Messages() { super(); }

    public Messages(JSONArray arr) {
        super();
        int id = Message.count;
        for (int i = 0; i < arr.length(); ++i) {
            JSONObject obj = arr.getJSONObject(i);
            id = obj.getInt("id");
            put(id, createMessage(obj));
            ++id;
        }
        Message.count = id;
    }

    public Message createMessage(JSONObject obj) {
        return switch (obj.getString("name")) {
            case "PROOF" -> new ProofMessage(obj);
            case "SESSIONS" -> new SessionsMessage(obj);
            case "SESSION" -> new SessionMessage(obj);
            case "NONCE" -> new NonceMessage(obj);
            case "SEND" -> new SendMessage(obj);
            default -> new Message(obj);
        };
    }
}