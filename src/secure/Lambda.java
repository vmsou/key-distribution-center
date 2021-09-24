package secure;

import java.util.HashMap;

public interface Lambda { int perform(int x); }

class Lambdas extends HashMap<Integer, Lambda> {}
