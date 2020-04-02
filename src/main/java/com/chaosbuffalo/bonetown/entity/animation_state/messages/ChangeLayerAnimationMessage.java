package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import net.minecraft.util.ResourceLocation;

public class ChangeLayerAnimationMessage extends AnimationLayerMessage {
    public static String CHANGE_ANIMATION_TYPE = "CHANGE_ANIMATION_TYPE";
    private int slot;
    private final ResourceLocation anim;

    public ChangeLayerAnimationMessage(ResourceLocation newAnim) {
        this(newAnim, 0);
    }

    public ChangeLayerAnimationMessage(ResourceLocation newAnim, int slot) {
        super(CHANGE_ANIMATION_TYPE);
        this.anim = newAnim;
        this.slot = slot;
    }

    public ResourceLocation getAnim() {
        return anim;
    }

    public int getSlot() {
        return slot;
    }
}
