package cn.taosx.btcplugin;

import cn.taosx.btcplugin.ui.PriceToolWindowPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * Created by taosx on 2024-08-19 14:32.
 */

public class PriceToolWindowFactory  implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        PriceToolWindowPanel priceToolWindowPanel = new PriceToolWindowPanel();
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(priceToolWindowPanel.getMainPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
