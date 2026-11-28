
package dev.firuhowa.wolfevolution;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class WolfEvolutionSounds {

    public static final SoundEvent WHISTLE =
            registerSound("whistle");

    private WolfEvolutionSounds() {
    }

    private static SoundEvent registerSound(String name) {
        var identifier = WolfEvolution.id(name);

        return Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                identifier,
                SoundEvent.createVariableRangeEvent(identifier)
        );
    }

    public static void initialize() {
        WolfEvolution.LOGGER.info(
                "WolfEvolution whistle sound registered."
        );
    }
}
