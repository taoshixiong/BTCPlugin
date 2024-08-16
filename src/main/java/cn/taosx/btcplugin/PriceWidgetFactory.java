package cn.taosx.btcplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;

public class PriceWidgetFactory implements StatusBarWidgetFactory {

    @Override
    public String getId() {
        return "PriceWidgetFactory";
    }

    @Override
    public String getDisplayName() {
        return "BTC Price";
    }

    @Override
    public boolean isAvailable(Project project) {
        return true;
    }

    @Override
    public StatusBarWidget createWidget(Project project) {
        return new PriceWidget();
    }

    @Override
    public void disposeWidget(StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(StatusBar statusBar) {
        return true;
    }
}
