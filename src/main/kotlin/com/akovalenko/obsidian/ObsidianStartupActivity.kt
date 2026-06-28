package com.akovalenko.obsidian

import com.intellij.ide.ui.customization.ActionUrl
import com.intellij.ide.ui.customization.CustomActionsSchema
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.io.File

class ObsidianStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        if (!ScriptInstaller.isInstalled()) {
            showInstallNotification(project)
        }
        ApplicationManager.getApplication().invokeLater {
            if (ensureToolbarButton()) showRestartNotification(project)
        }
    }

    private fun showInstallNotification(project: Project) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Open in Obsidian")
            .createNotification(
                "Obsidian helper script not installed",
                "Install <code>open-obsidian-vault</code> to <code>~/bin/</code> for command-line use.",
                NotificationType.INFORMATION
            )
            .addAction(NotificationAction.createSimpleExpiring("Install to ~/bin") {
                ScriptInstaller.install()
            })
            .notify(project)
    }

    private fun ensureToolbarButton(): Boolean {
        val configFile = File(PathManager.getOptionsPath(), "customization.xml")
        if (configFile.exists() && "OpenObsidianNote" in configFile.readText()) return false
        val schema = CustomActionsSchema.getInstance()
        val path = arrayListOf("root", "Main Toolbar", "Right")
        schema.addAction(ActionUrl(path, "OpenObsidianNote", ActionUrl.ADDED, Int.MAX_VALUE))
        schema.initActionIcons()
        return true
    }

    private fun showRestartNotification(project: Project) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Open in Obsidian")
            .createNotification(
                "Open in Obsidian: restart required",
                "Toolbar button was added. Restart IDE to apply.",
                NotificationType.INFORMATION
            )
            .addAction(NotificationAction.createSimpleExpiring("Restart IDE") {
                ApplicationManager.getApplication().restart()
            })
            .notify(project)
    }
}
