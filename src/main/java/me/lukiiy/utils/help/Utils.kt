package me.lukiiy.utils.help

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.mojang.brigadier.context.CommandContext
import com.viaversion.viaversion.api.Via
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.BrandPayload
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.UUID

object Utils : Listener {
    val USERNAME_REGEX = Regex("^[A-Za-z0-9_]{1,16}$")

    internal val originalSkins = mutableMapOf<UUID, ProfileProperty?>()
    internal val originalNametags = mutableMapOf<UUID, String>()

    // Component Help!
    fun adminCmdFeedback(sender: CommandSender, message: String) {
        if (!Lukitils.getInstance().config.getBoolean("commandFeedback")) return

        val senderName = if (sender is ConsoleCommandSender) "Server".asFancyString() else sender.name()
        val msg: Component = Component.translatable("chat.type.admin").arguments(senderName, message.asFancyString()).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)

        Bukkit.getOnlinePlayers().stream().filter { it.hasPermission("minecraft.admin.command_feedback") && it != sender }.forEach { it.sendMessage(msg) }
        if (sender !is ConsoleCommandSender) Lukitils.getInstance().componentLogger.trace(msg)
    }

    // String & Components Extensions!
    @JvmStatic
    fun String.asPermission(): String = Defaults.getPermission(this)

    @JvmStatic
    fun String.asFancyString(): Component = Defaults.FancyString.deserialize(this)

    @JvmStatic
    fun Component.asLegacyString(): String = LegacyComponentSerializer.legacySection().serialize(this)

    @JvmStatic
    fun Component.asPlainString(): String = PlainTextComponentSerializer.plainText().serialize(this)

    @JvmStatic
    fun Location.toComponent(): Component {
        val coordString = "${this.blockX} ${this.blockY} ${this.blockZ}"

        return coordString.asFancyString().hoverEvent(HoverEvent.showText(Component.text("Click to Copy!").color(Defaults.YELLOW))).clickEvent(ClickEvent.copyToClipboard("/tp @s $coordString")).append(" @ ${this.world.name}".asFancyString())
    }

    @JvmStatic
    fun Location.copyableComponent(): Component { // Probably needs refactoring
        val dim = when (this.world?.environment) {
            World.Environment.NORMAL -> "overworld"
            World.Environment.NETHER -> "the_nether"
            World.Environment.THE_END -> "the_end"
            else -> ""
        }

        val coords = "${this.blockX} ${this.blockY} ${this.blockZ}"
        return coords.asFancyString().hoverEvent(HoverEvent.showText(Component.text("Click to copy command!").color(Defaults.YELLOW))).clickEvent(ClickEvent.copyToClipboard("/execute ${if (dim.isNotEmpty()) "in minecraft:$dim" else ""} run tp @s $coords")).append(" @ ${this.world.name}".asFancyString())
    }

    @JvmStatic
    fun String.stripArgument(argument: String): Pair<String, Boolean> {
        val regex = Regex("(?<=^|\\s)-$argument(?=\\s|$)")

        val removed = regex.containsMatchIn(this)
        val modified = if (removed) regex.replace(this, "").replace(Regex("\\s+"), " ").trim() else this

        return modified to removed
    }

    // Doubles Extensions!
    @JvmStatic
    @JvmOverloads
    fun Double.boundedScale(max: Double = 10.0): Double = (this - 1.0).coerceIn(-0.9, max)

    @JvmStatic
    fun Double.fancy(): String {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val format = DecimalFormat("#,##0.##", symbols)

        format.isGroupingUsed = true
        return format.format(this)
    }

    // Player Extensions!
    fun Player.getServerPlayer(): ServerPlayer = (this as CraftPlayer).handle

    fun brand(p: Player, brand: Component?) {
        if (brand == null || brand == Component.empty()) return
        val fBrand = LegacyComponentSerializer.legacySection().serialize(brand)

        try {
            p.getServerPlayer().connection.send(ClientboundCustomPayloadPacket(BrandPayload(fBrand)))
        } catch (_: Exception) {}
    }

    @JvmStatic
    fun Player.getSpawn(): Location = this.respawnLocation ?: Bukkit.getWorlds()[0].spawnLocation

    @JvmStatic
    @JvmOverloads
    fun Player.mark(style: Style = Style.empty(), entityHover: Boolean = true, head: Boolean = false): Component {
        var end = Component.empty()
        var body = displayName().style(style)

        if (entityHover) body = body.hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.showEntity(Key.key(Key.MINECRAFT_NAMESPACE, "player"), this.uniqueId, this.name())))
        if (head) end = end.append(Component.`object`(ObjectContents.playerHead(this.playerProfile)).appendSpace())
        end = end.append(body)

        return end
    }

    @JvmStatic
    @JvmOverloads
    fun Collection<Player>.mark(style: Style = Style.empty(), heads: Boolean = false): Component {
        if (this.size == 1) return this.first().mark(style)

        return Component.text("${this.size} players").style(style).hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), this.stream().map { it.mark(style, false, heads) }.toList())))
    }

    @JvmStatic
    fun Player.getProtocol(): Int = try {
        Via.getAPI().getPlayerVersion(this)
    } catch (_: Exception) {
        player!!.protocolVersion // wow.
    }

    @JvmStatic
    fun Player.addScalarTransientMod(id: String, attribute: Attribute, value: Double) {
        val key = NamespacedKey(Lukitils.getInstance(), id)
        val instance = this.getAttribute(attribute) ?: return

        instance.removeModifier(key)
        if (value != 0.0) instance.addTransientModifier(AttributeModifier(key, value, AttributeModifier.Operation.MULTIPLY_SCALAR_1))
    }

    @JvmStatic
    fun Player.removeTransientMod(id: String, attribute: Attribute) = this.getAttribute(attribute)?.removeModifier(NamespacedKey(Lukitils.getInstance(), id))

    /**
     * Changes a player's nametag.
     * @param newUsername A new username, acceptable by the [USERNAME_REGEX] regex
     * @param viewers You can specify which players will see the new nametag. Nullable.
     */
    @JvmStatic
    @JvmOverloads
    fun Player.setNametag(newUsername: String, viewers: Collection<Player>? = null): Boolean {
        if (uniqueId !in originalNametags) originalNametags[uniqueId] = playerProfile.name ?: name

        return newUsername.trim().takeIf { USERNAME_REGEX.matches(it) }?.let { safeProfile(uniqueId, it) }?.also { applyProfile(it, viewers) } != null
    }

    /**
     * Changes a player's skin (and cape) to another player's.
     * @param username A valid username to copy the skin from.
     * @param viewers You can specify which players will see the new nametag. Nullable (global).
     */
    @JvmStatic
    @JvmOverloads
    fun Player.setTextures(username: String, viewers: Collection<Player>? = null): Boolean {
        Bukkit.getAsyncScheduler().runNow(Lukitils.getInstance()) {
            val prop = runCatching {
                Bukkit.createProfile(username).apply { complete(true) }.properties.firstOrNull { it.name.equals("textures", true) }
            }.getOrNull()

            Bukkit.getGlobalRegionScheduler().run(Lukitils.getInstance()) {
                prop?.let { applyTextureAndRefresh(it, viewers) }
            }
        }

        return true
    }

    /**
     * Changes a player's skin (and cape) to another player's.
     * @param skin A valid UUID of a player to copy the skin from.
     * @param viewers You can specify which players will see the new nametag. Nullable (global).
     */
    @JvmStatic
    @JvmOverloads
    fun Player.setTextures(skin: UUID, viewers: Collection<Player>? = null): Boolean {
        Bukkit.getAsyncScheduler().runNow(Lukitils.getInstance()) {
            val prop = runCatching {
                Bukkit.createProfile(skin, null).apply { complete(true) }.properties.firstOrNull { it.name.equals("textures", true) }
            }.getOrNull()

            Bukkit.getGlobalRegionScheduler().run(Lukitils.getInstance()) {
                prop?.let { applyTextureAndRefresh(it, viewers) }
            }
        }

        return true
    }

    /**
     * Changes a player's skin (& cape) to a custom one.
     * @param skin The texture data.
     * @param signature The texture signature.
     * @param viewers You can specify which players will see the new nametag. Nullable (global).
     */
    @JvmStatic
    @JvmOverloads
    fun Player.setTextures(skin: String, signature: String, viewers: Collection<Player>? = null): Boolean {
        applyTextureAndRefresh(ProfileProperty("textures", skin, signature), viewers)
        return true
    }

    @JvmStatic
    @JvmOverloads
    fun Player.resetNametag(viewers: Collection<Player>? = null): Boolean {
        applyProfile(safeProfile(uniqueId, originalNametags.remove(uniqueId) ?: name), viewers)
        return true
    }

    @JvmStatic
    @JvmOverloads
    fun Player.resetTextures(viewers: Collection<Player>? = null): Boolean {
        if (originalSkins.containsKey(uniqueId)) {
            val saved = originalSkins.remove(uniqueId)
            val profile = safeProfile(uniqueId, playerProfile.name ?: name).apply {
                val base = properties.filterNot { it.name.equals("textures", true) }

                setProperties(if (saved != null) base + saved else base)
            }

            applyProfile(profile, viewers)
            return true
        }

        Bukkit.getAsyncScheduler().runNow(Lukitils.getInstance()) {
            val prop = runCatching {
                Bukkit.createProfile(uniqueId, null).apply { complete(true) }.properties.firstOrNull { it.name.equals("textures", true) }
            }.getOrNull()
            Bukkit.getGlobalRegionScheduler().run(Lukitils.getInstance()) {
                prop?.let { applyTextureAndRefresh(it, viewers) }
            }
        }

        return true
    }

    internal fun safeProfile(uuid: UUID, name: String): PlayerProfile = runCatching { Bukkit.createProfileExact(uuid, name) }.getOrElse { Bukkit.createProfile(uuid, name) }

    fun Player.applyProfile(profile: PlayerProfile, viewers: Collection<Player>?) {
        scheduler.run(Lukitils.getInstance(), {
            playerProfile = profile

            val newName = Component.text(profile.name ?: name)
            val team = scoreboard.getPlayerTeam(this)

            val fName = if (team != null) Component.empty().append(team.prefix()).append(newName).append(team.suffix()) else newName

            playerListName(fName)
            displayName(fName)
            refreshForViewers(this, (viewers ?: Bukkit.getOnlinePlayers()).filter { it.uniqueId != uniqueId })
        }, null)
    }

    internal fun Player.applyTextureAndRefresh(prop: ProfileProperty, viewers: Collection<Player>?) {
        if (uniqueId !in originalSkins) originalSkins[uniqueId] = playerProfile.properties.firstOrNull { it.name.equals("textures", true) }?.copy()

        val profile = safeProfile(uniqueId, playerProfile.name ?: name).apply {
            setProperties(properties.filterNot { it.name.equals("textures", true) }.plus(prop))
        }

        applyProfile(profile, viewers)
    }


    internal fun refreshForViewers(target: Player, viewers: Collection<Player>) {
        viewers.forEach { v ->
            runCatching {
                v.scheduler.run(Lukitils.getInstance(), {
                    v.hideEntity(Lukitils.getInstance(), target)
                    v.showEntity(Lukitils.getInstance(), target)
                }, null)
            }
        }
    }

    // Player: Identity changes bandage patch
    @EventHandler(priority = EventPriority.LOWEST)
    fun quit(e: PlayerQuitEvent) {
        val p = e.player

        originalNametags.remove(p.uniqueId)
        originalSkins.remove(p.uniqueId)
    }

    // Command Extensions!
    @JvmStatic
    fun CommandContext<CommandSourceStack>.getPlayerOrThrow(arg: String): Player = getArgument(arg, PlayerSelectorArgumentResolver::class.java).resolve(source).stream().findFirst().orElse(null) ?: throw Defaults.NOT_FOUND

    @JvmStatic
    fun CommandContext<CommandSourceStack>.getPlayersOrThrow(arg: String): List<Player> = getArgument(arg, PlayerSelectorArgumentResolver::class.java).resolve(source).stream().toList().takeIf { it.isNotEmpty() } ?: throw Defaults.NOT_FOUND

    // Misc Extensions!
    @JvmStatic
    fun ProfileProperty.copy(): ProfileProperty = ProfileProperty(name, value, signature)

    val isFolia: Boolean by lazy(LazyThreadSafetyMode.PUBLICATION) {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }
}