import java.util.*;
import java.util.stream.*;

public class Java8Example2 implements java.io.Serializable, Runnable {

    private static final long serialVersionUID = 1L;
    public static final float E = 2.7182818284590452354f;
    public static final double PI = 3.14159265358979323846;
    public static final char DOT = '.';

    private static transient volatile StringBuffer STRING_BUFFER = new StringBuffer();

    public synchronized void run() {
        hello("Hello", "World", "hi");
    }

    void hello(String... args) {
        Arrays.stream(args).forEach(System.out::println);
        STRING_BUFFER.append(DOT);
    }

    float rem(float a, float b) {
        return a % b;
    }
}
