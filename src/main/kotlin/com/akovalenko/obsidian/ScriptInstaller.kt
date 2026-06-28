package com.akovalenko.obsidian

import java.io.File

object ScriptInstaller {
    private const val SCRIPT_NAME = "open-obsidian-vault"
    private val binDir = File(System.getProperty("user.home"), "bin")
    val targetFile: File = File(binDir, SCRIPT_NAME)

    fun isInstalled(): Boolean = targetFile.exists()

    fun install() {
        binDir.mkdirs()
        val resource = ScriptInstaller::class.java.getResourceAsStream("/scripts/$SCRIPT_NAME")
            ?: error("Script resource not found in plugin jar")
        resource.use { input ->
            targetFile.outputStream().use { output -> input.copyTo(output) }
        }
        targetFile.setExecutable(true)
    }
}
