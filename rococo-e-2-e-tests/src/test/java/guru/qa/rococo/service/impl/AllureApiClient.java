package guru.qa.rococo.service.impl;

import guru.qa.rococo.api.AllureDockerApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.allure.AllureProject;
import guru.qa.rococo.model.allure.AllureResponse;
import guru.qa.rococo.model.allure.AllureResults;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllureApiClient extends RestClient {

    private final AllureDockerApi allureDockerApi;


    public AllureApiClient(String baseUrl) {
        super(baseUrl, HttpLoggingInterceptor.Level.BASIC);
        allureDockerApi = create(AllureDockerApi.class);
    }

    public void getOrCreateProject(@Nonnull String name) {
        Response<AllureResponse> response;
        try {
            response = allureDockerApi.getProject(name).execute();
            if (response.code() == 404) {
                response = allureDockerApi.createProject(new AllureProject(name)).execute();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(201, response.code());
    }

    public void sendResults(String projectId, @Nonnull AllureResults results) {
        Response<AllureResponse> response;
        try {
            response = allureDockerApi.sendResults(projectId, false, results).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }

    public void generateReport(@Nonnull String projectId,
                               @Nonnull String executionName,
                               String executionFrom,
                               String executionType) {
        Response<AllureResponse> response;
        try {
            response = allureDockerApi.generateReport(projectId, executionName, executionFrom, executionType).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }
}
