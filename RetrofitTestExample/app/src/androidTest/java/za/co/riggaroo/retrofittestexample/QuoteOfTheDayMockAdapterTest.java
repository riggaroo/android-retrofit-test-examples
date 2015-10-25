package za.co.riggaroo.retrofittestexample;

import android.test.InstrumentationTestCase;

import com.squareup.okhttp.OkHttpClient;

import junit.framework.Assert;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.Call;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.mock.CallBehaviorAdapter;
import retrofit.mock.MockRetrofit;
import retrofit.mock.NetworkBehavior;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

/**
 * @author rebeccafranks
 * @since 15/10/23.
 */
public class QuoteOfTheDayMockAdapterTest extends InstrumentationTestCase {
    private MockRetrofit mockRetrofit;
    private Retrofit retrofit;
    private QuoteOfTheDayRestService qodService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        retrofit = new Retrofit.Builder().baseUrl("http://test.com")
                .client(new OkHttpClient())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();

        ExecutorService bg = Executors.newSingleThreadExecutor();

        mockRetrofit = new MockRetrofit(behavior, new CallBehaviorAdapter(retrofit, bg));

    }


    @Test
    public void testRandomQuoteRetrieval() throws Exception {
        QuoteOfTheDayRestService mockQodService = new MockQuoteOfTheDayService(retrofit);

        qodService = mockRetrofit.create(QuoteOfTheDayRestService.class, mockQodService);

        //Actual Test
        Call<QuoteOfTheDayResponse> quote = qodService.getQuoteOfTheDay();
        Response<QuoteOfTheDayResponse> quoteOfTheDayResponse = quote.execute();

        //Asserting response
        Assert.assertTrue(quoteOfTheDayResponse.isSuccess());
        Assert.assertEquals("Always code as if the guy who ends up maintaining your code will be a violent psychopath who knows where you live.", quoteOfTheDayResponse.body().getContents().getQuotes().get(0).getQuote());

    }

}