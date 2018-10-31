package create.rwendomain.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import create.rwendomain.R;
import create.rwendomain.TomCatLoadingView.CatLoadingView;
import create.rwendomain.util.Util;

/**
 * Created by Sym on 2016/1/18.
 */
public class WebActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.hidden)
    ImageView hidden;
    @BindView(R.id.web_username)
    TextView web_username;
    @BindView(R.id.web_password)
    TextView web_password;
    @BindView(R.id.linear_hidden)
    LinearLayout linear_hidden;
    @BindView(R.id.btn_copy1)
    Button btn_copy1;
    @BindView(R.id.btn_copy2)
    Button btn_copy2;
    String url="";
    String name="aaaaaaaaa";
    String number="1111111111111111";
    String js="";
    boolean flag=true;
    CatLoadingView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        ButterKnife.bind(this);
        webView = (WebView) findViewById(R.id.webView);
        //webView.loadUrl("http://oa.rwen.com:8282/login.aspx");//加载url
        Intent intent=getIntent();

        name=intent.getStringExtra("username");
        number=intent.getStringExtra("password");
        url=intent.getStringExtra("url");
        if (!intent.hasExtra("username")||!intent.hasExtra("password")){
            linear_hidden.setVisibility(View.GONE);hidden.setVisibility(View.GONE);
        }
        web_username.setText(name);
        web_password.setText(number);
        if (url.equals(Util.getUrl("西部数码"))){
            js = "javascript:document.getElementsByName('u_name')[0].value = '" + name + "';javascript:document.getElementsByName('u_password')[0].value='" + number + "';";
            //js = "javascript:document.getElementById('J_loginPage_u_name').value = '" + name + "';document.getElementById('J_loginPage_u_password').value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("华为云"))){
            js = "javascript:document.getElementById('userNameId').value = '" + name + "';document.getElementById('pwdId').value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("阿里云"))){
            js = "javascript:document.getElementsByName('_fm.l._0.d')[0].value = '" + name + "';javascript:document.getElementsByName('_fm.l._0.p')[0].value='" + number + "';";
        }else if (url.equals(Util.getUrl("中数"))){
            js = "javascript:document.getElementsByName('domain')[0].value = '" + name + "';javascript:document.getElementsByName('password')[0].value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("商务中国"))){
            js = "javascript:document.getElementById('userName').value = '" + name + "';document.getElementById('password').value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("纳网"))){
            js = "javascript:document.getElementById('LoginForm_username').value = '" + name + "';document.getElementById('LoginForm_password').value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("中资源"))){
            js = "javascript:document.getElementsByName('domainname')[0].value = '" + name + "';javascript:document.getElementsByName('domainpass')[0].value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("sun接口"))){
            js = "javascript:document.getElementsByName('domain')[0].value = '" + name + "';javascript:document.getElementsByName('password')[0].value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("时代互联"))){
            js = "javascript:document.getElementsByName('dmLogin')[0].value = '" + name + "';javascript:document.getElementsByName('dmpw')[0].value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("世纪东方"))){
            js = "javascript:document.getElementById('domain').value = '" + name + "';document.getElementById('password').value='" + number + "';";
        }
        else if (url.equals(Util.getUrl("人文网"))){
            js = "javascript:document.getElementById('domain').value = '" + name + "';document.getElementById('inipass').value='" + number + "';";
        }
        mView = new CatLoadingView();
        webView.loadUrl(url);
        setView();


        webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(new WebChromeClient());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()){
                    webView.goBack();
                }
                else {
                    startActivity(new Intent(WebActivity.this,MainActivity.class));
                }}
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebActivity.this,MainActivity.class));
            }
        });
        final ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        btn_copy1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(web_username.getText().toString());
                        Toast.makeText(WebActivity.this, "复制成功", Toast.LENGTH_LONG).show();
                    }
                }
        );
        btn_copy2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 将文本内容放到系统剪贴板里。
                        cm.setText(web_password.getText().toString());
                        Toast.makeText(WebActivity.this, "复制成功", Toast.LENGTH_LONG).show();
                    }
                }
        );

        hidden.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flag){
                            hidden.setImageResource(R.drawable.up);
                            flag=!flag;
                            linear_hidden.setVisibility(View.GONE);
                        }
                        else {
                            hidden.setImageResource(R.drawable.down);
                            linear_hidden.setVisibility(View.VISIBLE);
                            flag=!flag;
                        }
                    }
                }
        );
    }



    public void setView() {
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString("android_csf");//设置代理
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.setWebChromeClient(new WebChromeClient());//这行最好不要丢掉

        webView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            Log.e("开启硬件加速","开启硬件加速");
        }

        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("错误", "onReceivedError" + view.getUrl());

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.e("现在加载的页面", "url" + url);




                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!mView.isAdded()){
                    mView.show(getFragmentManager(), "");
                }


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mView.isAdded()){
                    mView.dismiss();
                }

                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                } else {
                    view.loadUrl(js);
                }

            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (webView.canGoBack()){
                webView.goBack();
                return true;
            }
            else {
                startActivity(new Intent(WebActivity.this,MainActivity.class));
                return true;
            }
        }

        return super.onKeyDown(keyCode,event);
    }

    /**
     * JS调用android的方法
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void  getClient(String str){
        Log.i("ansen","html调用客户端:"+str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        webView.destroy();
        webView=null;
    }

}

