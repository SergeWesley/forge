import org.springframework.web.util.UriComponentsBuilder;
public class Test {
    public static void main(String[] args) {
        System.out.println("Methods in UriComponentsBuilder:");
        for (java.lang.reflect.Method m : UriComponentsBuilder.class.getMethods()) {
            if (m.getName().startsWith("from")) {
                System.out.println(m.getName() + " " + java.util.Arrays.toString(m.getParameterTypes()));
            }
        }
    }
}
