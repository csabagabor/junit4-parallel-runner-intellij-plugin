package gabor.paralleltester.helper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

public class UIHelper {
    public static void showErrorMessage(String msg, Project project) {
        StatusBar statusBar = WindowManager.getInstance()
                .getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(msg, MessageType.ERROR, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }
}
