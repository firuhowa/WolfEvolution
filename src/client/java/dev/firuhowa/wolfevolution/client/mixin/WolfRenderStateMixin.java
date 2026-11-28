
package dev.firuhowa.wolfevolution.client.mixin;

import dev.firuhowa.wolfevolution.client.WolfRenderStateAccess;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WolfRenderState.class)
public abstract class WolfRenderStateMixin implements WolfRenderStateAccess {

    @Unique
    private boolean wolfevolution$sleeping;

    @Override
    public boolean wolfevolution$isSleeping() {
        return wolfevolution$sleeping;
    }

    @Override
    public void wolfevolution$setSleeping(boolean sleeping) {
        this.wolfevolution$sleeping = sleeping;
    }
}
