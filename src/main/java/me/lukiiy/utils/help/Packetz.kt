package me.lukiiy.utils.help

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.BrandPayload
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object Packetz {
    fun brand(p: Player, brand: Component?) {
        if (brand == null || brand == Component.empty()) return
        val fBrand = LegacyComponentSerializer.legacySection().serialize(brand)

        try {
            (p as CraftPlayer).handle.connection.send(ClientboundCustomPayloadPacket(BrandPayload(fBrand)))
        } catch (_: Exception) {}
    }
}