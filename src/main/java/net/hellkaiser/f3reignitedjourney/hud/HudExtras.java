package net.hellkaiser.f3reignitedjourney.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.msymbios.reignitedhud.gui.internal.RenderDrawCallback;

/**
 * Greffe la soif (et la temperature avec Tough As Nails) dans la rangee de
 * Reignited HUD, et supprime la barre que le mod source dessinait a l'emplacement
 * vanilla — celui-ci ayant disparu, elle y flottait toute seule.
 *
 * Aucun mixin: on se branche apres le rendu de Reignited (meme evenement, priorite
 * plus basse) et on dessine avec SES helpers, donc au pixel et a la police pres.
 */
public final class HudExtras {
    private HudExtras() {}

    public static final String REIGNITED = "reignitedhud";

    private static final ResourceLocation DROP =
            new ResourceLocation("f3reignitedjourney", "textures/gui/drop.png");
    private static final ResourceLocation TEMPERATURE =
            new ResourceLocation("f3reignitedjourney", "textures/gui/temperature.png");

    /** Overlay Forge de Thirst Was Taken (enregistre au-dessus de FOOD_LEVEL). */
    private static final ResourceLocation TWT_OVERLAY = new ResourceLocation("thirst", "thirst_level");

    private static final int COLOR_THIRST = 0x54D0FF;
    private static final int SHADOW_THIRST = 0x143C57;
    private static final int COLOR_THIRST_LOW = 0xE05A3A;
    private static final int SHADOW_THIRST_LOW = 0x3D1409;

    /** ICY, COLD, NEUTRAL, WARM, HOT — memes teintes que le HUD d'origine. */
    private static final int[] TEMPERATURE_COLORS = {0xB0E0FF, 0x5AA9FF, 0xC2C2C2, 0xFFB24C, 0xFF5A3C};

    private static boolean active() {
        return ModList.get().isLoaded(REIGNITED) && ThirstSources.get() != null;
    }

    // ------------------------------------------------------------ insertion ---

    /** Decalage (px) applique aux entrees de la rangee apres la faim. */
    private static int rowShiftPx;
    /** Vrai quand la goutte s'insere apres la faim (faim visible + soif active). */
    private static boolean insertAfterFood;

    /**
     * Appele par GuiWidgetMixin en tete de getFoodAndArmor, AVANT que la rangee
     * ne soit dessinee: fige pour cette frame la largeur du bloc soif que les
     * redirects intercalent apres l'entree faim.
     */
    public static void computeRowShift(LocalPlayer player) {
        rowShiftPx = 0;
        insertAfterFood = false;
        try {
            ThirstSource source = ThirstSources.get();
            if (source == null || player == null) return;
            if (!ThirstSources.shouldRender(player)) return;
            if (!ReignitedRow.foodShown()) return;   // faim masquee: retour en fin de rangee
            rowShiftPx = ReignitedRow.advance(String.valueOf(source.thirst(player)));
            insertAfterFood = true;
        } catch (Throwable ignored) {
        }
    }

    public static int rowShift() {
        return rowShiftPx;
    }

    // ------------------------------------------------------- tete du joueur ---

    /** Position et taille du visage dans le cadre-portrait (drawPlayerIcon(21,17,17)). */
    private static final int HEAD_X = 21, HEAD_Y = 17, HEAD_SIZE = 17;

