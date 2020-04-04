package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.PacketBuffer;

public class LayerMessage extends LayerControlMessage {

    public static final String LAYER_MESSAGE = "LAYER_MESSAGE";

    private AnimationLayerMessage message;

    static {
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(LAYER_MESSAGE,
                LayerMessage::fromPacketBuffer);
        AnimationComponent.addMessageHandler(LAYER_MESSAGE, LayerMessage::handleMessage);
    }

    public LayerMessage(String stateName, String layerName, AnimationLayerMessage message) {
        super(LAYER_MESSAGE, stateName, layerName);
        this.message = message;
    }

    private static void handleMessage(AnimationComponent<?> component, AnimationMessage message){
        if (message instanceof LayerMessage){
            LayerMessage msg = (LayerMessage) message;
            if (msg.getMessage() != null){
                component.distributeLayerMessage(msg.getStateName(), msg.getLayerName(), msg.getMessage());
            }
        }
    }

    @Override
    public void toPacketBuffer(PacketBuffer buffer) {
        super.toPacketBuffer(buffer);
        message.toPacketBuffer(buffer);
    }

    private static LayerMessage fromPacketBuffer(PacketBuffer buffer){
        String stateName = buffer.readString();
        String layerName = buffer.readString();
        AnimationLayerMessage msg = NetworkDeserializers.layerMessageDeserializer.deserialize(buffer);
        return new LayerMessage(stateName, layerName, msg);
    }

    public AnimationLayerMessage getMessage() {
        return message;
    }
}
