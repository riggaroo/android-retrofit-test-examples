package za.co.riggaroo.retrofittestexample;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import junit.framework.Assert;

import java.lang.annotation.Annotation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import retrofit.Call;
import retrofit.Converter;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.mock.CallBehaviorAdapter;
import retrofit.mock.MockRetrofit;
import retrofit.mock.NetworkBehavior;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayErrorResponse;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

/**
 * @author rebeccafranks
 * @since 15/10/23.
 */
public class QuoteOfTheDayMockAdapterTest extends InstrumentationTestCase {
    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        retrofit = new Retrofit.Builder().baseUrl("http://test.com")
                .client(new OkHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();


        // Create the Behavior object which manages the fake behavior and the background executor.
        NetworkBehavior behavior = NetworkBehavior.create();

        ExecutorService bg = Executors.newSingleThreadExecutor();

        // Create the mock implementation and use MockRetrofit to apply the behavior to it.
        mockRetrofit = new MockRetrofit(behavior, new CallBehaviorAdapter(retrofit, bg));


    }

    @SmallTest
    public void testRandomQuoteRetrieval() throws Exception {
        //Setup
        QuoteOfTheDayRestService mockQodService = new MockQuoteOfTheDayService(retrofit);

        QuoteOfTheDayRestService qodService = mockRetrofit.create(QuoteOfTheDayRestService.class, mockQodService);

        //Actual Test
        Call<QuoteOfTheDayResponse> quote = qodService.getQuoteOfTheDay();
        Response<QuoteOfTheDayResponse> quoteOfTheDayResponse = quote.execute();

        //Asserting response
        Assert.assertTrue(quoteOfTheDayResponse.isSuccess());
        Assert.assertEquals("Always code as if the guy who ends up maintaining your code will be a violent psychopath who knows where you live.", quoteOfTheDayResponse.body().getContents().getQuotes().get(0).getQuote());

    }

    @SmallTest
    public void testQuoteUnauthorised() throws Exception {
        //Setup
        String jsonResponseFileName = "quote_404_not_found.json";
        MockWebServer server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), jsonResponseFileName)));

        Retrofit retrofit2 = new Retrofit.Builder().baseUrl(server.url("/"))
                .client(new OkHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        QuoteOfTheDayRestService qodService = retrofit2.create(QuoteOfTheDayRestService.class);


        //Actual Test
        Call<QuoteOfTheDayResponse> quote = qodService.getQuoteOfTheDay();
        Response<QuoteOfTheDayResponse> quoteOfTheDayResponse = quote.execute();
        Converter<ResponseBody, QuoteOfTheDayErrorResponse> errorConverter =
                retrofit.responseConverter(QuoteOfTheDayErrorResponse.class, new Annotation[0]);
        // Convert the error body into our Error type.
        QuoteOfTheDayErrorResponse error = errorConverter.convert(quoteOfTheDayResponse.errorBody());

        //Asserting response
        Assert.assertFalse(quoteOfTheDayResponse.isSuccess());
        Assert.assertEquals(404, quoteOfTheDayResponse.code());
        Assert.assertEquals("Quote Not found", error.getError().getMessage());

    }
}