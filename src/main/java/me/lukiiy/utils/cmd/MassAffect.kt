package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import me.lukiiy.utils.help.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.title.Title
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration

object MassAffect { // TODO
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("massaffect").requires {it.sender.hasPermission("lukitils.massaffect")}
            .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("effect", StringArgumentType.word())
                .suggests { _, builder ->
                    val input = builder.remaining.uppercase()
                    Effect.entries.forEach { if (it.name.startsWith(input)) builder.suggest(it.name.lowercase()) }
                    builder.buildFuture()
                }
//                .then(Commands.argument("intensity", DoubleArgumentType.doubleArg(0.0))
//                    .executes {
//                        val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND_MULTI.create()
//                        val effect =
//                            try { Effect.valueOf(StringArgumentType.getString(it, "effect").uppercase()) }
//                            catch (e: IllegalArgumentException) { throw Defaults.CUSTOM_ERR("This effect doesn't exist.").create() }
//
//                        handle(it.source.sender, targets, effect, DoubleArgumentType.getDouble(it, "intensity"))
//                        Command.SINGLE_SUCCESS
//                    }) Someday ill fix it
            .executes {
                val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND_MULTI.create()
                val effect =
                    try { Effect.valueOf(StringArgumentType.getString(it, "effect").uppercase()) }
                    catch (e: IllegalArgumentException) { throw Defaults.CUSTOM_ERR("This effect doesn't exist.").create() }

                handle(it.source.sender, targets, effect, 1.0)
                Command.SINGLE_SUCCESS
            }))
        .build()
    }

    private fun handle(sender: CommandSender, targets: List<Player>, effect: Effect, intensity: Double) {
        targets.forEach { effect.act(it, intensity) }
        sender.sendMessage(Defaults.msg(
            if (intensity != 1.0) Component.text("Applying ${effect.name} with intensity $intensity to ").append(PlayerUtils.group(targets, Style.style(Defaults.YELLOW)))
            else Component.text("Applying ${effect.name} to ").append(PlayerUtils.group(targets, Style.style(Defaults.YELLOW)))
        ))
    }

    enum class Effect(val act: (Player, Double) -> Unit) {
        DEMO({ p, _ -> p.showDemoScreen() }),
        CREDITS({ p, _ -> p.showWinScreen() }),
        GUARDIAN({ p, _ -> p.showElderGuardian() }),
        KABOOM({ p, mult -> p.apply {
            world.apply {
                spawnParticle(Particle.EXPLOSION_EMITTER, p.location.add(0.0, 1.0, 0.0), 1)
                strikeLightningEffect(p.location)
            }
            velocity = velocity.setY(15 * mult)
            showTitle(Title.title(Component.text("KABOOM!").color(Defaults.RED), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))))
        }});
//        SIZEMOD({ p, mult ->
//            val key = NamespacedKey(Lukitils.getInstance(), "sizeMod")
//            val scale = 1.0 * mult
//
//            PlayerUtils.applyScalarTransientAttribute(p, Attribute.SCALE, key, scale)
//            PlayerUtils.applyScalarTransientAttribute(p, Attribute.STEP_HEIGHT, key, scale)
//            PlayerUtils.applyScalarTransientAttribute(p, Attribute.BLOCK_INTERACTION_RANGE, key, scale)
//            PlayerUtils.applyScalarTransientAttribute(p, Attribute.ENTITY_INTERACTION_RANGE, key, scale)
//        })
    }
}