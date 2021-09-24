package secure;

import java.util.HashMap;

public abstract class Lambda { public abstract int perform(int x); }

class Lambdas extends HashMap<Integer, Lambda> {}
