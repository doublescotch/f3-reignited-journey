package net.hellkaiser.f3reignitejourney.hud;

import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.world.entity.player.Player;

/**
 * Source de soif: Thirst Was Taken (modid "thirst").
 *
 * ATTENTION: son ThirstHelper public ne concerne que les ITEMS. La soif DU JOUEUR
 * est une capability Forge — c'est elle qu'on lit ici.
 * Classe chargee UNIQUEMENT si le modid "thirst" est present (cf. ThirstSources).
 */
public final class TwtThirstSource implements ThirstSource {

    /** En dessous de ce seuil, l'affichage passe en couleur d'alerte. */
    private static final int LOW_THIRST = 6;

    private static IThirst cap(Player player) {
        try {
            return player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public boolean available() {
        return true; // le mod est present; le tick par joueur est verifie au rendu
    }

    @Override
    public int thirst(Player player) {
        IThirst thirst = cap(player);
        return thirst == null ? 0 : thirst.getThirst();
    }

    @Override
    public float secondary(Player player) {
        IThirst thirst = cap(player);
        return thirst == null ? 0.0F : thirst.getQuenched();
    }

    @Override
    public boolean thirsty(Player player) {
        IThirst thirst = cap(player);
        return thirst != null && thirst.getThirst() <= LOW_THIRST;
    }

    /** TWT peut desactiver la soif par joueur (creatif, etc.): on n'affiche rien alors. */
    public static boolean tickingFor(Player player) {
        IThirst thirst = cap(player);
        return thirst != null && thirst.getShouldTickThirst();
    }

    @Override
    public String sourceName() {
        return "Thirst Was Taken";
    }
}
