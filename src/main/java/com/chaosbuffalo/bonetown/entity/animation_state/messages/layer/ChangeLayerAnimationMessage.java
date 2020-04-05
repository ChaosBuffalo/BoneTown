package com.chaosbuffalo.bonetown.entity.animation_state.messages.layer;

import com.chaosbuffalo.bonetown.entity.animation_state.layers.LayerWithAnimation;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ChangeLayerAnimationMessage extends AnimationLayerMessage {
    public static String CHANGE_ANIMATION_TYPE = "CHANGE_ANIMATION_TYPE";
    private final String slot;
    private final ResourceLocation anim;

    static {
        NetworkDeserializers.layerMessageDeserializer.addNetworkDeserializer(CHANGE_ANIMATION_TYPE,
                ChangeLayerAnimationMessage::fromPacketBuffer);
    }

    @Override
    public void toPacketBuffer(PacketBuffer buffer){
        super.toPacketBuffer(buffer);
        buffer.writeString(getSlot());
        buffer.writeResourceLocation(getAnim());
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
