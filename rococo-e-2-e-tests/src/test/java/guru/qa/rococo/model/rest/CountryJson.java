package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.grpc.Country;

import java.util.UUID;


public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name
) {

        public Country toGrpc() {
                return Country.newBuilder()
                        .setId(id.toString())
                        .setName(name)
                        .build();
        }
}
