package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.PacketBuffer;

public class EnterStateMessage extends AnimationMessage {
    private String stateName;
    public static final String ENTER_STATE = "ENTER_STATE";

    static {
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(ENTER_STATE,
                EnterStateMessage::fromPacketBuffer);
        AnimationComponent.addMessageHandler(ENTER_STATE, EnterStateMessage::handleMessage);
    }

    private static void handleMessage(AnimationComponent<?> component, AnimationMessage message){
        if (message instanceof EnterStateMessage){
            component.setState(((EnterStateMessage) message).getStateName());
        }
    }

    private static EnterStateMessage fromPacketBuffer(PacketBuffer buffer){
        String stateName = buffer.readString();
        return new EnterStateMessage(stateName);
    }

    @Override
    public void toPacketBuffer(PacketBuffer buffer) {
        super.toPacketBuffer(buffer);
        buffer.writeString(getStateName());
    }

    public EnterStateMessage(String stateName) {
        super(ENTER_STATE);
        this.stateName = stateName;
    }

    public String getStateName() {
        return stateName;
    }
}
