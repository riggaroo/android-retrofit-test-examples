package za.co.riggaroo.retrofittestexample;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.mockwebserver.MockResponse;

import retrofit.Call;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.mock.CallBehaviorAdapter;
import retrofit.mock.Calls;
import za.co.riggaroo.retrofittestexample.pojo.*;
import za.co.riggaroo.retrofittestexample.pojo.Error;

/**
 * @author rebeccafranks
 * @since 15/10/25.
 */
public class MockFailedQODService implements QuoteOfTheDayRestService {
    private static final String TAG = "MockFailedQOD";
    private final Retrofit retrofit;

    public MockFailedQODService(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public Call<QuoteOfTheDayResponse> getQuoteOfTheDay() {
        za.co.riggaroo.retrofittestexample.pojo.Error error = new Error();
        error.setCode(404);
        error.setMessage("Quote Not Found");
        QuoteOfTheDayErrorResponse quoteOfTheDayErrorResponse = new QuoteOfTheDayErrorResponse();
        quoteOfTheDayErrorResponse.setError(error);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = "";
        try {
            json = ow.writeValueAsString(quoteOfTheDayErrorResponse);
            Response response = Response.error(404, ResponseBody.create(MediaType.parse("application/json"),json));
            return Calls.response(response, retrofit);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "JSON Processing exception:",e);
            return Calls.failure(e);
        }

    }
}
