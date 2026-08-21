package net.hellkaiser.f3reignitejourney;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Masque la hotbar vanilla pendant l'écran de debug F3 (lisibilité BetterF3).
 * Effet de bord voulu: annuler le Pre de la hotbar supprime aussi son Post,
 * donc le rendu Reignited (accroché au Post hotbar par notre mixin) disparaît
 * aussi pendant F3 — double verrou avec le mixin.
 */
@Mod.EventBusSubscriber(modid = F3ReigniteJourney.MOD_ID, value = Dist.CLIENT)
public class DebugHudHider {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (Minecraft.getInstance().options.renderDebug
                && event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            event.setCanceled(true);
        }
    }
}