    /**
     * Redessine le visage du skin au z normal du HUD. Reignited le dessine en
     * draw direct a z = -1000: cette passe echoue au test de profondeur en jeu
     * (la vignette plein ecran a deja ecrit la sienne) et le cadre restait
     * vide hors menus. Plutot que de lutter contre son etat GPU, on repeint le
     * visage proprement par-dessus: face (8,8) puis calque chapeau (40,8).
     */
    private static void drawHead(GuiGraphics graphics, LocalPlayer player) {
        if (!ReignitedRow.skinShown()) return;
        ResourceLocation skin = player.getSkinTextureLocation();
        graphics.blit(skin, HEAD_X, HEAD_Y, HEAD_SIZE, HEAD_SIZE, 8.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.enableBlend();
        graphics.blit(skin, HEAD_X, HEAD_Y, HEAD_SIZE, HEAD_SIZE, 40.0F, 8.0F, 8, 8, 64, 64);
        RenderSystem.disableBlend();
    }

    /**
     * La barre du mod source n'a plus lieu d'etre: son ancrage (la barre de faim
     * vanilla) est supprime par Reignited HUD.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (!active()) return;
        if (TWT_OVERLAY.equals(event.getOverlay().id())) {
            event.setCanceled(true);
        }
    }

    /**
     * Dessine nos attributs juste apres ceux de Reignited. Il s'accroche au Post de
     * la HOTBAR (le seul que notre patch de deduplication laisse passer), donc une
     * priorite plus basse nous place apres lui, dans la meme frame.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (!ModList.get().isLoaded(REIGNITED)) return;
        if (!VanillaGuiOverlay.HOTBAR.id().equals(event.getOverlay().id())) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) return;

        GuiGraphics graphics = event.getGuiGraphics();

        // La tete ne depend d'aucun mod de soif: des que Reignited est la.
        try {
            drawHead(graphics, player);
        } catch (Throwable ignored) {
        }

        if (ThirstSources.get() == null || !ThirstSources.shouldRender(player)) return;

        try {
            // Faim visible: la goutte s'insere dans le trou ouvert par les
            // redirects, juste apres l'entree faim. Sinon: fin de rangee.
            int iconX = insertAfterFood ? ReignitedRow.afterFoodIconX(player)
                                        : ReignitedRow.iconEndX(player);
            int textX = insertAfterFood ? ReignitedRow.afterFoodTextX(player)
                                        : ReignitedRow.textEndX(player);

            ThirstSource source = ThirstSources.get();
            String value = String.valueOf(source.thirst(player));
            boolean low = source.thirsty(player);
            float alpha = source.secondary(player) > 0.0F ? 1.0F : pulse();

            drawIcon(graphics, DROP, iconX, low ? COLOR_THIRST_LOW : COLOR_THIRST, alpha);
            RenderDrawCallback.drawFontWithShadow(graphics, value, textX, ReignitedRow.TEXT_Y,
                    low ? COLOR_THIRST_LOW : COLOR_THIRST, low ? SHADOW_THIRST_LOW : SHADOW_THIRST);

            if (ThirstSources.hasTemperature()) {
                // Fin REELLE de la rangee: fin native + largeur du bloc insere.
                int tempX = insertAfterFood ? ReignitedRow.iconEndX(player) + rowShiftPx
                                            : iconX + ReignitedRow.advance(value);
                drawIcon(graphics, TEMPERATURE, tempX, temperatureColor(player), 1.0F);
            }
        } catch (Throwable ignored) {
            // un HUD tiers ne doit jamais casser le rendu du jeu
        }
    }

    /** Clignotement lent quand la valeur secondaire est a zero. */
    private static float pulse() {
        double phase = (double) System.currentTimeMillis() / 150.0;
        return 0.35F + 0.65F * (0.5F + 0.5F * (float) Math.sin(phase));
    }

    /**
     * Meme technique que la police de Reignited (drawFontWithShadow): une copie
     * sombre decalee de +1,+1 puis le glyphe teinte. La couleur d'ombre est le
     * quart de la teinte, la convention des ombres de texte Minecraft. Les
     * textures gardent leur ligne et colonne 9 vides pour que l'ombre reste
     * dans la case de 10x10.
     */
    private static void drawIcon(GuiGraphics graphics, ResourceLocation icon, int x, int color, float alpha) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderSystem.enableBlend();
        graphics.setColor(r / 4.0F, g / 4.0F, b / 4.0F, alpha);
        graphics.blit(icon, x + 1, ReignitedRow.ICON_Y + 1, 0.0F, 0.0F, 10, 10, 10, 10);
        graphics.setColor(r, g, b, alpha);
        graphics.blit(icon, x, ReignitedRow.ICON_Y, 0.0F, 0.0F, 10, 10, 10, 10);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    /** Isole dans une methode a part: TemperatureHelper n'est touche qu'avec TAN. */
    private static int temperatureColor(net.minecraft.world.entity.player.Player player) {
        try {
            int level = toughasnails.api.temperature.TemperatureHelper
                    .getTemperatureForPlayer(player).ordinal();
            if (level < 0) level = 0;
            if (level >= TEMPERATURE_COLORS.length) level = TEMPERATURE_COLORS.length - 1;
            return TEMPERATURE_COLORS[level];
        } catch (Throwable t) {
            return 0xC2C2C2;
        }
    }
}
