import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

public class Java4Example {

    /*
     * Example for RET instruction
     */
    public static void serialize(Serializable obj, OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);
            
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    void tableSwitch(int inputValue) {
        switch (inputValue) {
            case 1:  System.out.println("One");
            case 2:  System.out.println("Two");
            case 3:  System.out.println("Three");
            default: throw new IllegalArgumentException();
        }
    }

    void lookupSwitch(int inputValue) {
        switch (inputValue) {
            case 1:  System.out.println("One");
            case 1000:  System.out.println("One thousand");
            case 1000000:  System.out.println("One million");
            default: throw new IllegalArgumentException();
        }
    }
}
