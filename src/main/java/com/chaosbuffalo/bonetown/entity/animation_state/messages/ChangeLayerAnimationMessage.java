package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import net.minecraft.util.ResourceLocation;

public class ChangeLayerAnimationMessage extends AnimationLayerMessage {
    public static String CHANGE_ANIMATION_TYPE = "CHANGE_ANIMATION_TYPE";

    private final ResourceLocation anim;

    public ChangeLayerAnimationMessage(ResourceLocation newAnim) {
        super(CHANGE_ANIMATION_TYPE);
        this.anim = newAnim;
    }

    public ResourceLocation getAnim() {
        return anim;
    }
}
