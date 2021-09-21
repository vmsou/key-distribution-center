package secure;

import java.util.HashMap;

public abstract class EntityContainer<T extends Entity> extends HashMap<Integer, T> {
    public String toSave() {
        StringBuilder str = new StringBuilder();
        for (T t : values())
            str.append(t.toSave()).append("\n");
        return str.toString();
    }
}

class Users extends EntityContainer<UserEntity> {}
class Messages extends EntityContainer<Message> {}
class MasterKeys extends HashMap<Integer, SecretKey> {}