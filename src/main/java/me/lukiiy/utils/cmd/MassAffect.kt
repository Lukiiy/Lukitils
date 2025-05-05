package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.addScalarTransientMod
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.group
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.title.Title
import org.bukkit.Particle
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration
import java.util.concurrent.CompletableFuture

object MassAffect {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("massaffect")
            .requires { it.sender.hasPermission("massaffect".asPermission()) }
            .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("effect", EffectArgument())
                .then(Commands.argument("intensity", DoubleArgumentType.doubleArg(0.0))
                    .executes {
                        val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                        handle(it.source.sender, targets, it.getArgument("effect", Effect::class.java), DoubleArgumentType.getDouble(it, "intensity"))
                        Command.SINGLE_SUCCESS
                    })
            .executes {
                val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                handle(it.source.sender, targets, it.getArgument("effect", Effect::class.java), 1.0)
                Command.SINGLE_SUCCESS
            }))
        .build()
    }

    private fun handle(sender: CommandSender, targets: List<Player>, effect: Effect, intensity: Double) {
        targets.forEach { effect.act(it, intensity) }

        var msg = "Applying ${effect.name}${if (intensity != 1.0) " with intensity $intensity" else ""} to "
        if (intensity == 0.0) msg = "Reverting ${effect.name}'s changes for "

        Utils.adminCmdFeedback(sender, "Massaffected ${targets.group()} with ${effect.name}")
        sender.sendMessage(Defaults.success(Component.text(msg).append(targets.group(Style.style(Defaults.YELLOW)))))
    }

    private enum class Effect(val act: (Player, Double) -> Unit, val desc: String) {
        DEMO({ p, _ -> p.showDemoScreen() }, "Shows the demo screen"),
        CREDITS({ p, _ -> p.showWinScreen() }, "Shows the credits screen"),
        GUARDIAN({ p, _ -> p.showElderGuardian() }, "Shows the Elder Guardian jumpscare"),
        KABOOM({ p, _ -> p.apply {
            world.apply {
                spawnParticle(Particle.EXPLOSION_EMITTER, p.location.add(0.0, 1.0, 0.0), 1)
                strikeLightningEffect(p.location)
            }
            velocity = velocity.setY(15)
            showTitle(Title.title(Component.text("KABOOM!").color(Defaults.RED), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))))
        }}, "Sends players flying!"),
        SIZEMOD({ p, mult ->
            val key = "sizeMod"
            val scale: Double = 1.0 * mult

            p.addScalarTransientMod(key, Attribute.SCALE, scale)
            p.addScalarTransientMod(key, Attribute.STEP_HEIGHT, scale)
            p.addScalarTransientMod(key, Attribute.BLOCK_INTERACTION_RANGE, scale)
            p.addScalarTransientMod(key, Attribute.ENTITY_INTERACTION_RANGE, scale)
            p.addScalarTransientMod(key, Attribute.SAFE_FALL_DISTANCE, if (mult > 0) scale/4 + 1 else 0.0)
        }, "Changes the size of the players"),
        LOWGRAVITY({ p, mult ->
            val key = "lowGravity"
            val scale: Double = -0.8 * mult

            p.addScalarTransientMod(key, Attribute.GRAVITY, scale)
            p.addScalarTransientMod(key, Attribute.SAFE_FALL_DISTANCE, if (mult > 0) scale + 4 else 0.0)
        }, "Reduces the gravity..."),
        LONGARMS({ p, mult ->
            val key = "longArms"
            val scale: Double = 2.0 * mult

            p.addScalarTransientMod(key, Attribute.BLOCK_INTERACTION_RANGE, scale)
            p.addScalarTransientMod(key, Attribute.ENTITY_INTERACTION_RANGE, scale)
        }, "Increases the reach")
    }

    private class EffectArgument : CustomArgumentType.Converted<Effect, String> {
        override fun convert(type: String): Effect {
            try { return Effect.valueOf(type.uppercase()) }
            catch (_: IllegalArgumentException) { throw Defaults.CmdException(Component.text("Effect \"$type\" doesn't exist.")) }
        }

        override fun getNativeType(): ArgumentType<String> = StringArgumentType.string()
        override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
            val input = builder.remaining.uppercase()
            Effect.entries.forEach { if (it.name.startsWith(input)) builder.suggest(it.name, MessageComponentSerializer.message().serialize(Component.text("→ ").color(Defaults.ORANGE).append(Component.text(it.desc).color(Defaults.YELLOW)))) }
            return builder.buildFuture()
        }
    }
}