package com.emptyplugin

import org.bukkit.plugin.java.JavaPlugin

class EmptyPlugin : JavaPlugin() {

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
        logger.info("EmptyPlugin enabled.")
        saveDefaultConfig()
    }

    override fun onDisable() {
        logger.info("EmptyPlugin disabled.")
    }
}
