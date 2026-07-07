# Theming

Decision record for user-facing theming. Sits beside CONTROL-FOUNDATIONS.md: that doc
defines the system language; theming is user expression WITHIN that language.

## Position (2026-07-03)

- No ES-DE-style layout engine. Screens are compiled Compose wired to the custom input
  model (dual-modality, InputHandler focus, guide bar, dual-screen, TV/handheld scale);
  arbitrary community layouts cannot carry those guarantees. Argosy is a console UI:
  themes mean color, surface, sound, and presentation - not element positioning.
- Themes ship as PACKS: a named, shareable JSON bundle (plus optional assets) of the
  knobs that already exist. Import/export; ship 3-4 first-party presets. Community
  trades files, not code.

## Tiers

1. Theme packs (committed direction): accents, box-art style, background config,
   sounds, density - the existing UserPreferences knobs, bundled and serialized.
   Prerequisite: the Phase 2 primitive port designs `LocalArgosyTheme` so every field
   resolves `user-theme value ?: token default` from day one; retrofitting runtime
   resolution later means touching every consumer twice.
2. Asset packs: per-platform / per-screen backdrops, platform icon packs, font family
   (issue #60), sound packs. Runtime-loaded through the same pack format; the app
   already loads covers, screenshots, and video wallpaper at runtime.
3. Curated layout variants (post-V2 possibility): first-party alternates (hero / grid /
   carousel home), each hand-built with full input wiring, selectable per pack. This is
   the ceiling of layout theming; there is no tier 4.

## Surface backdrops (the "feel" layer)

User-set decorative treatment per ALLOWED surface role: solid color, gradient, or a
GRID-LATTICE pattern. Initial allowlist: dual-screen companion background, settings/menu
screen background. Grow the allowlist deliberately.

The pattern engine is a grid lattice with three independent, togglable layers (2026-07-03):

- CELL layer: a shape inside each grid cell (user-selected shape set, scale, rotation
  jitter).
- VERTEX layer: icons at the grid corners (user-selected icon set).
- EDGE layer: a line effect along cell edges between vertices (solid / dashed / faded).

Each layer has its own toggle, tint, and alpha (all within the surface cap); all layers
share one stored seed. Presets (dots, scanlines, hex, icon grid) are just saved layer
configs.

Cell/vertex content sources: built-in shape sets, icon sets, and USER-DRAWN stamps via
the existing social doodle canvas reused as a stamp editor. The doodle is a PIXEL canvas
(pixels: Map of coords -> DoodleColor, fixed sizes) - stamps store that pixel map
directly (tiny), render as a monochrome MASK (any drawn pixel = on; the pattern tint
owns the color, drawn colors are ignored), and scale nearest-neighbor so they stay
crisp. Retro-native by construction. Stamps ship inside the pack as assets.

Guardrails:

- Pattern renders on BASE/background surfaces only, never raised/elevated panels; the
  elevation ramp and the 0.15-0.18 focus washes must stay legible above it.
- Opacity caps per surface role: content surfaces <= 0.15; wallpaper-role surfaces
  (companion background) may go higher. Caps live in tokens.json
  (`components.surfaceBackdrop`), not literals.
- Pattern is monochrome - one tint (surface tint or accent) at low alpha. Never
  full-color glyphs in chrome.
- Seed-deterministic randomness: the seed is stored; reshuffle only on explicit user
  action, never per recomposition.
- Static; no pattern animation. Parallax/drift is a separate future decision.
- Implementation: render one tile offscreen and repeat via ShaderBrush/ImageShader,
  not per-icon draws each frame.

## Surface tint ramp + bleed (2026-07-03)

The original backdrop intent: users tune the FEEL of chrome without hand-picking hexes.

- User picks a hue; the neutral elevation ramp derives from it (deep navy / purple /
  warm charcoal families). Contrast stays guaranteed because only the hue moves, never
  the lightness steps.
- A "color bleed" slider tunes how strongly the primary/accent tints surfaces (and
  optionally text tint). CLAMPED: a max chroma/saturation cap so chrome can never
  collapse into one hue or wash out legibility. Free-form full palettes stay out of
  scope (the ES-DE contrast lottery).

## Fonts (2026-07-03)

Decision: user-provided fonts ship in the FIRST font-customization release (no
curated-only phase; do not half-ass it). Curated bundled fonts exist as defaults and
examples, not as the ceiling.

- Two slots: DISPLAY (titles, headers, eyebrows) and BODY (UI text). Personality lives
  in display; body warns harder on risky choices.
- Formats: TTF and OTF only, declared in the UI. File picker limited accordingly (SAF;
  extension filter + parse validation on import - reject anything Typeface cannot load,
  with a plain error).
- The Font Customization UI ships guidelines (10-foot legibility, weight coverage
  400/500/600, CJK per-glyph fallback behavior), a live preview rendering real UI
  samples at title/body sizes and weights, and a one-tap revert to Default.
- Row-height tolerance is tight post-density (40dp rows): preview must include a
  focused menu row so clipping/mis-centering is visible before the user commits.

## Sounds (2026-07-03)

- Sound packs (nav tick / confirm / back / launch samples) ride the pack format.
- PREREQUISITE: sound manager rework. Rapid successive replays are currently broken
  (fast d-pad nav); move to pre-loaded, polyphonic playback (SoundPool-style streams
  with per-sound stream limits) BEFORE packs land, and improve the base sample set as
  part of that rework.

## Candidate knobs (evaluated 2026-07-03, unscheduled)

- Focus treatment intensity: promote focusGlowAlpha / halo tint to pack fields.
- Placeholder-cover theming: no-art cards inherit pack tint + pattern (reuses the
  backdrop engine).
- Issue #60 fields: glow intensity steps, border palette modes
  (complementary/triadic/analogous), per-view grid spacing.

## Settings IA restructure (2026-07-07)

Interface was one flat section holding four concerns; the theming roadmap does not fit
in it. Decision: split identity from activity, and embrace sub-menus wherever a concern
has depth.

**Theme (new top-level section)** - the identity layer, future home of packs:
- Mode (dark/light/auto); colors: accent, secondary, tint ramp + bleed sliders inline
- Surface Backdrop -> subsection (phase 1 presets; phase 2 builder + tile creator)
- Fonts -> subsection (user TTF/OTF import, validated, preview + revert)
- Sounds -> subsection (UI sounds toggle/volume/per-sound customize; sound sets later)
- Box Art -> moves here (visual identity)
- Packs (future): Save-as-pack / Apply-pack rows at section top when tiers land

**Interface (remaining, compartmentalized by activity):**
- Layout: grid density, UI scale, Home Screen behavior
- Screen safety: dimmer trio
- Ambience: BGM group (content you listen to, not identity - stays apart from theme sounds)
- Displays: dual-screen, display roles, Ambient LED (hardware behavior; palette-fed but not identity)

Mechanics: nav-row-to-subsection pattern already exists (Box Art, Ambient LED); Theme is
a new SettingsSection entry plus row redistribution, not new machinery.

## Tile creator (2026-07-07)

The backdrop tile/doodle drawing tool paints in GREYSCALE ONLY, exactly 5 steps from
black to white (black, dark, mid, light, white). Tiles are luminance masks: the theme
tint ramp colors them at render time, so every tile works under every accent and both
modes by construction. No per-tile color data is ever stored.
