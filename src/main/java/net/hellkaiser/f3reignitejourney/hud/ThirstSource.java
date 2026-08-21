package net.hellkaiser.f3reignitejourney.hud;

import net.minecraft.world.entity.player.Player;

/**
 * Source unifiee d'information de soif.
 *
 * Deux mods peuvent l'alimenter — Tough As Nails et Thirst Was Taken — avec des
 * modeles internes differents (API statique d'un cote, capability Forge de l'autre).
 * Le HUD ne connait que cette interface: un seul rendu, quelle que soit la source.
 */
public interface ThirstSource {

    /** Le mod source est present ET sa mecanique de soif est active. */
    boolean available();

    /** Valeur principale affichee (0-20 chez les deux mods). */
    int thirst(Player player);

    /**
     * Valeur secondaire, equivalent de la saturation: hydratation (TAN) ou
     * "quenched" (TWT). A zero, l'icone clignote — comme dans le HUD d'origine.
     */
    float secondary(Player player);

    /** Etat "assoiffe": change la couleur de l'icone et du compteur. */
    boolean thirsty(Player player);

    /** Nom court pour les logs. */
    String sourceName();
}
