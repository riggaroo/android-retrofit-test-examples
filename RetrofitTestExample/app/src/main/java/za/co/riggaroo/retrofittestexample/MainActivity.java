package za.co.riggaroo.retrofittestexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;

import retrofit.Call;
import retrofit.Callback;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import za.co.riggaroo.retrofittestexample.interceptor.LoggingInterceptor;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuoteOfTheDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewQuoteOfTheDay = (TextView)findViewById(R.id.text_view_quote);
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.theysaidso.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        QuoteOfTheDayRestService service = retrofit.create(QuoteOfTheDayRestService.class);



        Call<QuoteOfTheDayResponse> call =
                service.getQuoteOfTheDay("test");

        call.enqueue(new Callback<QuoteOfTheDayResponse>() {

            @Override
            public void onResponse(Response<QuoteOfTheDayResponse> response, Retrofit retrofit) {
                Log.d("MainActivity", "call executed");
                textViewQuoteOfTheDay.setText(response.body().getContents().getQuotes().get(0).getQuote());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
