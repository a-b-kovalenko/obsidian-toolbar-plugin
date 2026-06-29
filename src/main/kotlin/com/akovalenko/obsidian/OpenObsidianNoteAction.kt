package com.akovalenko.obsidian

import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class OpenObsidianNoteAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val vaultRoot = vaultRoot(e) ?: return
        val currentFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val note = findNote(vaultRoot, currentFile.nameWithoutExtension)
        if (note != null)
            openNote(vaultRoot, note)
        else
            openVault(vaultRoot)
    }

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = vaultRoot(e) != null && e.getData(CommonDataKeys.VIRTUAL_FILE) != null
    }

    internal fun findNote(vaultRoot: File, noteName: String): File? =
        vaultRoot.walkTopDown()
            .filter { it.isFile && it.extension == "md" && it.nameWithoutExtension == noteName }
            .firstOrNull()

    internal fun vaultRoot(e: AnActionEvent): File? {
        val projectPath = e.project?.basePath ?: return null
        return vaultRoot(File(projectPath))
    }

    internal fun vaultRoot(dir: File): File? =
        if (File(dir, ".obsidian").isDirectory) dir else null

    private fun openVault(vaultRoot: File) {
        openUri("obsidian://open?vault=${encode(vaultRoot.name)}")
    }

    private fun openNote(vaultRoot: File, note: File) {
        val vault = encode(vaultRoot.name)
        val file = encode(note.relativeTo(vaultRoot).invariantSeparatorsPath)
        openUri("obsidian://open?vault=$vault&file=$file")
    }

    private fun openUri(uri: String) {
        try {
            when {
                SystemInfo.isMac -> ProcessBuilder("open", uri).start()
                SystemInfo.isWindows -> ProcessBuilder("cmd", "/c", "start", "", uri).start()
                else -> ProcessBuilder("xdg-open", uri).start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun encode(s: String): String =
        URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20")
}
