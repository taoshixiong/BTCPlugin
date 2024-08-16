package cn.taosx.btcplugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PriceWidget extends JLabel implements CustomStatusBarWidget {

    private static final String WS_URL = "wss://fstream.binance.com/ws/btcusdt@kline_1m";
//    private static final String WS_URL = "wss://ws-fapi.binance.com/ws-fapi/v1";
    private static final String SYMBOL = "BTCUSDT";

    //    private Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));

    private OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
//            .proxy(proxy)
            .build();
    private Request mRequest = new Request.Builder().url(WS_URL).build();
    private WebSocket mWebSocket;

    @Override
    public String ID() {
        return "PriceWidget";
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void install(StatusBar statusBar) {
        setText(SYMBOL + " --");
        connect();
    }

    private void connect() {
        setText("loading...");
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, new WidgetWebSocketListener());
    }

    private class WidgetWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            mWebSocket = webSocket;
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            /* {
                "e": "kline",          // 事件类型
                "E": 1672515782136,    // 事件时间
                "s": "BNBBTC",         // 交易对
                "k": {
                "t": 1672515780000,  // 这根K线的起始时间
                "T": 1672515839999,  // 这根K线的结束时间
                "s": "BNBBTC",       // 交易对
                "i": "1m",           // K线间隔
                "f": 100,            // 这根K线期间第一笔成交ID
                "L": 200,            // 这根K线期间末一笔成交ID
                "o": "0.0010",       // 这根K线期间第一笔成交价
                "c": "0.0020",       // 这根K线期间末一笔成交价
                "h": "0.0025",       // 这根K线期间最高成交价
                "l": "0.0015",       // 这根K线期间最低成交价
                "v": "1000",         // 这根K线期间成交量
                "n": 100,            // 这根K线期间成交数量
                "x": false,          // 这根K线是否完结（是否已经开始下一根K线）
                "q": "1.0000",       // 这根K线期间成交额
                "V": "500",          // 主动买入的成交量
                "Q": "0.500",        // 主动买入的成交额
                "B": "123456"        // 忽略此参数
            }
            }*/
            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
            if(jsonObject!=null&&jsonObject.get("k")!=null
                    &&!jsonObject.get("k").isJsonNull()&&jsonObject.get("k").getAsJsonObject().get("c")!=null) {
                setText(SYMBOL + " " + jsonObject.get("k").getAsJsonObject().get("c").getAsString());
            }
        }



        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignore) {
            }
            connect();
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            connect();
        }
    }

    @Override
    public void dispose() {
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
    }


}
