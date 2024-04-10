package me.lukiiy.utils.cool

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit

class Presets {
    private object Default {
        const val PRE = "✎"
        const val PRE2 = "⛈"
    }

    companion object {
        val PRIMARY = TextColor.color(0x7b88b0)
        val SECONDARY = TextColor.color(0x505891)
        val ACCENT_TRUE = TextColor.color(0x85ff73)
        val ACCENT_FALSE = TextColor.color(0xff6e90)
        val ACCENT_NEUTRAL = TextColor.color(0xfff373)

        val PREFIX = Component.text(Default.PRE + " ").color(PRIMARY)
        val PREFIX_WARN = Component.text(Default.PRE2 + " ").color(PRIMARY)

        fun msg(msg: Component?): Component {
            return PREFIX.append(msg!!).color(SECONDARY)
        }

        fun msg(msg: String?): Component {
            return PREFIX.append(why(msg!!))
        }

        fun warnMsg(msg: Component?): Component {
            return PREFIX_WARN.append(msg!!).color(SECONDARY)
        }

        fun warnMsg(msg: String?): Component {
            return PREFIX_WARN.append(Component.text(msg!!)).color(SECONDARY)
        }

        fun debugMsg(msg: String) {
            Bukkit.getServer().logger.info(Default.PRE + " " + msg)
        }

        fun why(msg: String?): Component {
            return Component.text(msg!!).color(SECONDARY)
        }
    }
}