package com.akovalenko.obsidian

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ObsidianStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        if (!ScriptInstaller.isInstalled()) {
            showInstallNotification(project)
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
}
