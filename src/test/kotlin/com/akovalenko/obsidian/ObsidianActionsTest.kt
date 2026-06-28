package com.akovalenko.obsidian

import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObsidianActionsTest {

    private lateinit var tempDir: File

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory("obsidian-test").toFile()
    }

    @AfterTest
    fun teardown() {
        tempDir.deleteRecursively()
    }

    private val action = OpenObsidianNoteAction()

    // vaultRoot

    @Test
    fun `vaultRoot returns null when no obsidian dir`() {
        assertNull(action.vaultRoot(tempDir))
    }

    @Test
    fun `vaultRoot returns project dir when obsidian dir exists`() {
        File(tempDir, ".obsidian").mkdir()
        assertEquals(tempDir, action.vaultRoot(tempDir))
    }

    @Test
    fun `vaultRoot returns null when obsidian is a file not a dir`() {
        File(tempDir, ".obsidian").createNewFile()
        assertNull(action.vaultRoot(tempDir))
    }

    // findNote

    @Test
    fun `findNote returns null when no matching note`() {
        File(tempDir, ".obsidian").mkdir()
        File(tempDir, "Other.md").createNewFile()
        assertNull(action.findNote(tempDir, "UserService"))
    }

    @Test
    fun `findNote returns matching note in vault root`() {
        File(tempDir, ".obsidian").mkdir()
        val note = File(tempDir, "UserService.md").also { it.createNewFile() }
        assertEquals(note, action.findNote(tempDir, "UserService"))
    }

    @Test
    fun `findNote returns matching note in subdirectory`() {
        File(tempDir, ".obsidian").mkdir()
        val subdir = File(tempDir, "backend").also { it.mkdir() }
        val note = File(subdir, "UserService.md").also { it.createNewFile() }
        assertEquals(note, action.findNote(tempDir, "UserService"))
    }

    @Test
    fun `findNote ignores files with non-md extension`() {
        File(tempDir, ".obsidian").mkdir()
        File(tempDir, "UserService.txt").createNewFile()
        assertNull(action.findNote(tempDir, "UserService"))
    }

    @Test
    fun `findNote match is case-sensitive`() {
        File(tempDir, ".obsidian").mkdir()
        File(tempDir, "userservice.md").createNewFile()
        assertNull(action.findNote(tempDir, "UserService"))
    }
}
