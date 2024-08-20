package cn.taosx.btcplugin.Configuration;

import cn.taosx.btcplugin.entity.ToolWindowDataEntity;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosx on 2024-08-19 16:34.
 */


@State(
        name = "cn.taosx.btcplugin.Configuration.PluginConfiguration",
        storages = {@Storage("PluginSettings.xml")}
)
public class PluginConfiguration implements PersistentStateComponent<PluginConfiguration> {
    public List<ToolWindowDataEntity> cryptos = new ArrayList<ToolWindowDataEntity>();
    public String statusBarCoin = "";
    @Override
    public void initializeComponent() {
        if (cryptos.size() <= 0) {
            ToolWindowDataEntity data = new ToolWindowDataEntity();
            data.setCryptoName("BTCUSDT");
            data.setPrice("--");
            cryptos.add(data);
        }
        if (statusBarCoin.isEmpty()) {
            statusBarCoin="BTCUSDT";
        }
        PersistentStateComponent.super.initializeComponent();
    }

    @Nullable
    @Override
    public PluginConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static PluginConfiguration getInstance() {
        return ServiceManager.getService(PluginConfiguration.class);
    }
}
