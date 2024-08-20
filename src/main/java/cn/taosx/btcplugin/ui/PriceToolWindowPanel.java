package cn.taosx.btcplugin.ui;

import cn.taosx.btcplugin.Configuration.PluginConfiguration;
import cn.taosx.btcplugin.entity.ToolWindowDataEntity;
import cn.taosx.btcplugin.http.WebSocketClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

/**
 * Created by taosx on 2024-08-19 14:36.
 */

public class PriceToolWindowPanel {
   private PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();

   private JPanel panel;
   private DefaultListModel<ToolWindowDataEntity> listModel = new DefaultListModel<>();
   private JList<ToolWindowDataEntity> list;

   public PriceToolWindowPanel() {

      panel = new JPanel(new BorderLayout());
      panel.setBorder(new EmptyBorder(10, 10, 10, 10));

      for (ToolWindowDataEntity crypto : pluginConfiguration.cryptos) {
         listModel.addElement(crypto);
      }
      list = new JList<>(listModel);
      list.setBorder(null); // 移除边框
      list.setCellRenderer(new ToolWindowCellRenderer());



      JTextField textField = new JTextField();
      textField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent e) {
            super.keyTyped(e);
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
               if(textField.getText().isEmpty()){
                  return;
               }
               ToolWindowDataEntity item = new ToolWindowDataEntity();
               item.setCryptoName(textField.getText().toUpperCase(Locale.ROOT));
               item.setPrice("--");
               listModel.add(0, item);
               pluginConfiguration.cryptos.add(0, item);


               JsonObject jsonObject = new JsonObject();
               jsonObject.addProperty("method","SUBSCRIBE");
               JsonArray jsonArray = new JsonArray();
               jsonArray.add(textField.getText().toLowerCase(Locale.ROOT) + "@kline_1m");
               jsonObject.add("params", jsonArray);
               jsonObject.addProperty("id", System.currentTimeMillis());

               WebSocketClient.send(jsonObject.toString());
               textField.setText("");
            }
         }
      });

      list.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            int index = list.locationToIndex(e.getPoint());
            if (index != -1) {
               Rectangle rect = list.getCellBounds(index, index);
               ToolWindowCellRenderer renderer = (ToolWindowCellRenderer) list.getCellRenderer().getListCellRendererComponent(list, list.getModel().getElementAt(index), index, false, false);
               renderer.setBounds(rect);
               renderer.doLayout();
               Point point = e.getPoint();
               point.translate(-rect.x, -rect.y);
               Component c = SwingUtilities.getDeepestComponentAt(renderer, point.x, point.y);
               if (c == renderer.deleteButton) {
                  DefaultListModel<ToolWindowDataEntity> model = (DefaultListModel<ToolWindowDataEntity>) list.getModel();
                  pluginConfiguration.cryptos.remove(index);
                  ((DefaultListModel<ToolWindowDataEntity>) list.getModel()).remove(index);


                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("method","UNSUBSCRIBE");
                  JsonArray jsonArray = new JsonArray();
                  jsonArray.add(model.get(index).getCryptoName().toLowerCase(Locale.ROOT) + "@kline_1m");
                  jsonObject.add("params", jsonArray);
                  jsonObject.addProperty("id", System.currentTimeMillis());

                  WebSocketClient.send(jsonObject.toString());

               }
            }
         }
      });
      JScrollPane jScrollPane = new JScrollPane(list);
      jScrollPane.setBorder(null);

      panel.add(jScrollPane, BorderLayout.CENTER);
      panel.add(textField, BorderLayout.NORTH);
//      panel.add(addButton, BorderLayout.SOUTH);
//      btn.addActionListener(new ActionListener() {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//            mlable.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//         }
//      });
   }


   private void updateUI(String text) {
      JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
      if(jsonObject!=null) {
         String SYMBOL=jsonObject.get("s").getAsString().toUpperCase(Locale.ROOT);

         if(jsonObject.get("k")!=null
                 &&!jsonObject.get("k").isJsonNull()&&jsonObject.get("k").getAsJsonObject().get("c")!=null){
            SwingUtilities.invokeLater(() -> {
//               mlable.setText(SYMBOL + " " + jsonObject.get("k").getAsJsonObject().get("c").getAsString());

               for (int i = 0; i < listModel.size(); i++) {
                  ToolWindowDataEntity entity = listModel.get(i);
                  if (entity.getCryptoName().equals(SYMBOL)) {
                     entity.setPrice(jsonObject.get("k").getAsJsonObject().get("c").getAsString());
                     listModel.set(i, entity); // 必要的，以触发JList的更新
                     break; // 假设只有一个匹配的项，如果有多个匹配项需要更新，可以去掉这个 break
                  }
               }
            });
         }

      }
   }

   public void onClose() {
      WebSocketClient.removeListener(this::updateUI);
   }


   public JPanel getMainPanel() {
      WebSocketClient.addListener(this::updateUI);

      return panel;
   }

}
