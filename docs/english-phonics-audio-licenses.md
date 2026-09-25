# English phonics audio licenses

The English Level 1 phonics audio currently bundled at build time is sourced from Wikimedia Commons:

- `phonics_e.ogg`: “Open-mid front unrounded vowel(ɛ).ogg” by Denelson83. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Open-mid_front_unrounded_vowel(%C9%9B).ogg
- `phonics_i.ogg`: “Near-close near-front unrounded vowel.ogg” by Denelson83. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Near-close_near-front_unrounded_vowel.ogg

The Gradle build downloads these files into `app/build/generated/phonics-assets` and packages them as Android assets. The original license terms require attribution and, for adaptations, the applicable ShareAlike terms.

These recordings are phoneme samples, not letter-name recordings. They are used for the English “صوت الحرف” action for E and I.


Additional phonics recordings:
- `phonics_q.ogg`: “Labialized voiceless velar plosive.ogg”, representing [kʷ], used as the Q phonics onset. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Labialized_voiceless_velar_plosive.ogg
- `phonics_s.ogg`: “Voiceless alveolar sibilant.ogg”, [s]. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Voiceless_alveolar_sibilant.ogg
- `phonics_z.ogg`: “Voiced alveolar sibilant.ogg”, [z]. License: CC BY-SA 3.0. Source: https://commons.wikimedia.org/wiki/File:Voiced_alveolar_sibilant.ogg

Q is normally taught as /kw/ in English; the selected recording is the closest directly recorded labialized /k/ sample found in the licensed phonetic sources searched, so it is documented as [kʷ] rather than incorrectly claiming it is a full /kw/ recording.
