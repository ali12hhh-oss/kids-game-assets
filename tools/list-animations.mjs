// Lists every animation clip inside the Rig_Medium GLB files and writes docs/ANIMATIONS.md
import { NodeIO } from '@gltf-transform/core';
import fs from 'fs';
import path from 'path';

const dir = 'assets/characters/KayKit/Animations/gltf/Rig_Medium';
const io = new NodeIO();

let body = '';
let total = 0;

for (const file of fs.readdirSync(dir).filter((f) => f.endsWith('.glb')).sort()) {
  const doc = await io.read(path.join(dir, file));
  const names = doc.getRoot().listAnimations().map((a) => a.getName());
  total += names.length;
  body += `## ${file} (${names.length})\n\n`;
  body += names.map((n) => `- ${n}`).join('\n') + '\n\n';
}

const md = `# Rig_Medium animations\n\nTotal clips: ${total}\n\n${body}`;
fs.mkdirSync('docs', { recursive: true });
fs.writeFileSync('docs/ANIMATIONS.md', md);
console.log(`Wrote docs/ANIMATIONS.md with ${total} clips`);
