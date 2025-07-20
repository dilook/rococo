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

    public AllureApiClient() {
        super(CFG.allureDockerServiceUrl(), HttpLoggingInterceptor.Level.BASIC);
        allureDockerApi = create(AllureDockerApi.class);
    }

    public void createProjectIfNotExist(@Nonnull String name) {
        Response<AllureResponse> response;
        try {
            response = allureDockerApi.getProject(name).execute();
            if (response.code() == 404) {
                response = allureDockerApi.createProject(new AllureProject(name)).execute();
                assertEquals(201, response.code());
            } else {
                assertEquals(200, response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void cleanResults(@Nonnull String projectId) {
        Response<AllureResponse> response;
        try {
            response = allureDockerApi.cleanResults(projectId).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }

    public void generateReport(@Nonnull String projectId) {
        Response<AllureResponse> response;
        try {
            response = allureDockerApi.generateReport(
                    projectId,
                    System.getenv("HEAD_COMMIT_MESSAGE"),
                    System.getenv("BUILD_URL"),
                    System.getenv("EXECUTION_TYPE")
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }
}
