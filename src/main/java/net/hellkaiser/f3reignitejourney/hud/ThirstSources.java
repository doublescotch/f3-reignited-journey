package net.hellkaiser.f3reignitejourney.hud;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolution de la source de soif, une fois pour toutes au premier acces.
 *
 * Priorite: Tough As Nails s'il est la (il apporte aussi la temperature), sinon
 * Thirst Was Taken. Aucune classe d'integration n'est touchee si son mod est absent:
 * le simple fait de ne pas appeler ces classes suffit a ne jamais les charger.
 */
public final class ThirstSources {
    private ThirstSources() {}

    private static final Logger LOGGER = LoggerFactory.getLogger("f3reignitejourney");

    public static final String TAN = "toughasnails";
    public static final String TWT = "thirst";

    private static boolean resolved = false;
    private static ThirstSource source;
    private static boolean temperature;

    private static void resolve() {
        if (resolved) return;
        resolved = true;
        try {
            if (ModList.get().isLoaded(TAN)) {
                source = new TanThirstSource();
                temperature = true;
            } else if (ModList.get().isLoaded(TWT)) {
                source = new TwtThirstSource();
            }
            if (source != null) {
                LOGGER.info("Thirst source: {}{}", source.sourceName(), temperature ? " (+ temperature)" : "");
            }
        } catch (Throwable t) {
            source = null;
            temperature = false;
        }
    }

    /** Source active, ou null si aucun mod de soif n'est installe. */
    public static ThirstSource get() {
        resolve();
        return source;
    }

    /** La temperature n'existe que chez Tough As Nails. */
    public static boolean hasTemperature() {
        resolve();
        return temperature;
    }

    /** Faut-il afficher quelque chose pour ce joueur ? */
    public static boolean shouldRender(Player player) {
        ThirstSource s = get();
        if (s == null || player == null || !s.available()) return false;
        // TWT peut couper la soif joueur par joueur (creatif...): on respecte ce choix.
        if (s instanceof TwtThirstSource) {
            return TwtThirstSource.tickingFor(player);
        }
        return true;
    }
}
