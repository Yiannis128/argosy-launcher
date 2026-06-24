# Control Foundations

The base design language for Argosy's menu/control system. Built bottom-up: controls and
primitives are defined here first; screens are composed from instances of them. Lives ahead of
any screen redesign.

Source of truth for VISUAL intent is the Penpot `Foundations - Controls` page; this doc is the
decision record and the annotation text for that page. Token VALUES live in `tokens.json`.

## Direction

- Modern, clean, real game console. Not Material, not maximal-cinematic.
- Two locked anchors, validated on-device: Home and Game Detail. Everything conforms to their
  language; neither is up for redesign.
- Bottom-up: lock controls/primitives, then screens fall out as instances + overrides.

## Color usage

- Chrome is neutral monochrome built as a deep base + elevation ramp, never a flat fill. Dark
  mode anchors to Home's rendered near-black family (`#050507` base -> `#13141a` surface ->
  `#1c1e26` raised -> `#262834` elevated), NOT the stale Material `#121212`. Light mode mirrors
  the same principle inverted (near-white base + subtle grey elevation). Color lives in accents
  and per-game art, never in chrome.
- Accents carry per-mode variants (cyan `#00ACC1` dark / `#007C91` light, etc.) so contrast
  holds both ways; the art-tint bloom is lighter-touch in light mode to avoid washout.
- `accentPrimary` (default cyan `#00ACC1`) + `accentSecondary` (user-pick, default
  complementary). Both user-themeable; defaults are just defaults.
- The existing scheme primary/secondary/tertiary (cyan / teal / green) is already an analogous
  near-triad - formalize it, do not reinvent.
- Supporting-tone derivation, pick per accent: single-hue mono ramp (vary saturation/brightness)
  OR analogous hue offsets of 15-30 degrees.
- Per-game cover-art tint (computed from cover palette[0]): focus halo, CTA shadow, wallpaper
  bloom, progress fill. The game brings the color; the system stays neutral.

## Radius roles

Sharp surfaces, soft controls - the inverse of Material's over-rounded panels.

- `radiusPanel` ~6dp: modals, drawers, sheets, side rails, panels (today `GlassPanel` uses 16dp - too round).
- `radiusControl` ~12dp: buttons, rows, chips.
- `radiusPill` 999: toggles, capsule CTAs.

## States

One canonical set, reused by every interactive primitive: `neutral / focused / active(selected) /
disabled`.

Focus is the primary visual event, but its TREATMENT is per component class, composed from five
indicators (already implemented in `primitives/Focus.kt` as `FocusIndicators`, motion-tier-aware springs):

- `lift` - translateY -6dp + scale 1.04
- `halo` - radial accent glow behind (0.55 -> 0.14 -> transparent)
- `fill` - accent background wash, alpha 0.18 focused / 0.108 selected-not-focused
- `stripe` - 4dp accent left-stripe
- `ring` - 2dp accent border

| Class | Focus treatment |
|---|---|
| Cover tile (Home / Library) | halo + lift |
| Nav row / tab row | stripe + fill |
| Menu item / list row | fill |
| Button / pill | fill |

- HARD RULE: focus changes color / fill / glow / border only - NEVER position or scale. No
  "elevate on focus" (the AI-default tell). The element stays put; only its surface treatment
  changes. `fill`, `halo`, `stripe`, `ring` are all in-place; `lift` (translate + scale) is
  disallowed on controls, rows, buttons, chips - anything in a menu.
- The cover tile's `lift` is the only open exception (the one place Home moves on focus). Default
  is to keep it as the single deliberate signature; it can go static (halo only) if you want zero
  movement anywhere. See open items.
- Focus tint resolves: explicit -> user focus-halo override -> user primary -> per-game art
  palette[0] -> theme focus accent. Focus inherits the game's color when nothing overrides.
- `active/selected` = the persistent fill (0.108) / stripe marking the current value or page, shown
  even when focus is elsewhere. Distinct from `focused`.
