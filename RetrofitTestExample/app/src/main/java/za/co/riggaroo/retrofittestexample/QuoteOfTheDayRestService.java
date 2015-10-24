package za.co.riggaroo.retrofittestexample;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

/**
 * @author rebeccafranks
 * @since 15/10/23.
 */
public interface QuoteOfTheDayRestService {

    @GET("/qod.json")
    Call<QuoteOfTheDayResponse> getQuoteOfTheDay(@Query("apiKey") String apiKey);

}
