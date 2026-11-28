
package dev.firuhowa.wolfevolution.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.firuhowa.wolfevolution.WolfEvolutionSounds;
import dev.firuhowa.wolfevolution.network.WhistlePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;

public final class WhistleKeyBindings {

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            "wolfevolution",
                            "whistle"
                    )
            );

    private static final KeyMapping WHISTLE_KEY =
            KeyMappingHelper.registerKeyMapping(
                    new KeyMapping(
                            "key.wolfevolution.whistle",
                            InputConstants.Type.KEYBOARD,
                            InputConstants.KEY_H,
                            CATEGORY
                    )
            );

    private WhistleKeyBindings() {
    }

    public static void initialize() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (WHISTLE_KEY.consumeClick()) {

                if (client.player == null || client.level == null) {
                    continue;
                }

                // 口笛の音をローカル再生
                client.level.playLocalSound(
                        client.player.getX(),
                        client.player.getY(),
                        client.player.getZ(),
                        WolfEvolutionSounds.WHISTLE,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F,
                        false
                );

                // サーバーに口笛を通知
                if (ClientPlayNetworking.canSend(WhistlePayload.TYPE)) {
                    ClientPlayNetworking.send(new WhistlePayload());
                }
            }
        });
    }
}
