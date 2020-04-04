package com.chaosbuffalo.bonetown.network;

import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class StringTypeNetworkDeserializer<T> implements ITypedNetworkDeserializer<T, String>  {

    private final HashMap<String, Function<PacketBuffer, T>> deserializers;

    public StringTypeNetworkDeserializer(){
        deserializers = new HashMap<>();
    }

    @Override
    public void addNetworkDeserializer(String messageType, Function<PacketBuffer, T> callback) {
        deserializers.put(messageType, callback);
    }

    @Nullable
    @Override
    public T deserialize(PacketBuffer message) {
        String type = message.readString();
        if (deserializers.containsKey(type)){
            return deserializers.get(type).apply(message);
        } else {
            BoneTown.LOGGER.error("Failed to find deserializer for type: {}", type);
            return null;
        }
    }
}
