<div align="center">
<h1>🧭 F3 Reignite Journey</h1>
<h3>A readable debug screen — and a serious FPS fix for Reignited HUD.</h3>
<p><b>Four tiny patches. Each one activates only if the mod it targets is installed.</b></p>
<p>
<img src="https://img.shields.io/badge/Minecraft-1.20.1-brightgreen?style=for-the-badge" alt="Minecraft 1.20.1">
<img src="https://img.shields.io/badge/Loader-Forge-e04e14?style=for-the-badge" alt="Forge">
<img src="https://img.shields.io/badge/Side-Client%20only-blueviolet?style=for-the-badge" alt="Client only">
</p>
<p>
<img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge" alt="Java 17">
<img src="https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge" alt="MIT">
<img src="https://img.shields.io/badge/Dependencies-none-success?style=for-the-badge" alt="No dependencies">
</p>
<p><i>~5 KB. No config. Nothing to set up.</i></p>
</div>

---

<br>

## ❓ The problem

<br>

Press **F3** on a modded client and the debug screen is often unreadable.

HUD mods keep drawing on top of it — and because Minecraft renders item icons at a very high z-offset, they punch straight through the text backgrounds of BetterF3 or Enhanced F3. Coordinates behind a hunger bar. TPS behind a minimap.

<br>

Neither side offers a fix: the HUD mods have no "hide while F3 is open" option, and the debug-screen mods have no offset setting.

<br>

There is a second, quieter problem.

**Reignited HUD** redraws its *entire* HUD on **every overlay event** — fifteen to twenty times per frame instead of once. It is a known upstream issue, and it costs a lot of frames.

<br>

---

<br>

## ✨ What this mod does

<br>

### ⚡ Fixes the Reignited HUD frame cost

<br>

The HUD is drawn **once per frame** instead of once per overlay event.

<br>

> On a 280-mod pack (Threadripper 3960X · RTX 3070 Ti · 3440×1440, no shaders), the framerate went back from *heavily degraded* to **180–200 FPS** — the same figure measured with Reignited HUD uninstalled.
>
> Your numbers will differ. The mechanism will not: fifteen to twenty redraws become one.

<br>

*No files are modified. Reignited HUD stays exactly as its author shipped it — the patch lives at runtime, in this mod.*

<br>
<br>

---

<br>

### 🧹 Clears the screen while F3 is open

<br>

While the debug screen is up, these are hidden — and come back the moment you close it:

<br>

| Hidden during F3 | Why |
|:--|:--|
| **Reignited HUD** | Its item icons render above the debug text backgrounds |
| **Vanilla hotbar** | Same overlap, bottom of the screen |
| **JourneyMap minimap** | Sits exactly where the right-hand debug column goes |

<br>

The result is a debug screen with nothing in front of it, at any resolution — including ultrawide, where the columns are widest.

<br>
<br>

---

<br>

### 🧩 Contextual by design

<br>

Each patch targets a specific mod, and **does nothing at all if that mod is absent**.

Install this alongside Reignited HUD only, JourneyMap only, both, or neither: the patches that have no target simply never activate. No crash, no warning, no hard dependency.

<br>

The trigger is the **vanilla F3 flag**, not any particular debug mod — so it works the same whether you use:

<br>

- 🟩 &nbsp; the **vanilla** debug screen

- 🟦 &nbsp; **BetterF3**

- 🟪 &nbsp; **Enhanced F3**

<br>

`F3+F4`, `F3+T` and the other vanilla combos keep working untouched.

<br>
<br>

---

<br>

## 📦 Install

<br>

Drop the jar in `mods`. That is all.

**Client-side only** — servers do not need it, and it never affects gameplay, only what is drawn on your screen.

<br>
<br>

---

<br>

## 🔧 Technical notes

### *For developers — skip this if you just want the mod*

<br>

### The frame-cost fix

<br>

Reignited HUD subscribes to `RenderGuiOverlayEvent.Post` and redraws its full HUD **without filtering which overlay fired the event**. With a typical overlay stack that is 15–20 full HUD passes per frame.

A `@Pseudo` mixin on its `GuiWidget#renderOverlay` lets only the **hotbar** `Post` through and cancels the rest: one render per frame, identical output.

<br>
<br>

### Hiding, and why z-order made it necessary

<br>

Item icons are drawn by the item renderer at **z + 200**, far above the ~0 plane where debug-screen mods paint their text backgrounds. No amount of ordering on their side can win; the HUD has to be suppressed instead.

<br>

- **Reignited HUD** and **JourneyMap** → `@Pseudo` mixins, `remap = false`, `require = 0`, targeting `journeymap.client.event.handlers.HudOverlayHandler#onRenderOverlay` and Reignited's widget renderer. Absent target = mixin silently inert.
- **Vanilla hotbar** → a plain Forge event handler cancelling `RenderGuiOverlayEvent.Pre` for `VanillaGuiOverlay.HOTBAR` at `HIGHEST` priority.

<br>

Everything keys off `Minecraft.getInstance().options.renderDebug`, the vanilla flag — which is why any F3 replacement that toggles it is supported for free.

<br>
<br>

### Cross-mod mixin pattern

<br>

Targeting another mod's classes is done by string — `@Pseudo @Mixin(targets = "full.class.Name", remap = false)` with `require = 0` and `"required": false` in the mixin config — so a missing target is a no-op rather than a startup failure. That is what makes every patch here optional at runtime with no dependency declarations.

<br>

**Built against** Forge 47.3.0, Java 17, official mappings. No `@Shadow` anywhere.

<br>

Reignited HUD, Tough As Nails and Thirst Was Taken resolve from the Modrinth Maven. Clone and run `gradlew build`; there is nothing to download by hand. All three are `compileOnly` and none is required to *run* the mod. Reignited HUD is pinned by version **id** rather than number, because thirteen of its published files share the number `1.1.0`.

<br>
<br>

---

<div align="center">
<p><b>MIT</b> &nbsp;·&nbsp; by <b>Hell_Kaiser</b></p>
<p><i>Issues and suggestions welcome.</i></p>
</div>
