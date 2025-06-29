package guru.qa.rococo.api;

import guru.qa.rococo.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserdataApi {

  @GET("user")
  Call<UserJson> currentUser(@Query("username") String username);

  @POST("user")
  Call<UserJson> updateUserInfo(@Body UserJson user);

}
