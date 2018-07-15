package dropdevs.droidbrowser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    EditText url;
    ImageView back;
    ImageView forward;
    ImageView refresh;
    ImageView mic;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.web_view);
        url = (EditText) findViewById(R.id.url);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new MyViewClient());
        webView.loadUrl("http://www.google.com");
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);


        url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String theWebsite = url.getText().toString();
                    String theRealWebsite = "";
                    if (!theWebsite.startsWith("http://") || theWebsite.startsWith("www."))
                        theRealWebsite = "http://" + theWebsite;

                    if (Patterns.WEB_URL.matcher(theRealWebsite).matches()) {
                        webView.loadUrl(theRealWebsite);
                    } else if (theWebsite.equals("")) {
                        Toast.makeText(MainActivity.this, "Please enter URL or search words", Toast.LENGTH_LONG).show();
                    } else {
                        webView.loadUrl("https://www.google.com/search?q=" + theWebsite);
                    }
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(url.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    public class MyViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView v, String url) {
            v.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            String page_url = view.getUrl();
            EditText bar = (EditText)findViewById(R.id.url);
            bar.setText(page_url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void back(View v) {
        back = (ImageView) findViewById(R.id.back);
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    public void forward(View v) {
        forward = (ImageView) findViewById(R.id.forward);
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    public void refresh(View v) {
        refresh = (ImageView) findViewById(R.id.refresh);
        webView.reload();
    }

    public void mic(View v) {
        mic = (ImageView) findViewById(R.id.mic);
        promptSpeechInput();

    }

    public void promptSpeechInput() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak search words or URL");
        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, "Sorry your device doesn't support speech language!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    url.setText(result.get(0));
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(url.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();
    }

}
