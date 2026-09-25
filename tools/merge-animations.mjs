// Builds a single character GLB that contains only the educational / encouraging
// animation clips, so the app can play them on the mannequin.
// Usage: node tools/merge-animations.mjs <output.glb>
import { NodeIO } from '@gltf-transform/core';
import fs from 'fs';
import path from 'path';

const out = process.argv[2];
if (!out) {
  console.error('Usage: node tools/merge-animations.mjs <output.glb>');
  process.exit(1);
}

const charPath = 'assets/characters/KayKit/Mannequin Character/characters/Mannequin_Medium.glb';
const animDir = 'assets/characters/KayKit/Animations/gltf/Rig_Medium';

// The ORDER below defines the animation index used by the app:
// 0 Idle_A, 1 Idle_B, 2 Interact, 3 PickUp, 4 Use_Item, 5 Spawn_Ground,
// 6 Waving, 7 Cheering, 8 Sit_Floor_Idle, 9 Jump_Full_Short, 10 Walking_A, 11 Running_A
const WANTED = [
  ['Rig_Medium_General.glb', ['Idle_A', 'Idle_B', 'Interact', 'PickUp', 'Use_Item', 'Spawn_Ground']],
  ['Rig_Medium_Simulation.glb', ['Waving', 'Cheering', 'Sit_Floor_Idle']],
  ['Rig_Medium_MovementBasic.glb', ['Jump_Full_Short', 'Walking_A', 'Running_A']],
];

const io = new NodeIO();
const charDoc = await io.read(charPath);
const root = charDoc.getRoot();

// Remove any animation already embedded so indices are predictable.
root.listAnimations().forEach((a) => a.dispose());

const nodeByName = new Map(root.listNodes().map((n) => [n.getName(), n]));
const buffer = root.listBuffers()[0];
let added = 0;

for (const [file, names] of WANTED) {
  const src = await io.read(path.join(animDir, file));
  const byName = new Map(src.getRoot().listAnimations().map((a) => [a.getName(), a]));

  for (const name of names) {
    const anim = byName.get(name);
    if (!anim) {
      console.warn(`MISSING clip ${name} in ${file}`);
      continue;
    }
    const dst = charDoc.createAnimation(name);
    let channels = 0;

    for (const ch of anim.listChannels()) {
      const targetName = ch.getTargetNode() ? ch.getTargetNode().getName() : null;
      const target = targetName ? nodeByName.get(targetName) : null;
      if (!target) continue;
      const s = ch.getSampler();

      const input = charDoc
        .createAccessor()
        .setType(s.getInput().getType())
        .setArray(s.getInput().getArray().slice())
        .setBuffer(buffer);
      const output = charDoc
        .createAccessor()
        .setType(s.getOutput().getType())
        .setArray(s.getOutput().getArray().slice())
        .setBuffer(buffer);

      const sampler = charDoc
        .createAnimationSampler()
        .setInput(input)
        .setOutput(output)
        .setInterpolation(s.getInterpolation());
      const channel = charDoc
        .createAnimationChannel()
        .setTargetNode(target)
        .setTargetPath(ch.getTargetPath())
        .setSampler(sampler);

      dst.addSampler(sampler);
      dst.addChannel(channel);
      channels++;
    }
    console.log(`${name}: ${channels} channels`);
    added++;
  }
}

fs.mkdirSync(path.dirname(out), { recursive: true });
await io.write(out, charDoc);
console.log(`Wrote ${out} with ${added} animations`);
