
package dev.firuhowa.wolfevolution.client.mixin;

import dev.firuhowa.wolfevolution.client.AdultWolfModelAccess;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.animal.wolf.AdultWolfModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdultWolfModel.class)
public interface AdultWolfModelAccessorMixin extends AdultWolfModelAccess {

    @Override
    @Accessor("upperBody")
    ModelPart wolfevolution$getUpperBody();

    @Override
    @Accessor("realHead")
    ModelPart wolfevolution$getRealHead();

    @Override
    @Accessor("realTail")
    ModelPart wolfevolution$getRealTail();
}
