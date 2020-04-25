# BoneTown
An alternate entity rendering pipeline for Minecraft Forge 1.15.2.

## Features

* Skeletal Animations
* Supports standard 3d modeler output instead of the boxy MC format.
* On GPU Animation and Vertex calculations
* Modern GLSL Shader pipeline (no fixed function)
* Modern layer-based animation blending pipeline
* Treat animations as a resource (mods can introduce animations for 
other mods entities)
* Extensible Network Protocol for automatically syncing animation state


## Supported Formats

Internally we use our own BoneTown Model Format (.bonemf) that is designed
for simplicity and to expose exactly what we need for rendering in Minecraft.
Converters from popular model formats to .bonemf are necessary at the moment
we support the following model formats:

* [FBX](https://github.com/ChaosBuffalo/FBXToBoneMF)


## Why Use Bonetown?

If you are a modder wanting to develop entities using industry standard software 
and provide more detailed animations and models without a performance cost, you should
consider depending on BoneTown.

The basic Minecraft rendering pipeline performs all animation and rendering
calculations on the cpu and submits them every frame. This works fine for most
of the low vertex count default models, but many Minecraft mods introduce significantly
more detailed models. This approach to rendering breaks down both with complexity
of model and complexity of animation. 

BoneTown performs nearly all calculations directly on the GPU. Model geometry is only
uploaded to the graphics card once instead of every frame and following that only the
information that changes transfers every frame: lighting information, final positions, and
animation bone data.

In addition to this, animation data can be re-used and shared between mods, opening up
the ability for modders to mod each other's modded entities and introduce new behaviors
significantly more efficiently than in the current Java MC environment.




