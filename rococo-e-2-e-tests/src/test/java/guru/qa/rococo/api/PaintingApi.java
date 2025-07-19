package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.PaintingJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.UUID;

public interface PaintingApi {

    @GET("api/painting")
    Call<RestResponsePage<PaintingJson>> getAllPainting(@Query("page") int page,
                                                       @Query("size") int size,
                                                       @Query("title") String title);

    @GET("api/painting/{id}")
    Call<PaintingJson> getPaintingById(@Path("id") UUID id);

    @PATCH("api/painting")
    Call<PaintingJson> updatePainting(@Header("Authorization") String bearerToken,
                                     @Body PaintingJson paintingJson);

    @POST("api/painting")
    Call<PaintingJson> createPainting(@Header("Authorization") String bearerToken,
                                     @Body PaintingJson paintingJson);
}