package cn.taosx.btcplugin.http;

import cn.taosx.btcplugin.Configuration.PluginConfiguration;
import cn.taosx.btcplugin.listener.DataListener;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosx on 2024-08-19 15:44.
 * https://developers.binance.com/docs/zh-CN/derivatives/usds-margined-futures/websocket-market-streams/Connect
 */


public class WebSocketClient {
    private static WebSocket webSocket;
    private static OkHttpClient client = new OkHttpClient();
    private static List<DataListener> listeners = new ArrayList<>();

    public static synchronized void start() {
        if (webSocket == null) {
        PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();

//            Request request = new Request.Builder().url("wss://fstream.binance.com/ws/" + pluginConfiguration.defCoins.toLowerCase(Locale.ROOT) + "@kline_1m").build();
            Request request = new Request.Builder().url("wss://fstream.binance.com/ws").build();
            webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
//                    new Thread(() -> {
//                        try {
//                            Thread.sleep(3000);  // 延迟 1 秒
                            // 连接成功后发送数据
//                            JsonObject jsonObject = new JsonObject();
//                            jsonObject.addProperty("method","SUBSCRIBE");
//                            JsonArray jsonArray = new JsonArray();
//                            jsonArray.add("ETHUSDT".toLowerCase(Locale.ROOT) + "@kline_1m");
//                            jsonObject.add("params", jsonArray);
//                            jsonObject.addProperty("id", System.currentTimeMillis());
//                            webSocket.send(jsonObject.toString());
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                        }
//                    }).start();

                }
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    if (text.contains("result")) {
                        return;
                    }
                    notifyListeners(text);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    WebSocketClient.webSocket = null; // Ensure reconnect possible
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                }
            });
        }
    }

    public static synchronized void stop() {
        if (webSocket != null) {
            webSocket.close(1000, "Client shutdown");
            webSocket = null;
        }
    }

    public static synchronized void send(String data) {
        if (webSocket != null) {
            webSocket.send(data);
        }
    }

    public static synchronized void addListener(DataListener listener) {
        listeners.add(listener);
    }

    public static synchronized void removeListener(DataListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners(String data) {
        for (DataListener listener : listeners) {
            listener.onDataReceived(data);
        }
    }
}
