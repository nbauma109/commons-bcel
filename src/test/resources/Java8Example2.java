import java.util.*;
import java.util.stream.*;

public class Java8Example2 implements java.io.Serializable, Runnable {

    private static final long serialVersionUID = 1L;
    public static final float E = 2.7182818284590452354f;
    public static final double PI = 3.14159265358979323846;

    private static transient volatile StringBuffer STRING_BUFFER = new StringBuffer();

    public synchronized void run() {
        hello("Hello", "World", "hi");
    }

    void hello(String... args) {
        List<String> words = Arrays.asList(args);
        System.out.println(words);

        List<String> words2 = words.stream().filter((String s) -> s.length() > 2).collect(Collectors.<String> toList());
        System.out.println(words2);
    }

    float rem(float a, float b) {
        return a % b;
    }
}
