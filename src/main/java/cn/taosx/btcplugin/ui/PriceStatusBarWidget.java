package cn.taosx.btcplugin.ui;

import cn.taosx.btcplugin.Configuration.PluginConfiguration;
import cn.taosx.btcplugin.entity.ToolWindowDataEntity;
import cn.taosx.btcplugin.http.WebSocketClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class PriceStatusBarWidget extends JLabel implements CustomStatusBarWidget {
    PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();
    private Color backgroundColor;
    @Override
    public String ID() {
        return "PriceWidget";
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    public PriceStatusBarWidget() {
        WebSocketClient.addListener(this::updateUI);
        List<ToolWindowDataEntity> cryptos = pluginConfiguration.cryptos;
        for (ToolWindowDataEntity crypto : cryptos) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method","SUBSCRIBE");
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(crypto.getCryptoName().toLowerCase(Locale.ROOT) + "@kline_1m");
            jsonObject.add("params", jsonArray);
            jsonObject.addProperty("id", System.currentTimeMillis());

            WebSocketClient.send(jsonObject.toString());
            try {
                //Websocket服务器每秒最多接受10个订阅消息。
                //如果用户发送的消息超过限制，连接会被断开连接。反复被断开连接的IP有可能被服务器屏蔽。
                Thread.sleep(500);  // 延迟
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        setOpaque(false); // Make the JLabel non-opaque
    }


    private void updateUI(String text) {
        // This method will be called on the EDT
        // Update your UI component here

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

        if(jsonObject!=null) {
            String SYMBOL=jsonObject.get("s").getAsString().toUpperCase(Locale.ROOT);

            if(SYMBOL.equals(pluginConfiguration.statusBarCoin)&&jsonObject.get("k")!=null
                    &&!jsonObject.get("k").isJsonNull()&&jsonObject.get("k").getAsJsonObject().get("c")!=null){
                SwingUtilities.invokeLater(() -> {
                    //趋势涨跌颜色
//                    if (jsonObject.get("k").getAsJsonObject().get("c").getAsString()
//                            .equals(jsonObject.get("k").getAsJsonObject().get("o").getAsString())) {
//                        setBackgroundColor(new Color(0, 0, 0, 0));
//                    }else if(jsonObject.get("k").getAsJsonObject().get("c").getAsDouble()
//                            > jsonObject.get("k").getAsJsonObject().get("o").getAsDouble()){
//                        setBackgroundColor(new Color(64, 255, 0, 32));
//                    }else{
//                        setBackgroundColor(new Color(255, 0, 64, 32));
//                    }

                    setText(SYMBOL + " " + jsonObject.get("k").getAsJsonObject().get("c").getAsString());
                });
            }

        }
    }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void paintComponent(Graphics g) {

        if (backgroundColor != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, getWidth(), getHeight()); // Fill the background with the semi-transparent color
            g2d.dispose();
        }
        super.paintComponent(g);
    }
    public void onClose() {
        WebSocketClient.removeListener(this::updateUI);
    }

    @Override
    public void install(StatusBar statusBar) {
        setText("--");
    }



}
