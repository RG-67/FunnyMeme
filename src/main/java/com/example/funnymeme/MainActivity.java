package com.example.funnymeme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ImageView memeView;
    private Button shareBtn, nextBtn;
    private ProgressBar loadBar;
    private String memeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memeView = findViewById(R.id.memeView);
        shareBtn = findViewById(R.id.shareBtn);
        nextBtn = findViewById(R.id.nextBtn);
        loadBar = findViewById(R.id.loadBar);

        loadMeme();

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Checkout the funny meme.. "+memeUrl);
                intent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(intent, "Share via");
                startActivity(shareIntent);

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMeme();
            }
        });
    }

    private void loadMeme() {
        loadBar.setVisibility(View.VISIBLE);
        String url = "https://meme-api.herokuapp.com/gimme";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    memeUrl = response.getString("url");
                    Glide.with(MainActivity.this).load(memeUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            loadBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Loading failed", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loadBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(memeView);
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error: JSONException e ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                loadBar.setVisibility(View.GONE);
            }
    });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}