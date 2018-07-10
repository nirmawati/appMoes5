package com.example.nirma.moes5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        String title = getIntent().getStringExtra("title");
        String category = getIntent().getStringExtra("category");

        ((TextView) findViewById(R.id.txt_title)).setText(title);
        ((TextView) findViewById(R.id.txt_title)).setText(category);

        ((WebView) findViewById(R.id.wv_post)).loadData(
                getIntent().getStringExtra("content"),
                "text/html",
                "utf-8"
        );

    }
}
