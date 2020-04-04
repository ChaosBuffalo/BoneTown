package com.chaosbuffalo.bonetown.network;

import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ITypedNetworkDeserializer<T, U> {

    void addNetworkDeserializer(U messageType, Function<PacketBuffer, T> callback);

    @Nullable
    T deserialize(PacketBuffer message);
}
