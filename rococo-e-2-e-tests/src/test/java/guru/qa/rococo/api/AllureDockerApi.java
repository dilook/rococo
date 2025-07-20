package guru.qa.rococo.api;

import guru.qa.rococo.model.allure.AllureProject;
import guru.qa.rococo.model.allure.AllureResponse;
import guru.qa.rococo.model.allure.AllureResults;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface AllureDockerApi {

    @POST("projects")
    Call<AllureResponse> createProject(@Body AllureProject project);

    @GET("projects/{id}")
    Call<AllureResponse> getProject(@Path("id") String id);

    @POST("send-results")
    Call<AllureResponse> sendResults(@Query("project_id") @Nullable String projectId,
                                     @Query("force_project_creation") @Nullable Boolean forceProjectCreation,
                                     @Body AllureResults results);

    @GET("generate-report")
    Call<AllureResponse> generateReport(@Query("project_id") @Nullable String projectId,
                                        @Query("execution_name") @Nullable String executionName,
                                        @Query("execution_from") @Nullable String executionFrom,
                                        @Query("execution_type") @Nullable String executionType);
}
