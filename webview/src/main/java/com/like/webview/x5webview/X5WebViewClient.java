package com.like.webview.x5webview;

import android.graphics.Bitmap;

import com.like.rxbus.RxBus;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 帮助WebView处理各种通知和请求事件的
 */
public class X5WebViewClient extends WebViewClient {

    public X5WebViewClient() {
    }

    //页面开始加载时
    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        super.onPageStarted(webView, url, favicon);
        // 该方法在WebView开始加载页面且仅在Main frame loading（即整页加载）时回调，一次Main frame的加载只会回调该方法一次。
        // 我们可以在这个方法里设定开启一个加载的动画，告诉用户程序在等待网络的响应。CustomWebChromeClient里面的onProgressChanged()方法来取代。
        RxBus.post(X5ProgressBarWebView.TAG_WEBVIEW_PAGE_STARTED, url);
    }

    //页面完成加载时
    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        // 该方法只在WebView完成一个页面加载时调用一次（同样也只在Main frame loading时调用），
        // 我们可以可以在此时关闭加载动画，进行其他操作。
        // 注意：由于浏览器内核有可能导致该结束的时候不结束，不该结束的时候提前结束。可以用
        RxBus.post(X5ProgressBarWebView.TAG_WEBVIEW_PAGE_FINISHED, url);
    }

    //网络错误时回调的方法
    @Override
    public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError);
        // 该方法在web页面加载错误时回调，这些错误通常都是由无法与服务器正常连接引起的，最常见的就是网络问题。
        // 在这里写网络错误时的逻辑,比如显示一个错误页面
        // 这个方法有两个地方需要注意：
        // 1.这个方法只在与服务器无法正常连接时调用，类似于服务器返回错误码的那种错误（即HTTP ERROR），该方法是不会回调的，
        // 因为你已经和服务器正常连接上了（全怪官方文档(︶^︶)）；
        // 2.这个方法是新版本的onReceivedError()方法，从API23开始引进，
        // 与旧方法onReceivedError(WebView view,int errorCode,String description,String failingUrl)不同的是，
        // 新方法在页面局部加载发生错误时也会被调用（比如页面里两个子Tab或者一张图片）。
        // 这就意味着该方法的调用频率可能会更加频繁，所以我们应该在该方法里执行尽量少的操作。
        RxBus.postByTag(X5ProgressBarWebView.TAG_WEBVIEW_ON_RECEIVED_ERROR);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
        // 是否在WebView内加载新页面
        // 当我们没有给WebView提供WebViewClient时，WebView如果要加载一个url会向ActivityManager寻求一个适合的处理者来加载该url（比如系统自带的浏览器），
        // 这通常是我们不想看到的。于是我们需要给WebView提供一个WebViewClient，并重写该方法返回true来告知WebView url的加载就在app中进行。
        // 这时便可以实现在app内访问网页。
        webView.loadUrl(s);
        return true;
    }

}
