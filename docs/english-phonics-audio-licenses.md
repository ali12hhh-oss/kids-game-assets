# English phonics audio licenses

The English Level 1 phonics audio currently bundled at build time is sourced from Wikimedia Commons:

- `phonics_e.ogg`: “Open-mid front unrounded vowel(ɛ).ogg” by Denelson83. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Open-mid_front_unrounded_vowel(%C9%9B).ogg
- `phonics_i.ogg`: “Near-close near-front unrounded vowel.ogg” by Denelson83. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Near-close_near-front_unrounded_vowel.ogg

The Gradle build downloads these files into `app/build/generated/phonics-assets` and packages them as Android assets. The original license terms require attribution and, for adaptations, the applicable ShareAlike terms.

These recordings are phoneme samples, not letter-name recordings. They are used for the English “صوت الحرف” action for E and I.