- `disabled` = reduced content alpha + no focus treatment, skipped in d-pad traversal. (Not yet in
  `Focus.kt`; define as a token.)
- Focus signals WHERE you are; the always-visible inline affordance signals WHAT the control does.

## Menu item

- Anatomy: title + optional sub-text + trailing control slot.
- Inline affordances are ALWAYS visible, on every row, not only the focused one - a menu must
  self-document at a glance. Focus adds the fill on top (no lift - see States).

### List scrolling

- Centered cursor: in a scrollable menu list the focused item stays vertically CENTERED and the
  list scrolls beneath a fixed cursor, so the eye never tracks the highlight down the screen.
  Standard for every overflowing menu list.
- Engages only when content exceeds the viewport. A list that fits stays static (no scroll); focus
  just moves between rows.
- End clamping includes NON-FOCUSABLE content. The scroll range is bounded by the content edges -
  including leading/trailing non-focusable sections (headers, descriptions, disabled rows) - not by
  the focusable items. Focusing the first or last FOCUSABLE entry shifts the list to bring those
  end sections FULLY into view (the cursor can never land on them to reveal them by centering).
- Consequence: the cursor is centered in the interior and rides toward the boundary at the head/
  tail (it cannot stay centered with nothing left to scroll) while the end content stays fully
  visible. Centered-interior + clamp-at-ends are one behavior.
- Section headers obey the SAME rule, not a separate one - a header is non-focusable content that
  belongs to its section. Focusing the FIRST focusable row of a section guarantees its header is
  fully in view above; focusing the LAST row before a trailing non-focusable block reveals that
  block. The list-end behavior is just this rule at the outermost sections.
- Section-navigation chrome is aspect-ratio adaptive. WIDE screens put sections in a LEFT RAIL
  (active highlighted) - the rail is the orientation, so no sticky header. On 4:3 / 1:1 devices
  there is no room for the rail, so sections are inline AND the active section header STICKS to the
  top edge while scrolling within it. The centered-cursor + reveal model is identical in both.

## Control modes

Each mode has a deliberately distinct silhouette - no two readable as the same thing.

| Mode | Visual | A / Confirm | Left / Right | Touch |
|---|---|---|---|---|
| Nav row (drills in) | row + chevron `>` | open submenu | - | tap = open |
| Check | checkbox + mark | toggle | - | tap = toggle |
| Toggle | track + knob | flip | left=off / right=on | tap or drag |
| Enum | filled triangles `< value >` | open full list (modal) | cycle one option | tap triangle = cycle, tap value = open list |
| Stepper | `- value +` | - | step down / up | tap -/+ |
| Slider | track-height knob + filled track | - | step down / up | drag / tap track |

- Enum inline cue = small FILLED TRIANGLES that rhyme with the d-pad glyph language, never `<>`
  chevrons (those read as math, not motion). Tint accent on focus; pressed direction flashes.
- Enum is ONE control, two access paths: L/R (or tapping a triangle) is the fast cycle;
  A/Confirm (or tapping the value) opens a modal with all options as a scrollable list. No
  inline-vs-picker split, and A stays "open," never "adjust."
- Stepper stays `- value +` (quantity), distinct from enum (lateral option movement).
- Slider knob is flush with the track (no Material lollipop). Knob distinguishes it from a
  progress bar; continuous fill distinguishes it from a stepper.

## Interaction principles

- A/Confirm means enter / commit / toggle - NEVER adjust. Sliders, steppers, and inline enums
  adjust on Left/Right only, so A's meaning stays constant everywhere.
- The input axis is shown by the control itself (triangles, knob, plus/minus), not by words.

## Footer philosophy: the control is the guide

Reduce footer reliance across the board. The inline affordance carries the interaction; the
footer is not a crutch.

- One app-root bottom bar (singleton, push/pop stack). No modal or drawer owns its own footer.
- With self-documenting controls the bar is usually quiet or empty. When empty it collapses
  smoothly by sliding below the screen edge + fading (MotionTokens) - never blanks, never pops.
