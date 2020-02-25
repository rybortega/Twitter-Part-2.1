package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    TextView tvCharCount;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnTweet = findViewById(R.id.btnTweet);

        //initialize the tweet button to be at a disabled state and set the colors accordingly.
        btnTweet.setTextColor(Color.LTGRAY);
        btnTweet.setBackgroundColor(Color.parseColor("#4daab8c2"));
        btnTweet.setEnabled(false);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Fires right before text is changing

                String text = etCompose.getText().toString();
                int charactersLeft = 280 - text.length();
                tvCharCount.setText("" + charactersLeft);


                if(charactersLeft < 0 || charactersLeft == 280) {
                    btnTweet.setTextColor(Color.LTGRAY);
                    btnTweet.setBackgroundColor(Color.parseColor("#4daab8c2"));
                    btnTweet.setEnabled(false);}

                else if (charactersLeft >= 0){
                    tvCharCount.setTextColor(Color.parseColor("#BC030303"));
                    btnTweet.setBackgroundColor(Color.parseColor("#ff1da1f2"));
                    btnTweet.setEnabled(true);
                    btnTweet.setTextColor(Color.WHITE);

                    if(charactersLeft > 0 && charactersLeft <= 20) {
                        tvCharCount.setTextColor(Color.parseColor("#FFCE30"));
                    }
                    if(charactersLeft < 1) {
                        tvCharCount.setTextColor(Color.parseColor("#FF0000"));
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Fires right after the text has changed

            }
        });

        //set click listener on the button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();

                if(tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be longer that 280 characters :(", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();

                //make an API call to Twitter to publish the tweet.
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "on Success to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Tweet Published: " + tweet.body);

                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            //set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            //close the activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "of failure to publish tweet", throwable);
                    }
                });
            }
        });


    }
}
