package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.PacketBuffer;

public class PopStateMessage extends AnimationMessage {
    public static final String POP_STATE = "POP_STATE";

    static {
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(POP_STATE,
                PopStateMessage::fromPacketBuffer);
        AnimationComponent.addMessageHandler(POP_STATE, PopStateMessage::handleMessage);
    }

    private static void handleMessage(AnimationComponent<?> component, AnimationMessage message){
        BoneTown.LOGGER.info("Popping state for {}", component.getEntity());
        component.popState();
    }

    private static PopStateMessage fromPacketBuffer(PacketBuffer buffer){
        return new PopStateMessage();
    }

    @Override
    public void toPacketBuffer(PacketBuffer buffer) {
        super.toPacketBuffer(buffer);
    }

    public PopStateMessage() {
        super(POP_STATE);
    }

}
