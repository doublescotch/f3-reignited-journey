# Local compile-only dependencies

These jars are **not redistributed here** — they belong to their respective authors.
Drop them in this folder before building; they are `compileOnly`, so the produced mod
jar never contains any of their code. Every patch is independent and stays inert when
its target mod is absent.

| Expected file name | Where to get it | Used for |
|---|---|---|
| `reignitedhud.jar` | [Reignited HUD](https://www.curseforge.com/minecraft/mc-mods/reignited-hud) (Forge 1.20.1) | the HUD row this mod hides during F3 and extends |
| `toughasnails.jar` | [Tough As Nails](https://www.curseforge.com/minecraft/mc-mods/tough-as-nails) (Forge 1.20.1) | thirst + temperature readings |
| `thirst.jar` | [Thirst Was Taken](https://github.com/ghen-git/Thirst-Mod) (Forge 1.20.1) | thirst readings, when Tough As Nails is absent |

Class names are identical between mapping sets and mod-own methods are never remapped,
so compiling against production jars is safe here.

Without these files the build fails at compile time. That is intentional: silently
dropping a patch would ship a jar that quietly does less than it claims.
