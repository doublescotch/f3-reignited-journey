package net.hellkaiser.f3reignitedjourney.mixin;

import net.hellkaiser.f3reignitedjourney.hud.HudExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.msymbios.reignitedhud.gui.internal.RenderDrawCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Trois correctifs pour Reignited HUD, sans toucher à son jar:
 *
 * 1. PERF — son renderOverlay écoute RenderGuiOverlayEvent.Post SANS filtrer
 *    l'overlay: le HUD complet est redessiné une fois par overlay vanilla,
 *    soit ~15-20 fois par frame (drop FPS connu, non corrigé upstream).
 *    On ne laisse passer que le Post de la HOTBAR → un seul rendu par frame.
 *
 * 2. F3 — on coupe le rendu quand l'écran de debug est ouvert (les icônes
 *    d'items du HUD, rendues à z+200, passaient au-dessus des fonds BetterF3).
 *
 * 3. INSERTION DE LA SOIF APRÈS LA FAIM — la rangée est dessinée entrée par
 *    entrée dans getFoodAndArmor, chaque entrée avançant le curseur. Les deux
 *    redirects décalent toutes les entrées APRÈS la première (la faim) de la
 *    largeur de notre bloc soif; HudExtras dessine ensuite la goutte dans le
 *    trou ainsi ouvert. Décalage nul quand aucun mod de soif n'est présent.
 *
 * @Pseudo: cible absente (Reignited non installé) = mixin ignoré sans erreur.
 */
@Pseudo
@Mixin(targets = "net.msymbios.reignitedhud.gui.GuiWidget", remap = false)
public class GuiWidgetMixin {

    @Unique private static int f3rj$icons;
    @Unique private static int f3rj$texts;

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true, require = 0)
    private void f3reignitedjourney$dedupeAndHideOnDebug(RenderGuiOverlayEvent.Post event, CallbackInfo ci) {
        // un seul rendu par frame: celui qui suit la hotbar
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            ci.cancel();
            return;
        }
        // masquage pendant l'écran de debug F3
        if (Minecraft.getInstance().options.renderDebug) {
            ci.cancel();
            return;
        }
        // TÊTE DU JOUEUR — dessinée ICI, AVANT le rendu du widget: le cadre de
        // Reignited passe ensuite PAR-DESSUS et recouvre les bords du visage,
        // exactement l'empilement de son dessin d'origine (le sien, à z=-1000,
        // échoue au test de profondeur en jeu direct et reste vide). La
        // dessiner après tout — comme en 1.1.4/1.1.5 — la posait AU-DESSUS du
        // cadre: bordure visible en trop et pixels débordants (retour in-game).
        HudExtras.drawHeadUnderFrame(event.getGuiGraphics());
    }

    @Inject(method = "getFoodAndArmor", at = @At("HEAD"), require = 0)
    private void f3reignitedjourney$beginRow(LocalPlayer player, GuiGraphics graphics, CallbackInfo ci) {
        f3rj$icons = 0;
        f3rj$texts = 0;
        HudExtras.computeRowShift(player);
    }

    @Redirect(method = "getFoodAndArmor", require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/msymbios/reignitedhud/gui/internal/RenderDrawCallback;drawIcon(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/GuiGraphics;IIII)V"))
    private void f3reignitedjourney$shiftIcon(ResourceLocation tex, GuiGraphics graphics,
                                              int x, int y, int row, int pos) {
        f3rj$icons++;
        int shift = f3rj$icons >= 2 ? HudExtras.rowShift() : 0;
        RenderDrawCallback.drawIcon(tex, graphics, x + shift, y, row, pos);
    }

    @Redirect(method = "getFoodAndArmor", require = 0,
            at = @At(value = "INVOKE",
                    target = "Lnet/msymbios/reignitedhud/gui/internal/RenderDrawCallback;drawFontWithShadow(Lnet/minecraft/client/gui/GuiGraphics;Ljava/lang/String;IIII)V"))
    private void f3reignitedjourney$shiftText(GuiGraphics graphics, String text,
                                              int x, int y, int color, int shadow) {
        f3rj$texts++;
        int shift = f3rj$texts >= 2 ? HudExtras.rowShift() : 0;
        RenderDrawCallback.drawFontWithShadow(graphics, text, x + shift, y, color, shadow);
    }
}
