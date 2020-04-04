package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import net.minecraft.network.PacketBuffer;

public abstract class AnimationMessage {

    public void toPacketBuffer(PacketBuffer buffer){
        buffer.writeString(getType());
    }

    private String type;

    public String getType() {
        return type;
    }

    public AnimationMessage(String type){
        this.type = type;
    }
}
