package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LayerMessageFactory {

    private static final HashMap<String, BiConsumer<AnimationLayerMessage, PacketBuffer>> SERIALIZERS = new HashMap<>();
    private static final HashMap<String, Function<PacketBuffer, AnimationLayerMessage>> DESERIALIZERS = new HashMap<>();


    public static void addNetworkSerializer(String messageType,
                                            BiConsumer<AnimationLayerMessage, PacketBuffer> callback){
        SERIALIZERS.put(messageType, callback);
    }

    public static void addNetworkDeserializer(String messageType,
                                              Function<PacketBuffer, AnimationLayerMessage> callback){
        DESERIALIZERS.put(messageType, callback);
    }

    @Nullable
    public static AnimationLayerMessage deserialize(PacketBuffer message){
        String msgType = message.readString();
        Function<PacketBuffer, AnimationLayerMessage> decoder = DESERIALIZERS.get(msgType);
        if (decoder == null){
            BoneTown.LOGGER.error("Failed to find animation message factory decoder for message type: {}",
                    msgType);
            return null;
        }
        return decoder.apply(message);
    }

    @Nullable
    public static void serialize(AnimationLayerMessage message, PacketBuffer buffer){
        BiConsumer<AnimationLayerMessage, PacketBuffer> encoder = SERIALIZERS.get(message.getMessageType());
        buffer.writeString(message.getMessageType());
        if (encoder == null){
            BoneTown.LOGGER.error("Failed to find animation message factory encoder for message type: {}",
                    message.getMessageType());
        }
        encoder.accept(message, buffer);
    }


}
