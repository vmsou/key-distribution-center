package secure;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public interface Lambda { int perform(int x); }

class Lambdas extends HashMap<Integer, Lambda> {
    public Lambdas(Client source, JSONObject obj) {
        for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
            String k = it.next();
            put(Integer.parseInt(k), source.lambdas[obj.getInt(k)]);
        }
    }
}
