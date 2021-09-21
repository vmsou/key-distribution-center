package secure;

import java.util.HashMap;

public abstract class EntityContainer<T> extends HashMap<Integer, T> {}

class Users extends EntityContainer<UserEntity> {}
class Messages extends EntityContainer<Message> {}
class MasterKeys extends EntityContainer<MasterKey> {}