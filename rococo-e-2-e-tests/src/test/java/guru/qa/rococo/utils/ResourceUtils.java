package guru.qa.rococo.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Base64;

public class ResourceUtils {

    public static String getDataImageBase64FromResource(String path) {
        byte[] imageBytes;
        try {
            imageBytes = new ClassPathResource(path).getContentAsByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
