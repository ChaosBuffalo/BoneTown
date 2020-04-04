package com.chaosbuffalo.bonetown.entity.animation_state.messages.layer;


import net.minecraft.network.PacketBuffer;

public abstract class AnimationLayerMessage {
    private String messageType;

    public String getMessageType() {
        return messageType;
    }

    public AnimationLayerMessage(String messageType){
        this.messageType = messageType;
    }

    public void toPacketBuffer(PacketBuffer buffer) {
        buffer.writeString(messageType);
    }


}
