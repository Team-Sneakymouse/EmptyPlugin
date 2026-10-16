package com.emptyplugin

import com.emptyplugin.commands.ReloadCommand
import org.bukkit.plugin.java.JavaPlugin

open class EmptyPlugin : JavaPlugin() {

    companion object {
        const val IDENTIFIER = "emptyplugin"
        lateinit var instance: EmptyPlugin

        fun log(message: String) {
            instance.logger.info(message)
        }
    }

    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        saveDefaultConfig()
        reloadPlugin()
        registerCommand(IDENTIFIER, "Reload the plugin configuration", ReloadCommand(this))
        logger.info("EmptyPlugin enabled.")
    }

    /** Shared startup/reload path. Keep config-derived runtime state refreshed here. */
    fun reloadPlugin() {
        reloadConfig()
    }

    override fun onDisable() {
        logger.info("EmptyPlugin disabled.")
    }
}
