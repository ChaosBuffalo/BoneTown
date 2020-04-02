package com.chaosbuffalo.bonetown.entity.animation_state.messages;

public abstract class AnimationLayerMessage {
    private String messageType;

    public String getMessageType() {
        return messageType;
    }

    public AnimationLayerMessage(String messageType){
        this.messageType = messageType;
    }
}
