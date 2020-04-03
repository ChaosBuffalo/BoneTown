package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.entity.animation_state.layers.LayerWithAnimation;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ChangeLayerAnimationMessage extends AnimationLayerMessage {
    public static String CHANGE_ANIMATION_TYPE = "CHANGE_ANIMATION_TYPE";
    private String slot;
    private final ResourceLocation anim;

    static {
        LayerMessageFactory.addNetworkDeserializer(CHANGE_ANIMATION_TYPE, ChangeLayerAnimationMessage::fromPacketBuffer);
        LayerMessageFactory.addNetworkSerializer(CHANGE_ANIMATION_TYPE, ChangeLayerAnimationMessage::toPacketBuffer);
    }

    private static void toPacketBuffer(AnimationLayerMessage message, PacketBuffer buffer){
        if (message instanceof ChangeLayerAnimationMessage){
            ChangeLayerAnimationMessage changeMessage = (ChangeLayerAnimationMessage) message;
            buffer.writeString(changeMessage.getSlot());
            buffer.writeResourceLocation(changeMessage.getAnim());
        }
    }

    private static ChangeLayerAnimationMessage fromPacketBuffer(PacketBuffer buffer){
        String slot = buffer.readString();
        ResourceLocation animName = buffer.readResourceLocation();
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
