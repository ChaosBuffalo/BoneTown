package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.PacketBuffer;

public class PushStateMessage extends AnimationMessage {
    private String stateName;
    public static final String PUSH_STATE = "PUSH_STATE";

    static {
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(PUSH_STATE,
                PushStateMessage::fromPacketBuffer);
        AnimationComponent.addMessageHandler(PUSH_STATE, PushStateMessage::handleMessage);
    }

    private static void handleMessage(AnimationComponent<?> component, AnimationMessage message){
        if (message instanceof PushStateMessage){
            component.pushState(((PushStateMessage) message).getStateName());
        }
    }

    private static PushStateMessage fromPacketBuffer(PacketBuffer buffer){
        String stateName = buffer.readString();
        return new PushStateMessage(stateName);
    }

    @Override
    public void toPacketBuffer(PacketBuffer buffer) {
        super.toPacketBuffer(buffer);
        buffer.writeString(getStateName());
    }

    public PushStateMessage(String stateName) {
        super(PUSH_STATE);
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
}
