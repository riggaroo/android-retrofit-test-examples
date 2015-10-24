package za.co.riggaroo.retrofittestexample;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.Query;
import retrofit.mock.Calls;
import za.co.riggaroo.retrofittestexample.pojo.Contents;
import za.co.riggaroo.retrofittestexample.pojo.Quote;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

/**
 * @author rebeccafranks
 * @since 15/10/24.
 */
public class MockQuoteOfTheDayService implements QuoteOfTheDayRestService {
    private final Retrofit retrofit;

    public MockQuoteOfTheDayService(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public Call<QuoteOfTheDayResponse> getQuoteOfTheDay(@Query("apiKey") String apiKey) {
        QuoteOfTheDayResponse quoteOfTheDayResponse = new QuoteOfTheDayResponse();
        Contents contents = new Contents();
        Quote quote = new Quote();
        quote.setQuote("Always code as if the guy who ends up maintaining your code will be a violent psychopath who knows where you live.");
        ArrayList<Quote> quotes = new ArrayList<>();
        quotes.add(quote);
        contents.setQuotes(quotes);
        quoteOfTheDayResponse.setContents(contents);
        return Calls.response(quoteOfTheDayResponse, retrofit);

    }
}
