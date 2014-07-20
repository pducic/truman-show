package com.pducic.cardboard;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by pducic on 7/20/14.
 */
public class InputActionsService extends IntentService {

    private static final String TAG = InputActionsService.class.getSimpleName();
    private MyHTTPD server;
    private Handler handler = new Handler();

    public InputActionsService() {
        super(TAG);
        try {
            server = new MyHTTPD();
        } catch (IOException e) {
            Log.e(TAG, "Error on init", e);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            server.start();
        } catch (IOException e) {
            Log.e(TAG, "Error on start", e);
        }
        while (true) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null)
            server.stop();
    }

    private class MyHTTPD extends NanoHTTPD {
        public MyHTTPD() throws IOException {
            super(ConfigurationActivity.PORT);
        }

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
            final StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> kv : headers.entrySet())
                buf.append(kv.getKey() + " : " + kv.getValue() + "\n");

            if(method == Method.POST){
                sendBroadcast(new Intent(MainActivity.SHOW_THE_BOX));
                Log.i(TAG, "Received show_the_box request");
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, buf.toString());
                }
            });

            final String html = "<html><head>" +
                    "<script type=\"text/javascript\">" +
                    "function proceed () {\n" +
                    "    var form = document.createElement('form');\n" +
                    "    form.setAttribute('method', 'post');\n" +
                    "    form.setAttribute('action', './show');\n" +
                    "    form.style.display = 'hidden';\n" +
                    "    document.body.appendChild(form)\n" +
                    "    form.submit();\n" +
                    "}</script><head><body><h1>Hello, World</h1>" +
                    "<button type=\"button\" onclick=\"proceed();\">Show The Box!</button> " +
                    "</body></html>";
            return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, html);
        }
    }
}
