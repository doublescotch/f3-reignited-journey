package net.hellkaiser.f3reignitejourney.hud;

import net.minecraft.world.entity.player.Player;
import toughasnails.api.potion.TANEffects;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;

/**
 * Source de soif: Tough As Nails.
 * Classe chargee UNIQUEMENT si le modid "toughasnails" est present (cf. ThirstSources).
 */
public final class TanThirstSource implements ThirstSource {

    @Override
    public boolean available() {
        try {
            return ThirstHelper.isThirstEnabled();
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public int thirst(Player player) {
        try {
            IThirst thirst = ThirstHelper.getThirst(player);
            return thirst == null ? 0 : thirst.getThirst();
        } catch (Throwable t) {
            return 0;
        }
    }

    @Override
    public float secondary(Player player) {
        try {
            IThirst thirst = ThirstHelper.getThirst(player);
            return thirst == null ? 0.0F : thirst.getHydration();
        } catch (Throwable t) {
            return 0.0F;
        }
    }

    @Override
    public boolean thirsty(Player player) {
        try {
            return player.hasEffect(TANEffects.THIRST);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public String sourceName() {
        return "Tough As Nails";
    }
}
