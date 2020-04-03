package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.entity.animation_state.layers.LayerWithAnimation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ChangeLayerAnimationMessage extends AnimationLayerMessage {
    public static String CHANGE_ANIMATION_TYPE = "CHANGE_ANIMATION_TYPE";
    private String slot;
    private final ResourceLocation anim;

    static {
        LayerMessageFactory.addDeseralizer(CHANGE_ANIMATION_TYPE, ChangeLayerAnimationMessage::fromNBT);
        LayerMessageFactory.addSerializer(CHANGE_ANIMATION_TYPE, ChangeLayerAnimationMessage::toNBT);
    }

    private static CompoundNBT toNBT(AnimationLayerMessage message, CompoundNBT tag){
        if (message instanceof ChangeLayerAnimationMessage){
            ChangeLayerAnimationMessage changeMessage = (ChangeLayerAnimationMessage) message;
            tag.putString("slot", changeMessage.getSlot());
            tag.putString("anim", changeMessage.getAnim().toString());
        }
        return tag;
    }

    private static ChangeLayerAnimationMessage fromNBT(CompoundNBT tag){
        String slot = tag.getString("slot");
        ResourceLocation animName = new ResourceLocation(tag.getString("anim"));
        return new ChangeLayerAnimationMessage(animName, slot);
    }

    public ChangeLayerAnimationMessage(ResourceLocation newAnim) {
        this(newAnim, LayerWithAnimation.BASE_SLOT);
    }

    public ChangeLayerAnimationMessage(ResourceLocation newAnim, String slot) {
        super(CHANGE_ANIMATION_TYPE);
        this.anim = newAnim;
        this.slot = slot;
    }

    public ResourceLocation getAnim() {
        return anim;
    }

    public String getSlot() {
        return slot;
    }
}
