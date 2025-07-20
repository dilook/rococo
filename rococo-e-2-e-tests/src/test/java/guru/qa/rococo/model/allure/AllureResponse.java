package guru.qa.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record AllureResponse(
    @JsonProperty("data")
    Map<String, Object> data,
    
    @JsonProperty("meta_data")
    Map<String, Object> metaData
) {
}