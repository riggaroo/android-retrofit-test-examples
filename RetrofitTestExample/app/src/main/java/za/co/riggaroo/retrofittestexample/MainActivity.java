package za.co.riggaroo.retrofittestexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Converter;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import za.co.riggaroo.retrofittestexample.interceptor.LoggingInterceptor;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayErrorResponse;
import za.co.riggaroo.retrofittestexample.pojo.QuoteOfTheDayResponse;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuoteOfTheDay;
    private Button buttonRetry;

    private static final String TAG = "MainActivity";
    private QuoteOfTheDayRestService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewQuoteOfTheDay = (TextView) findViewById(R.id.text_view_quote);
        buttonRetry = (Button) findViewById(R.id.button_retry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQuoteOfTheDay();
            }
        });

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QuoteOfTheDayConstants.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(QuoteOfTheDayRestService.class);
        getQuoteOfTheDay();

    }


    private void getQuoteOfTheDay() {
        Call<QuoteOfTheDayResponse> call =
                service.getQuoteOfTheDay();

        call.enqueue(new Callback<QuoteOfTheDayResponse>() {

            @Override
            public void onResponse(Response<QuoteOfTheDayResponse> response, Retrofit retrofit) {
                Log.d(TAG, "onResponse quoteOfTheDay");

                if (response.isSuccess()) {
                    textViewQuoteOfTheDay.setText(response.body().getContents().getQuotes().get(0).getQuote());
                } else {
                    try {
                        Converter<ResponseBody, QuoteOfTheDayErrorResponse> errorConverter = retrofit.responseConverter(QuoteOfTheDayErrorResponse.class, new Annotation[0]);
                        QuoteOfTheDayErrorResponse error = errorConverter.convert(response.errorBody());
                        showRetry(String.valueOf(error.getError().getMessage()));

                    } catch (IOException e) {
                        Log.e(TAG, "IOException parsing error:", e);
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                showRetry(getString(R.string.failed_to_load_quote));
            }
        });
    }

    private void showRetry(String error) {
        textViewQuoteOfTheDay.setText(error);
        buttonRetry.setVisibility(View.VISIBLE);

    }
}
