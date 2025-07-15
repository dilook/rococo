package guru.qa.rococo.utils;

public class GrpcUtils {

    public static String safe(Object object) {
        return object != null ? object.toString() : "";
    }
}
