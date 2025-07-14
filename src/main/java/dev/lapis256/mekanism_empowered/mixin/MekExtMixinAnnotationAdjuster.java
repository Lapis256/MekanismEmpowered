package dev.lapis256.mekanism_empowered.mixin;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Objects;


public class MekExtMixinAnnotationAdjuster implements MixinAnnotationAdjuster {
    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode, AdjustableAnnotationNode annotationNode) {
        System.out.println(mixinClassName);

        // Disables destructive mixins by Mekanism Extras.
        if(Objects.equals(mixinClassName, "com.jerry.mekextras.mixin.MixinMachineEnergyContainer") && annotationNode.is(Overwrite.class)) {
            handlerNode.name = "mekanism_empowered$disabled";
            return null;
        }
        return annotationNode;
    }
}
