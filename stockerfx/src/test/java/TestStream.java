import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public class TestStream {
    public static void main(String[] args) {
        Stream<String> lsStr = Stream.of("a", "b", "c");
        String target = "d";
        boolean hasA = lsStr.anyMatch(s -> StringUtils.equals(s, target));
        System.out.println(hasA);
    }
}
