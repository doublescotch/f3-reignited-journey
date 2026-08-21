package net.hellkaiser.f3reignitejourney.mixin;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Deux correctifs pour Reignited HUD, sans toucher à son jar:
 *
 * 1. PERF — son renderOverlay écoute RenderGuiOverlayEvent.Post SANS filtrer
 *    l'overlay: le HUD complet est redessiné une fois par overlay vanilla,
 *    soit ~15-20 fois par frame (drop FPS connu, non corrigé upstream).
 *    On ne laisse passer que le Post de la HOTBAR → un seul rendu par frame.
 *
 * 2. F3 — on coupe le rendu quand l'écran de debug est ouvert (les icônes
 *    d'items du HUD, rendues à z+200, passaient au-dessus des fonds BetterF3).
 *
 * @Pseudo: cible absente (Reignited non installé) = mixin ignoré sans erreur.
 */
@Pseudo
@Mixin(targets = "net.msymbios.reignitedhud.gui.GuiWidget", remap = false)
public class GuiWidgetMixin {

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void f3reignitejourney$dedupeAndHideOnDebug(RenderGuiOverlayEvent.Post event, CallbackInfo ci) {
        // un seul rendu par frame: celui qui suit la hotbar
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            ci.cancel();
            return;
        }
        // masquage pendant l'écran de debug F3
        if (Minecraft.getInstance().options.renderDebug) {
            ci.cancel();
        }
    }
}
