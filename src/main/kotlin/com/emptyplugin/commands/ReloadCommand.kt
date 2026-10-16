package com.emptyplugin.commands

import com.emptyplugin.EmptyPlugin
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack

/** Plugin-scoped config reload; never reloads the server or plugin classloader. */
open class ReloadCommand(private val plugin: EmptyPlugin) : BasicCommand {

    override fun permission(): String = "${EmptyPlugin.IDENTIFIER}.command.reload"

    override fun execute(commandSource: CommandSourceStack, args: Array<out String>) {
        if (args.size != 1 || !args[0].equals("reload", ignoreCase = true)) {
            commandSource.sender.sendMessage("Usage: /${EmptyPlugin.IDENTIFIER} reload")
            return
        }

        plugin.reloadPlugin()
        commandSource.sender.sendMessage("${plugin.name} configuration reloaded.")
    }
}
