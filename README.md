# BoneTown

[Curseforge Page](https://www.curseforge.com/minecraft/mc-mods/bonetown)

[Discord](https://discord.gg/mCXnxJ4)

An alternate entity rendering pipeline for Minecraft Forge 1.15.2.

## Features

* Skeletal Animations
* Supports standard 3d modeler output instead of the boxy MC format.
* On GPU Animation and Vertex calculations
* Modern GLSL Shader pipeline (no fixed function)
* Modern layer-based animation blending pipeline
* Treat animations as a resource (mods can introduce animations for 
other mod's entities)
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

## Downsides of this Approach

There are some downsides to the approach we are taking:

### No Custom Forge Armors for these entities

While our biped model supports the default vanilla minecraft biped armor models, it is
not possible to similarly support custom forge armor models. Our biped introduces an additional
joint at the elbow and knees, and really the armor models also need to be associated with skeletal
data to animate them using the approach we use. Our biped model is a recreation of the vanilla biped,
BoneTown is not capable of using the original Minecraft assets, new assets need to be authored to use
with this system.

On the other hand, in the BoneTown system it is possible to register new models for 
minecraft armors on a per-model, per-material basis, and to register armor models 
for non-biped characters provided they use the same textures. This opens
up entirely new possibilities for for custom armor models and armor integration 
in other entities.

### No Optifine

BoneTown introduces a new rendering flow that only partially piggybacks off existing
Minecraft rendering. I have no idea what that would mean for Optifine, and it is 
possible they could be made to work together, but I'm not really considering it
a priority. Optifine is also closed source so its very hard to look into such
problems.

## The Approach

There are 3 key parts to the BoneTown library that differ significantly from vanilla
Minecraft:

* The model format that stores data on disk
* The rendering code that submits the data to GPU
* The representation of animations on server and client and their syncing

### BoneTown Model Format

BoneTown loads models and animations from .bonemf files, which are a cbored representation
of geometry and animation data. These files have a format
inspired by the Autodesk FBX format, but significantly reduced in scope to fit the
needs of Minecraft. No material information is preserved for the geometry, instead
that information is controlled by the EntityRenderer as in vanilla.  Typically right now
you would convert your FBX files to .bonemf using the FBXToBoneMF command line utility, 
you can find a windows build of this software [here](https://github.com/ChaosBuffalo/FBXToBoneMF/releases)
or you can get the [source](https://github.com/ChaosBuffalo/FBXToBoneMF) and try to compile for other
platforms. Please consider contributing any platform upgrades to master!

#### Technical Details

* A max of 100 joints (bones) in the model.
* 4 joint (bone) weights per vertex.
* Only 1 skeleton in file

At the moment the software has only been tested with models produced with Maya LT,
as that is what I use. However, if you use 3dsmax or Blender and you experience
issues please contact me and we'll work them out.

### Rendering

BoneTown introduces a new rendering pipeline that submits the bulk of the model
data only a single time, and then uploads the minimal changes necessary
to draw that model at a specific place on screen. This is in contrast to
Minecraft's default entity rendering pipeline which transforms the models
into world space and applies animations on the cpu and then sends the final
geometry to the GPU every frame.

Data is submitted to the programmable pipeline instead of the legacy Fixed
Function pipeline. Model data is submitted separately as arrays of primitives
instead of in an interleaved struct format. Lighting, Overlay, 
World Space Transform, and Animation data that is normally copied out to
each vertex is instead submitted a single time as uniform values.

The built in shaders included in BoneTown are designed to emulate the legacy
OpenGL Fixed Function pipeline state that Minecraft uses almost exactly. However,
the usage of shaders in BoneTown opens up exciting possibilities for content
authors who are interested in more advanced rendering for their entities.

### Animations

Unlike vanilla, animation data is held on an Entity in BoneTown. All entities wanting to
use BoneTown's renderers should implement IBTAnimatedEntity. As part of this
your entity will need to hold an AnimationComponent. The AnimationComponent is responsible
for determining the actual position of the entities joints (bones) and syncing that data
with the relevant clients. This also enables server-side decisions such as spawning a projectile
at the position of a specific bone.

All adjustments of AnimationComponent state should be done using AnimationMessages through the
AnimationComponent::updateState function. Any changes made through this interface will be
automatically recorded, batched as one packet, and sent to the relevant clients. 

For an example of the usage of this system see the implementation of TestZombieEntity.

## Programming Guide

The best way to learn about these implementations is to take a look at the 
`com.chaosbuffalo.bonetown.init` package to see how the new assets are registered,
`com.chaosbuffalo.bonetown.client.render` package to see the equivalents of vanilla classes like
RenderLayers and Renderers, and finally the `com.chaosbuffalo.entity.TestZombieEntity` is an
example of everything coming together in a concrete implementation of a biped.

### Directory Structure

Like vanilla, BoneTown expects models (and animations to be located in specific folders),
the directory structure should look like:

Animations location: `assets/modid/bonetown/animations`

Models location: `assets/modid/bonetown/models`

So when you load a model with ResourceLocation("yourmod", "test_cube"), BoneTown will
look for a file called `test_cube.bonemf` at location:

`assets/yourmod/bonetown/models/test_cube.bonemf`

### Registries

There are 4 new registries introduced to manage the additional assets BoneTown supports:

* Materials
* Models
* Additional Animations
* Armor Models

#### Materials

Materials are at base the shader files that will control how your entities are rendered. The
BTMaterialEntry is the base class for the registry object which will eventually be loaded as an
IBTMaterial implementor. There are 2 materials provided by default: BTMaterial, suitable for 
static models, and AnimatedMaterial, suitable for models with skeletal animation.

#### Models

Similar to Materials, Models are made up of either static or animated data. The BTModel is the
base class for the registry and suitable for static data. There is also a BTAnimatedModel for the
animated data.

#### Additional Animations

Animations can be loaded separate from the models themselves. Typically you should place one
animation in each file, however if you do choose to use multiple the animations will be named
like `yourmodid:animation_name_N` where N is the index of that animation in the array. This way
it is possible for other mods to introduce additional animations for existing models.

#### Armor Models

Armor models can be introduced on a per-model basis, and overrides can be provided for specific
armor materials. If you are producing an entity that you want to be able to wear vanilla armor
you should create your model so that it uses the UVs for the default armor texture in Minecraft.
This should typically be the default armor added to your model. You can then introduce additional
models on a per-armor-material basis if your armor does not use the vanilla UV mapping or you want
additional geometry. Armor models should be rigged to the same skeleton as the model they are
intended for.


### The Animation Component

AnimationComponent serves as the holder of animation state for your entity in BoneTown. It is
responsible for assembling the final animation for a frame and syncing state changes over the
network. AnimationComponent holds a stack of AnimationStates which hold the actual details
about assembling the animation for the current frame. An AnimationState can be made up of
multiple layers, these layers could pull data from animation files, from blends of those assets,
or be procedurally generated such as the equivalent of Minecraft's vanilla head tracking.

Let's take a look at emulating the basic zombie walk, head look, and zombie arm behavior.
The default AnimationState for our TestZombieEntity is assembled like so:

```java
    protected void setupAnimationComponent() {
        AnimationState<TestZombieEntity> defaultState = new AnimationState<>("default", this);
        HeadTrackingLayer<TestZombieEntity> headTrackingLayer = new HeadTrackingLayer<>("head", this,
                "bn_head");
        LocomotionLayer<TestZombieEntity> locomotionLayer = new LocomotionLayer<>("locomotion",
                IDLE_ANIM, RUN_ANIM,
                this, true);
        SubTreePoseLayer<TestZombieEntity> armsLayer = new SubTreePoseLayer<>("arms",
                ZOMBIE_ARMS_ANIM, this, true, "bn_chest");
        defaultState.addLayer(locomotionLayer);
        defaultState.addLayer(armsLayer);
        defaultState.addLayer(headTrackingLayer);
        animationComponent.addAnimationState(defaultState);
        animationComponent.pushState("default");
    }
```

Layers are handled in the order they are added to your state, so when a frame is computed first
we apply the "locomotion" layer which is a full-body animation blending between
an idle pose and a running pose based on the speed of the entity. This results in a very vanilla
like outcome where the legs and arms appear to swing wider the faster the entity is moving.
The variables here like IDLE_ANIM or RUN_ANIM are simply static resource location names refering
to animations we have loaded in our animation registry. This is very similar to how
texture references work for regular entity rendering in vanilla.

#### Full Body Layers

The actual work of the locomotion layer looks like this:

```java
    void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BakedAnimation baseAnimation = getAnimation(BASE_SLOT);
        BakedAnimation blendAnimation = getAnimation(SECOND_SLOT);
        if (baseAnimation != null && blendAnimation != null){
            InterpolationFramesReturn ret = baseAnimation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop(), partialTicks);
            anim1Blend.simpleBlend(ret.current, ret.next, ret.partialTick);
            InterpolationFramesReturn ret2 = blendAnimation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop(), partialTicks);
            anim2Blend.simpleBlend(ret2.current, ret2.next, ret2.partialTick);
            finalBlend.simpleBlend(anim1Blend.getPose(), anim2Blend.getPose(), getBlendAmount());
            outPose.copyPose(finalBlend.getPose());
        }
    }
```

First we get the actual BakedAnimation objects and use the getInterpolationFrames function
to retrieve the current and next frame in our animation. We then use `WeightedAnimationBlend::simpleBlend`
to produce an in-between pose based on the current `partialTicks`. We then once again
blend those 2 blends together by the value of `entity.limbSwingAmount`, very similar to how walk 
cycles are calculated in vanilla Minecraft. Finally, we copy the blended pose to the `outPose` object 
that will be passed on to subsequent layers.

#### Subtree Layers

Secondly, we apply the "arms" layer. This only affects our skeleton from the "bn_chest" bone and up.
That way we can use whatever the movement state is from locomotion and the desired arm motion at the same
time instead of having to produce a walking and standing version of every arm animation we want.

```java
  void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BakedAnimation animation = getAnimation(BASE_SLOT);
        if (animation != null){
            InterpolationFramesReturn ret = animation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop(), partialTicks);
            localBlend.setFrames(ret);
            IPose localPose = localBlend.getPose();
            for (int id : boneIds){
                int parentId = skeleton.getBoneIdParentId(id);
                Matrix4d parentGlobalLoc;
                if (parentId != -1){
                    parentGlobalLoc = outPose.getJointMatrix(parentId);
                } else {
                    parentGlobalLoc = new Matrix4d();
                }
                outPose.setJointMatrix(id, parentGlobalLoc);
                outPose.getJointMatrix(id).mulAffine(localPose.getJointMatrix(id));
            }
        }
    }
```

A local blend functions a little differently from the `WeightedAnimationBlend` used in the 
"locomotion" layer. The largest difference is rather than lerping between the global joint positions
it lerps between the local joint positions. This is so that we can then take those results and
apply them to an existing global location from our previous layers and get the arms animation added
onto the existing animation frame. We begin by getting the interpolated frames just as before, but then we loop through 
only bones descending from and including the"bn_chest" bone we passed in, 
applying the local joint matrix to the parent's global joint matrix to get the new global matrix for that joint.

#### Procedural Layers

Sometimes you don't want to pull animation data from a file at all, such as in Minecraft's head tracking. 
All entities essentially hold state about the pitch and yaw of their head. We want to be able to use our 
system to do behavior like this as well. Luckily, it is actually quite easy to do so in a skeletal animation
system:

```java
public void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BoneMFSkeleton skeleton = getEntity().getSkeleton();
        if (skeleton == null){
            return;
        }
        BoneMFNode bone = skeleton.getBone(getBoneName());
        T entity = getEntity();
        if (bone != null) {
            float bodyYaw = MathHelper.interpolateAngle(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
            float headYaw = MathHelper.interpolateAngle(partialTicks, entity.prevRotationYawHead, entity.rotationYawHead);
            boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null &&
                    entity.getRidingEntity().shouldRiderSit());
            float netHeadYaw = headYaw - bodyYaw;
            if (shouldSit && entity.getRidingEntity() instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity.getRidingEntity();
                bodyYaw = MathHelper.interpolateAngle(partialTicks, livingentity.prevRenderYawOffset,
                        livingentity.renderYawOffset);
                netHeadYaw = headYaw - bodyYaw;
                float wrapped = MathHelper.wrapDegrees(netHeadYaw);
                if (wrapped < -getRotLimit()) {
                    wrapped = -getRotLimit();
                }

                if (wrapped >= getRotLimit()) {
                    wrapped = getRotLimit();
                }

                bodyYaw = headYaw - wrapped;
                if (wrapped * wrapped > 2500.0F) {
                    bodyYaw += wrapped * 0.2F;
                }
                netHeadYaw = headYaw - bodyYaw;
            }
            float headPitch = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
            float rotateY = netHeadYaw * ((float) Math.PI / 180F);
            boolean isFlying = entity.getTicksElytraFlying() > 4;
            float rotateX;
            if (isFlying) {
                rotateX = (-(float) Math.PI / 4F);
            } else {
                rotateX = headPitch * ((float) Math.PI / 180F);
            }
            Vector4d headRotation = new Vector4d(rotateX, rotateY, 0.0, 1.0);
            Matrix4d headTransform = bone.calculateLocalTransform(bone.getTranslation(),
                    headRotation, bone.getScaling());
            int boneId = skeleton.getBoneId(getBoneName());
            int parentBoneId = skeleton.getBoneParentId(getBoneName());
            Matrix4d parentTransform;
            if (parentBoneId != -1) {
                parentTransform = new Matrix4d(outPose.getJointMatrix(parentBoneId));
            } else {
                parentTransform = new Matrix4d();
            }
            outPose.setJointMatrix(boneId, parentTransform.mulAffine(headTransform));
        }
    }
```

One of the big differences for a procedural animation like this is rather than fetching a BakedAnimation resource
we fetch the entity's skeleton. From that skeleton we then fetch the bone we care about for animating the head, in this
case "bn_head" The next part of this code should be familiar to you if you've ever looked into Minecraft's
animation code. Essentially we determine the pitch and yaw based on various game conditions, and we only want
the net head yaw as the body also has a yaw so we just want to move the head by the difference between the entity yaw
and the head yaw. Once we've calculated the rotation, we call ```bone.calculateLocalTransform``` using the bone's 
already existing translation and scale but passing in our new calculated rotation vector. This will give us
the new local matrix for the head bone. We then fetch the current global transform of the parent of the bone from
the outPose and apply the newly created local transform to that to get the new head position.
