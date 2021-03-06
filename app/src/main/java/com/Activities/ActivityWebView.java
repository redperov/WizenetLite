package com.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.Classes.Call;
import com.DatabaseHelper;
import com.Helper;
import com.Adapters.CallsAdapter;
import com.model.Model;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityWebView extends FragmentActivity {

    Helper helper ;
    Context ctx;

    DatabaseHelper db;
    ListView myList;
    LocationManager manager = null;
    boolean result = false;
    private EditText mSearchEdt;
    CallsAdapter callsAdapter; //to refresh the list
    ArrayList<Call> data2 = new ArrayList<Call>() ;
    private TextWatcher mSearchTw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_webview);
        ctx = this;

        int callid = -1;
        int cid = -1;
        int technicianid = -1;
        String action = "";

        Bundle b = getIntent().getExtras();
        if(b != null){
            callid = b.getInt("callid");
            cid = b.getInt("cid");
            technicianid = b.getInt("technicianid");
            action = b.getString("action");
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.top_bar_back, null); // layout which contains your button.

        actionBar.setCustomView(customNav, lp1);
        Button iv = (Button) customNav.findViewById(R.id.back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //Toast.makeText(getApplicationContext(),"clicked", Toast.LENGTH_LONG).show();
            }
        });





        final WebView  mWebview  = new WebView(this);
        final Activity activity = this;
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
        mWebview.setWebChromeClient(new WebChromeClient());


        mWebview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        String url = "";
        switch(action) {
            case "calltime":
                url = DatabaseHelper.getInstance(getApplicationContext()).getValueByKey("URL")
                        + "/iframe.aspx?control=/modulesServices/CallRepHistory&CallID=" + String.valueOf(callid) + "&class=tdCallRepHistory&mobile=True";
                break;
            case "callparts":
                url = DatabaseHelper.getInstance(getApplicationContext()).getValueByKey("URL")
                    + "/iframe.aspx?control=modulesServices%2fCallParts&CallID=" + String.valueOf(callid) + "&type=customer&val=" + String.valueOf(cid) + "";
                break;
            default:
                //setContentView(R.layout.default);
        }

        String cookieString = "CID=" + String.valueOf(technicianid) + "; path=/";
        CookieManager.getInstance().setCookie(url, cookieString);
        mWebview .loadUrl(url);//"http://www.google.com");
        setContentView(mWebview );

//        WebView  mWebview  = new WebView(this);
//
//        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
//
//        final Activity activity = this;
//
//        mWebview.setWebViewClient(new WebViewClient() {
//            @SuppressWarnings("deprecation")
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
//            }
//            @TargetApi(android.os.Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
//                // Redirect to deprecated method, so you can use it in all SDK versions
//                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
//            }
//        });
//
//        mWebview .loadUrl("http://www.google.com");
//        setContentView(mWebview );



    }
    public void initList(){
        data2.clear();
        for (Call c : getCallsList()){
            data2.add(c);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Toast.makeText(getBaseContext(),"onRestart", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Toast.makeText(getBaseContext(),"onResume", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.add:
//                //add the function to perform here
//                return (true);
            //case R.id.action_filter:
            //add the function to perform here
            // return (true);
//            case R.id.about:
//                //add the function to perform here
//                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
    private List<Call> getCallsList(){
        JSONObject j = null;
        int length = 0;

        List<Call> calls = new ArrayList<Call>() ;
        try {
            calls= DatabaseHelper.getInstance(this).getCalls();
            length = calls.size();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return calls;
    }

}
