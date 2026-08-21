package net.hellkaiser.f3reignitedjourney;

import net.hellkaiser.f3reignitedjourney.hud.HudExtras;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Micro-mod compagnon de Reignited HUD.
 *
 * - masque Reignited HUD, la hotbar et la minimap JourneyMap pendant F3
 *   (les icones d'items sont rendues a z+200, elles traversent les fonds de texte
 *   des ecrans de debug) — dans les mixins;
 * - dedoublonne le rendu de Reignited HUD (gain de FPS) — dans les mixins;
 * - greffe la soif, et la temperature avec Tough As Nails, dans la rangee de
 *   Reignited HUD — ici, sans mixin.
 *
 * Chaque volet est independant et inerte si son mod cible est absent.
 */
@Mod(F3ReignitedJourney.MOD_ID)
public class F3ReignitedJourney {
    public static final String MOD_ID = "f3reignitedjourney";

    public F3ReignitedJourney() {
        if (FMLEnvironment.dist.isClient() && ModList.get().isLoaded(HudExtras.REIGNITED)) {
            MinecraftForge.EVENT_BUS.register(HudExtras.class);
        }
    }
}
