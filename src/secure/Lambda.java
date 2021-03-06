package secure;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public abstract class Lambda {
    public static int count = 0;
    protected int id = 0;

    Lambda() { id = count; ++count; }

    abstract int perform(int x);
}

class Lambdas extends HashMap<Integer, Lambda> {
    public Lambdas(Client source, JSONObject obj) {
        for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
            String k = it.next();
            put(Integer.parseInt(k), source.lambdas[obj.getInt(k)]);
        }
    }

    public String toSave() { return toJSON().toString(1); }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        for (Integer to : keySet()) {
            obj.put(String.valueOf(to), get(to).id);
        }


        return obj;
    }
}
