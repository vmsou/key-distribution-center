package secure;


import org.json.JSONArray;
import org.json.JSONObject;

public class Client {
    Users users;
    Messages messages;

    // Constructors
    public Client() {
        users = getUsersData("data/users.json");
        messages = getMessagesData("data/messages.json");
    }

    // Methods
    public void add(UserEntity u) { users.put(u.getId(), u); }

    public void send(Message m) { messages.put(m.getId(), m); }

    public Users getUsersData(String filename) {
        Users usrs = new Users();
        int id = UserEntity.count;
        String file = Engine.fstream(filename);
        if (file == null || file.isEmpty()) return usrs;
        JSONArray arr = new JSONArray(file);
        if (arr.isEmpty()) return usrs;

        for (int i = 0; i < arr.length(); ++i) {
            JSONObject obj = arr.getJSONObject(i);
            id = obj.getInt("id");
            usrs.put(obj.getInt("id"), new UserEntity(obj));
            ++id;
        }

        UserEntity.count = id;
        return usrs;
    }

    public Messages getMessagesData(String filename) {
        Messages msgs = new Messages();
        int id = Message.count;
        String file = Engine.fstream(filename);
        if (file == null || file.isEmpty()) return msgs;
        JSONArray arr = new JSONArray(file);
        if (arr.isEmpty()) return msgs;

        for (int i = 0; i < arr.length(); ++i) {
            JSONObject obj = arr.getJSONObject(i);
            id = obj.getInt("id");
            msgs.put(id, new Message(obj));
            ++id;
        }
        Message.count = id;
        return msgs;

    }

    public void showUsers() {
        for (UserEntity u : users.values())
            System.out.println(u);
    }

    public void showMessages() {
        for (Message m : messages.values())
            System.out.println(m);
    }

}
