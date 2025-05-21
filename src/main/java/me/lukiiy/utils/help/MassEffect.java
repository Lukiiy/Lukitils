package me.lukiiy.utils.help;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to make MassEffects. You can register them in {@link me.lukiiy.utils.Lukitils#addMassEffect(MassEffect)}
 */
@ApiStatus.Experimental
public interface MassEffect {
    /**
     * Apply the effect to a player with the given intensity.
     * @param player The target player.
     * @param intensity The strength or scale of the effect. 1.0 is default, 0.0 means clear.
     */
    void apply(@NotNull Player player, double intensity);

    /**
     * Revert or clear the effect from the player.
     * Should undo all changes made by apply().
     * @param player The target player.
     */
    default void clear(@NotNull Player player) {}

    /**
     * A display name for the effect.
     * @return The name.
     */
    @NotNull String name();

    /**
     * A description for the effect (What it does).
     * @return The description.
     */
    @NotNull String description();

    /**
     * Get the ID for the effect.
     * @return The ID.
     */
    @NotNull String id();
}
