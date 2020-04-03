package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LayerMessageFactory {

    private static final HashMap<String, BiFunction<AnimationLayerMessage, CompoundNBT, CompoundNBT>> SERIALIZERS = new HashMap<>();
    private static final HashMap<String, Function<CompoundNBT, AnimationLayerMessage>> DESERIALIZERS = new HashMap<>();


    public static void addSerializer(String messageType,
                                     BiFunction<AnimationLayerMessage, CompoundNBT, CompoundNBT> callback){
        SERIALIZERS.put(messageType, callback);
    }

    public static void addDeseralizer(String messageType,
                                      Function<CompoundNBT, AnimationLayerMessage> callback){
        DESERIALIZERS.put(messageType, callback);
    }

    @Nullable
    public static AnimationLayerMessage deserialize(CompoundNBT message){
        String msgType = message.getString("messageType");
        Function<CompoundNBT, AnimationLayerMessage> decoder = DESERIALIZERS.get(msgType);
        if (decoder == null){
            BoneTown.LOGGER.error("Failed to find animation message factory decoder for message type: {}",
                    msgType);
            return null;
        }
        return decoder.apply(message);
    }

    @Nullable
    public static CompoundNBT serialize(AnimationLayerMessage message){
        BiFunction<AnimationLayerMessage, CompoundNBT, CompoundNBT> encoder = SERIALIZERS.get(message.getMessageType());
        if (encoder == null){
            BoneTown.LOGGER.error("Failed to find animation message factory encoder for message type: {}",
                    message.getMessageType());
            return null;
        }
        return encoder.apply(message, getTagForMessageType(message.getMessageType()));
    }

    public static CompoundNBT getTagForMessageType(String messageType){
        CompoundNBT tag = new CompoundNBT();
        tag.putString("messageType", messageType);
        return tag;
    }
}