- Layout contract for the rise-from-below reveal: hint entries are grounded to the TOP of the
  bar container with fixed top padding and FIXED width (no adaptive/hug sizing). The bar then
  translates up from below the screen as a stable unit - no internal reflow, no vertical drift.
  Collapse reverses. (Compose: fixed-width + top-aligned `FooterHintItem`, bar animates offset.)
- Reserved for what is NOT attached to a focused control: screen-global verbs (filter, search,
  compose), shoulder paging between sections, genuinely non-obvious bound buttons.
- Back / A / B / d-pad are universal and never hinted.

## Buttons

Two families already exist as primitives (`PillButton`, `RowButton`); formalize their roles.

- **Pill** (`PillButton`, `radiusPill`) - the action CTA, two tiers:
  - `primary` - accent-filled, 12dp accent-tinted shadow, focus = halo. Accent defaults to theme
    focus accent but takes an override (e.g. Continue Playing in the game's art tint).
  - `secondary` - translucent raised surface + hairline border, focus = fill.
  - Padding 22h / 14v; `pressScale` press feedback (touch + gamepad).
- **Row** (`RowButton`, `radiusMd` 8dp, full-width) - the list affordance. Plain = list row
  (focus fill); accented = nav/drill row (focus stripe + fill). This IS the menu-item base -
  control modes plug into its trailing slot.
- Anatomy: title + optional subtitle + optional icon (left or right).
- States: `neutral / focused / active / disabled`, plus advanced (progress/working, destructive).
- The "progress/working" button still needs design - show in-progress work without becoming
  confusable with a slider or progress bar (same distinct-silhouette rule as the controls).

## Warning / confirm modal

Today these are raw Material `AlertDialog`s (8 of them) - foreign focus model, over-rounded, and
the destructive button uses Material dark `colorScheme.error`, a pale red that is a hard read on
near-black. Replace with an Argosy modal that splits the two signals Material conflates:

- SYMMETRIC base states: both actions rest at the SAME weight (both fill-less / text), so focus is
  one consistent signal that reads identically on either. The current bug is the dramatic base
  asymmetry - flat-text Cancel vs solid-fill Delete - which makes the focus cursor illegible on top
  of two different baselines.
- FOCUS is the standard fill, like everywhere else, applied identically to whichever button is
  focused (neutral/cyan on Cancel, red on Delete). Default focus = the SAFE action (Cancel), never
  the destructive one.
- DESTRUCTIVENESS is a legible accent on the dangerous action - red label/icon, neutral background
  at rest - using a saturated token red (`color.domain.difficulty #E53935` / `semantic.warning`),
  never Material's pale error fill.
- When the destructive action is focused, its fill is red-tinted so the dangerous moment is
  unmistakable. Color says WHICH action is dangerous; fill says WHICH is focused - never the same
  channel.
- Built on `ModalScaffold` + `radiusPanel` (sharp), our buttons, no modal-owned footer.

## Open items

- Token reconciliation: dark ramp canonical = Home's rendered near-black family; sync Penpot
  color tokens (stale `#121212`) and `tokens.json` to it.
- Cover tile focus motion: keep `lift` (translate + scale) as the single deliberate signature on
  Home/Library tiles, or drop it for halo-only so NOTHING moves on focus anywhere.
- `pressScale` (momentary scale-down on actuation, separate from focus): keep as tactile press
  feedback, or remove it too under the no-movement stance.
- Migrate 8 Material `AlertDialog`s (4 collections dialogs, settings, downloads, file browser) to
  the Argosy warning/confirm modal pattern.
- Light-mode ramp exists but is flat: `surfaceElevated` collapses to `surfaceBase` (`SurfaceLight`),
  no real elevation steps. Build it to mirror dark (distinct near-white base / raised / elevated,
  light accent variants, lighter bloom).
