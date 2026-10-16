package com.emptyplugin.commands

import com.emptyplugin.EmptyPlugin
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class ReloadCommandTest {

    private lateinit var server: ServerMock

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
    }

    @AfterEach
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun playerWithoutPermissionCannotReload() {
        val plugin = MockBukkit.load(EmptyPlugin::class.java)
        plugin.config.set("test-value", "before")
        File(plugin.dataFolder, "config.yml").writeText("test-value: after\n")
        val player = server.addPlayer()

        server.dispatchCommand(player, "emptyplugin reload")

        assertEquals("before", plugin.config.getString("test-value"))
    }

    @Test
    fun playerWithPermissionCanReloadRepeatedly() {
        val plugin = MockBukkit.load(EmptyPlugin::class.java)
        val player = server.addPlayer()
        player.addAttachment(plugin, "emptyplugin.command.reload", true)

        for (value in listOf("first", "second")) {
            File(plugin.dataFolder, "config.yml").writeText("test-value: $value\n")
            assertTrue(server.dispatchCommand(player, "emptyplugin reload"))
            assertEquals(value, plugin.config.getString("test-value"))
            assertEquals("EmptyPlugin configuration reloaded.", player.nextMessage())
        }
    }

    @Test
    fun invalidArgumentsShowUsageWithoutReloading() {
        val plugin = MockBukkit.load(EmptyPlugin::class.java)
        plugin.config.set("test-value", "before")
        File(plugin.dataFolder, "config.yml").writeText("test-value: after\n")

        for (command in listOf("emptyplugin", "emptyplugin unknown", "emptyplugin reload extra")) {
            assertTrue(server.dispatchCommand(server.consoleSender, command))
            assertEquals("Usage: /emptyplugin reload", server.consoleSender.nextMessage())
            assertEquals("before", plugin.config.getString("test-value"))
        }
    }

    @Test
    fun consoleReloadReparsesConfigFromDisk() {
        val plugin = MockBukkit.load(EmptyPlugin::class.java)
        plugin.config.set("test-value", "before")
        File(plugin.dataFolder, "config.yml").writeText("test-value: after\n")

        assertTrue(server.dispatchCommand(server.consoleSender, "emptyplugin reload"))

        assertEquals("after", plugin.config.getString("test-value"))
        assertEquals("EmptyPlugin configuration reloaded.", server.consoleSender.nextMessage())
        assertTrue(plugin.isEnabled)
    }
}
