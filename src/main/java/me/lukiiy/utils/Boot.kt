package me.lukiiy.utils

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import me.lukiiy.utils.cmd.*

class Boot : PluginBootstrap {
    override fun bootstrap(ctx: BootstrapContext) {
        ctx.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
            it.registrar().apply {
                register(Broadcast.register(), "Broadcasts a message", listOf("bcast", "bc"))
                register(Fly.register(), "Toggles flight state", listOf("flight"))
                register(Invulnerability.register(), "Toggles invulnerability state", listOf("god", "invul"))
                register(SimpleStats.registerHeal(), "Heals a player")
                register(SimpleStats.registerFeed(), "uh- feeds... a player?")
                register(SimpleStats.registerBare(), "Sets selected players' hp and foodlevel to 1")
                register(DisplayToast.register(), "Displays a custom advancement toast to a player")
                register(InventoryViewer.registerInv(), "View a player's inventory", listOf("openinv", "viewinv"))
                register(InventoryViewer.registerEChest(), "View a player's ender chest", listOf("openechest", "viewechest"))
                register(InventoryViewer.registerEquip(), "View a player's armor & offhand", listOf("viewequip"))
                register(Ping.register(), "Displays a player's ping")
                register(SlimeChunk.register(), "Tells if a player is in a slime chunk")
                register(Vanish.registerMain(), "Toggles invisibility state", listOf("v"))
                register(Vanish.registerList(), "Lists every player using /vanish", listOf("vlist"))
                register(MassAffect.register(), "Applies various effects to selected players")
                register(Collapse.register(), "Makes an area collapse!")
                register(Ignite.register(), "Sets selected players on fire", listOf("burn"))
                register(PlayerData.registerOnline(), "Shows player data from online users", listOf("playerinfo"))
                register(PlayerData.registerOffline(), "Shows player data from offline users", listOf("offplayerinfo"))
            }
        }
    }
}
