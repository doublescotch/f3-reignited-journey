package net.hellkaiser.f3reignitedjourney.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.msymbios.reignitedhud.config.ReignitedHudConfig;
import net.msymbios.reignitedhud.gui.internal.RenderDrawCallback;

/**
 * Calcul de la position ou greffer nos attributs dans la rangee "nourriture/armure"
 * de Reignited HUD.
 *
 * Cette rangee est DYNAMIQUE: chaque entree est conditionnelle (satiete, armure,
 * resistance) et fait avancer le curseur de (largeur du texte + 17). Une position
 * fixe se superposerait des qu'une armure est equipee, ou meme quand une valeur
 * passe de 1 a 2 chiffres. On rejoue donc exactement sa logique de curseur, avec
 * ses propres helpers (memes largeurs de police au pixel pres).
 *
 * Reference: net.msymbios.reignitedhud.gui.GuiWidget#getFoodAndArmor (v1.1.0).
 */
public final class ReignitedRow {
    private ReignitedRow() {}

    /** Y de la rangee, identique chez eux pour l'icone et le texte (31 / 32). */
    public static final int ICON_Y = 31;
    public static final int TEXT_Y = 32;

    /** Ecart applique apres chaque entree: largeur du texte + 5 + 8 + 4. */
    private static final int SPACING_PAD = 17;

    /** Position X (icone) juste apres la derniere entree affichee par Reignited. */
    public static int iconEndX(Player player) {
        boolean skin = bool(ReignitedHudConfig.PLAYER_SKIN, true);
        int x = skin ? 47 : 4;
        return x + consumedWidth(player);
    }

    /** Position X (texte) juste apres la derniere entree affichee par Reignited. */
    public static int textEndX(Player player) {
        boolean skin = bool(ReignitedHudConfig.PLAYER_SKIN, true);
        int x = skin ? 59 : 16;
        return x + consumedWidth(player);
    }

    /** Largeur totale consommee par les entrees natives actuellement visibles. */
    private static int consumedWidth(Player player) {
        int width = 0;

        if (bool(ReignitedHudConfig.FOOD_LEVEL, true)) {
            width += advance(String.valueOf(player.getFoodData().getFoodLevel()));
        }
        if (bool(ReignitedHudConfig.FOOD_SATURATION, true)) {
            width += advance(String.valueOf((int) player.getFoodData().getSaturationLevel()));
        }
        int armor = (int) player.getAttributeValue(Attributes.ARMOR);
        if (bool(ReignitedHudConfig.ARMOR_LEVEL, true) && armor > 0) {
            width += advance(String.valueOf(armor));
        }
        int toughness = (int) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        if (bool(ReignitedHudConfig.ARMOR_TOUGHNESS, true) && toughness > 0) {
            width += advance(String.valueOf(toughness));
        }
        return width;
    }

    /** Avance du curseur pour une entree, avec la meme mesure de police qu'eux. */
    public static int advance(String text) {
        return stringWidth(text) + SPACING_PAD;
    }

    /** L'entree faim est-elle affichee ? (condition de l'insertion apres-faim) */
    public static boolean foodShown() {
        return bool(ReignitedHudConfig.FOOD_LEVEL, true);
    }

    /** Position X (icone) du trou ouvert JUSTE APRES l'entree faim. */
    public static int afterFoodIconX(Player player) {
        int x = bool(ReignitedHudConfig.PLAYER_SKIN, true) ? 47 : 4;
        return x + advance(String.valueOf(player.getFoodData().getFoodLevel()));
    }

    /** Position X (texte) du trou ouvert JUSTE APRES l'entree faim. */
    public static int afterFoodTextX(Player player) {
        int x = bool(ReignitedHudConfig.PLAYER_SKIN, true) ? 59 : 16;
        return x + advance(String.valueOf(player.getFoodData().getFoodLevel()));
    }

    /** Le cadre-portrait (et donc la tete) est-il active dans leur config ? */
    public static boolean skinShown() {
        return bool(ReignitedHudConfig.PLAYER_SKIN, true);
    }

    public static int stringWidth(String text) {
        try {
            return RenderDrawCallback.getStringWidth(text);
        } catch (Throwable t) {
            Minecraft mc = Minecraft.getInstance();
            return mc.font == null ? text.length() * 6 : mc.font.width(text);
        }
    }

    /** L'effet de faim change les icones natives; sans consequence sur nos largeurs. */
    public static boolean hungerEffect(Player player) {
        return player.hasEffect(MobEffects.HUNGER);
    }

    private static boolean bool(net.minecraftforge.common.ForgeConfigSpec.BooleanValue value, boolean fallback) {
        try {
            Boolean b = value.get();
            return b == null ? fallback : b;
        } catch (Throwable t) {
            return fallback; // config pas encore chargee: on suppose l'entree visible
        }
    }
}
