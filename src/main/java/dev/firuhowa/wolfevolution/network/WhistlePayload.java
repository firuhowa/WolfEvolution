
package dev.firuhowa.wolfevolution.network;

import dev.firuhowa.wolfevolution.WolfEvolution;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record WhistlePayload() implements CustomPacketPayload {

    public static final Type<WhistlePayload> TYPE =
            new Type<>(WolfEvolution.id("whistle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WhistlePayload> CODEC =
            StreamCodec.unit(new WhistlePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
