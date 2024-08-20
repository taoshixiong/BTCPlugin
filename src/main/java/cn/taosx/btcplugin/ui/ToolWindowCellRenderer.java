package cn.taosx.btcplugin.ui;

import cn.taosx.btcplugin.Configuration.MyIcons;
import cn.taosx.btcplugin.Configuration.PluginConfiguration;
import cn.taosx.btcplugin.entity.ToolWindowDataEntity;

import javax.swing.*;
import java.awt.*;
/**
 * Created by taosx on 2024-08-20 10:37.
 */

public class ToolWindowCellRenderer extends JPanel implements ListCellRenderer<ToolWindowDataEntity> {
    private PluginConfiguration pluginConfiguration = PluginConfiguration.getInstance();

    private JLabel lblText = new JLabel();
    private JLabel lblName = new JLabel();

    public JButton deleteButton = new JButton(MyIcons.del);

    public ToolWindowCellRenderer() {
        setLayout(new BorderLayout(5, 5)); // 设置布局管理器
        add(lblName, BorderLayout.WEST);   // 将图标标签添加到西部（左侧）
        add(lblText, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.EAST);
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends ToolWindowDataEntity> list,
            ToolWindowDataEntity value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        lblName.setText(value.getCryptoName());
        lblText.setText(value.getPrice());
//        deleteButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                list.remove(index);
//                pluginConfiguration.cryptos.remove(index);
//            }
//        });
        return this;
    }
}
