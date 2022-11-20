import java.io.Serializable;
import java.util.*;
import java.util.stream.*;

public class Java8Example2 implements java.io.Serializable, Runnable {

    private static final long serialVersionUID = 1234567891234567891L;
    public static final float E = 2.7182818284590452354f;
    public static final double PI = 3.14159265358979323846;
    public static final char DOT = '.';
    public static final short PORT = 22;
    public static final float ZERO = 0f;
    public static final float ONE = 1f;
    public static final byte INVOKESTATIC = (byte) 184;
    public static final int[][] MULTI_ARRAY = {{0}, {1}};
    public static final int[][] MULTI_ARRAY2 = new int[2][2];

    private static transient volatile StringBuffer STRING_BUFFER = new StringBuffer();

    public synchronized void run() {
        try {
            hello("Hello", "World", "hi");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void check(String... args) throws Exception {
        if (!(args instanceof String[]) || args.getClass() != String[].class) {
            throw new Exception();
        }
    }
    
    void hello(String... args) throws Exception {
        check(args);
        Arrays.stream(args).forEach(System.out::println);
        STRING_BUFFER.append(DOT);
        STRING_BUFFER.append(INVOKESTATIC);
        STRING_BUFFER.append(frem(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
        STRING_BUFFER.append(frem(Float.NaN, Float.NaN));
        STRING_BUFFER.append(frem(E, E));
        STRING_BUFFER.append(drem(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        STRING_BUFFER.append(drem(Double.NaN, Double.NaN));
        STRING_BUFFER.append(drem(PI, PI));
        STRING_BUFFER.append(serialVersionUID);
        STRING_BUFFER.append(Arrays.deepToString(MULTI_ARRAY));
    }

    double drem(double a, double b) {
        return a % b;
    }
    
    float frem(float a, float b) {
        return a % b;
    }
}
