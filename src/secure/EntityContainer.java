package secure;

import java.util.ArrayList;

public abstract class EntityContainer<T> extends ArrayList<T>  {}

class Users extends EntityContainer<UserEntity> {}
class Messages extends EntityContainer<Message> {}