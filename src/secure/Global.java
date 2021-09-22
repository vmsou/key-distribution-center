package secure;


import org.json.JSONArray;
import org.json.JSONObject;

public class Global {
    Users users;
    Messages messages;

    // Constructors
    public Global() {
        users = getUsersData("data/users.json");
        messages = getMessagesData("data/messages.json");
    }

    // Methods
    public void add(UserEntity u) { users.put(u.getId(), u); }
    public void add(Message m) { messages.put(m.getId(), m); }

    public Users getUsersData(String filename) {
        int id = 1;
        Users users = new Users();
        String sc = Engine.fstream(filename);

        UserEntity.count = id;
        return users;
    }

    public Messages getMessagesData(String filename) {
        Messages messages = new Messages();

        String file = Engine.fstream(filename);
        if (file == null) return null;
        JSONArray arr = new JSONArray(file);
        if (arr.isEmpty()) return messages;

        for (int i = 0; i < arr.length(); ++i) {
            JSONObject obj = arr.getJSONObject(i);
            messages.put(obj.getInt("id"), new Message(obj));
        }
        return messages;

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
