package me.lukiiy.utils.idk

import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import me.lukiiy.utils.help.MassEffect
import me.lukiiy.utils.help.Utils.addScalarTransientMod
import me.lukiiy.utils.help.Utils.boundedScale
import me.lukiiy.utils.help.Utils.removeTransientMod
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration


object LukiMassEffects {
    private val DEMO: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) = player.showDemoScreen()
        override fun description() = "Shows the demo screen"
        override fun name() = "Demo"
        override fun id() = "demo"
    }

    private val CREDITS: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) = player.showWinScreen()
        override fun description() = "Shows the credits screen"
        override fun name() = "Credits"
        override fun id() = "credits"
    }

    private val GUARDIAN: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) = player.showElderGuardian()
        override fun description() = "Shows the Elder Guardian jumpscare"
        override fun name() = "Guardian"
        override fun id() = "guardian"
    }

    private val KABOOM: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) {
            player.apply {
                world.apply {
                    spawnParticle(Particle.EXPLOSION_EMITTER, location.add(0.0, 1.0, 0.0), 1)
                    strikeLightningEffect(location)
                }

                velocity = velocity.setY(15 + intensity)
                showTitle(Title.title(Component.text("KABOOM!").color(Defaults.RED), Component.empty(), Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ofSeconds(1))))
            }
        }

        override fun name() = "Kaboom"
        override fun description() = "Sends players flying!"
        override fun id() = "kaboom"
    }

    private val SIZEMOD: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) {
            val scale = intensity.boundedScale()

            player.apply {
                addScalarTransientMod(id(), Attribute.SCALE, scale)
                addScalarTransientMod(id(), Attribute.STEP_HEIGHT, scale)
                addScalarTransientMod(id(), Attribute.BLOCK_INTERACTION_RANGE, scale / 1.75)
                addScalarTransientMod(id(), Attribute.ENTITY_INTERACTION_RANGE, scale / 2)
                addScalarTransientMod(id(), Attribute.SAFE_FALL_DISTANCE, if (intensity > 0) scale / 4 + 1 else 0.0)
            }
        }

        override fun clear(player: Player) {
            player.apply {
                removeTransientMod(id(), Attribute.SCALE)
                removeTransientMod(id(), Attribute.STEP_HEIGHT)
                removeTransientMod(id(), Attribute.BLOCK_INTERACTION_RANGE)
                removeTransientMod(id(), Attribute.ENTITY_INTERACTION_RANGE)
                removeTransientMod(id(), Attribute.SAFE_FALL_DISTANCE)
            }
        }

        override fun name() = "Size Mod"
        override fun description() = "Changes the size of the players"
        override fun id() = "sizemod"
    }

    private val LOWGRAVITY: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) {
            val scale: Double = 0.8 * -intensity.boundedScale(1.0)

            player.apply {
                addScalarTransientMod(id(), Attribute.GRAVITY, scale)
                addScalarTransientMod(id(), Attribute.SAFE_FALL_DISTANCE, if (intensity > 0) scale + 4 else 0.0)
            }
        }

        override fun clear(player: Player) {
            player.apply {
                removeTransientMod(id(), Attribute.GRAVITY)
                removeTransientMod(id(), Attribute.SAFE_FALL_DISTANCE)
            }
        }

        override fun name() = "Low Gravity"
        override fun description() = "Reduces the gravity..."
        override fun id() = "lowgravity"
    }

    private val LONGARMS: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) {
            val scale = intensity.boundedScale()

            player.apply {
                addScalarTransientMod(id(), Attribute.BLOCK_INTERACTION_RANGE, scale)
                addScalarTransientMod(id(), Attribute.ENTITY_INTERACTION_RANGE, scale)
            }
        }

        override fun clear(player: Player) {
            player.apply {
                removeTransientMod(id(), Attribute.BLOCK_INTERACTION_RANGE)
                removeTransientMod(id(), Attribute.ENTITY_INTERACTION_RANGE)
            }
        }

        override fun name() = "Long Arms"
        override fun description() = "Increases the reach"
        override fun id() = "longarms"
    }

    private val INVBOOM: MassEffect = object : MassEffect {
        override fun apply(player: Player, intensity: Double) {
            val force = intensity - 1

            player.inventory.apply {
                contents.filterNotNull().forEach {
                    drop(player, it, force)
                    it.amount = 0
                }

                armorContents.filterNotNull().forEach {
                    drop(player, it, force)
                    it.amount = 0
                }

                drop(player, itemInOffHand, force)
                setItemInOffHand(null)
            }

            player.updateInventory()
        }

        fun drop(p: Player, i: ItemStack, d: Double) {
            p.world.dropItemNaturally(p.location.add(0.0, 1.0, 1.0), i) {
                it.apply {
                    thrower = p.uniqueId
                    pickupDelay = 60
                    if (d > 0.0) velocity = velocity.multiply(d).setY(0)
                }
            }
        }

        override fun name() = "Inventory Explosion"
        override fun description() = "Drops every item of a player"
        override fun id() = "inventoryboom"
    }

    fun init() {
        Lukitils.getInstance().apply {
            addMassEffect(DEMO)
            addMassEffect(CREDITS)
            addMassEffect(GUARDIAN)
            addMassEffect(KABOOM)
            addMassEffect(SIZEMOD)
            addMassEffect(LOWGRAVITY)
            addMassEffect(LONGARMS)
            addMassEffect(INVBOOM)
        }
    }

}