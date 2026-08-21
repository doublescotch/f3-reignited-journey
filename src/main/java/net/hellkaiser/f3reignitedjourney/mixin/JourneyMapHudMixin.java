package net.hellkaiser.f3reignitedjourney.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Masque la minimap JourneyMap pendant l'écran de debug F3.
 * Cible le handler commun (onRenderOverlay = dessin de la minimap chaque frame).
 * @Pseudo: inoffensif si JourneyMap est absent.
 */
@Pseudo
@Mixin(targets = "journeymap.client.event.handlers.HudOverlayHandler", remap = false)
public class JourneyMapHudMixin {

    @Inject(method = "onRenderOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void f3reignitedjourney$hideMinimapWhileDebugOpen(CallbackInfo ci) {
        if (Minecraft.getInstance().options.renderDebug) {
            ci.cancel();
        }
    }
}
