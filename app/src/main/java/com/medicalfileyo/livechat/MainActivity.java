package com.medicalfileyo.livechat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


class DetectConnection {
    public static boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected());
    }
}


public class MainActivity extends AppCompatActivity {
    private static final String myUrl = "https://uglivechat.herokuapp.com";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String offlineHTML = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "  <head>\n" + "    <meta charset=\"UTF-8\" />\n" + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" + "    <title>Live Chat|Offline</title>\n" + "    <style>\n" + "      body {\n" + "        height: 100vh;\n" + "        display: grid;\n" + "        place-items: center;\n" + "        background-color: #eee;\n" + "      }\n" + "\n" + "      button {\n" + "        background-color: inherit;\n" + "        color: #222;\n" + "        outline: 0;\n" + "        border: 1px solid #cdcdcd;\n" + "        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);\n" + "        padding: 0.5rem 1rem;\n" + "        width: 150px;\n" + "        cursor: pointer;\n" + "        border-radius: 4px;\n" + "        font-size: 1rem;\n" + "      }\n" + "    </style>\n" + "  </head>\n" + "  <body>\n" + "    <div id=\"root\">\n" + "      <h2>Offline</h2>\n" + "      <p>You have no internet connection!</p>\n" + "      <button onclick=\"location.reload();\">Reload</button>\n" + "    </div>\n" + "  </body>\n" + "</html>";
    private static final String errorHTML = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n" + "  <head>\n" + "    <meta charset=\"UTF-8\" />\n" + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" + "    <title>Live Chat|Error</title>\n" + "    <style>\n" + "      body {\n" + "        height: 100vh;\n" + "        display: grid;\n" + "        place-items: center;\n" + "        background-color: #eee;\n" + "      }\n" + "\n" + "      button {\n" + "        background-color: inherit;\n" + "        color: #222;\n" + "        outline: 0;\n" + "        border: 1px solid #cdcdcd;\n" + "        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);\n" + "        padding: 0.5rem 1rem;\n" + "        width: 150px;\n" + "        cursor: pointer;\n" + "        border-radius: 4px;\n" + "        font-size: 1rem;\n" + "      }\n" + "    </style>\n" + "  </head>\n" + "  <body>\n" + "    <div id=\"root\">\n" + "      <h2>Connection error!</h2>\n" + "      <p>Check your internet bundle and try again!</p>\n" + "      <button onclick=\"location.reload();\">Reload</button>\n" + "    </div>\n" + "  </body>\n" + "</html>\n";
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressbar);
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String host = request.getUrl().getHost();

                    if (host == null) return true;

                    if (host.contains("uglivechat.herokuapp.com") || host.contains("ugachat.herokuapp.com")) {
                        return false;
                    }
                }

                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                view.loadDataWithBaseURL(myUrl, errorHTML, "text/html", "utf-8", failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    },

                    PERMISSION_REQUEST_CODE);
        }

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });

        webView.setInitialScale(1);

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setEnableSmoothTransition(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (DetectConnection.checkInternetConnection(this)) {
                webView.reload();
            } else {
                showOfflinePage();
            }

            if (null != swipeRefreshLayout) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (DetectConnection.checkInternetConnection(this)) {
            webView.loadUrl(myUrl);
        } else {
            showOfflinePage();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "onRequestPermissionsResult: " + "Permission granted");
            } else {
                Toast.makeText(this, "All permissions have not been granted. Some features will not work!", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showOfflinePage() {
        webView.loadDataWithBaseURL(myUrl, offlineHTML, "text/html", "utf-8", myUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            progressBar.setVisibility(View.GONE);
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

