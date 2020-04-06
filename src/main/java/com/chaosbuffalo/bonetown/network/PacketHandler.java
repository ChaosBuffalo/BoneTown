package com.chaosbuffalo.bonetown.network;

import com.chaosbuffalo.bonetown.BoneTown;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static SimpleChannel networkChannel;
    private static final String VERSION = "1.0";

    public static void setupHandler(){
        networkChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(BoneTown.MODID, "packet_handler"),
                () -> VERSION, s -> s.equals(VERSION), s -> s.equals(VERSION));
        registerMessages(networkChannel);
    }

    public static SimpleChannel getNetworkChannel(){
        return networkChannel;
    }

    public static void sendAnimationUpdate(ServerPlayerEntity player, EntityAnimationClientUpdatePacket packet){
        networkChannel.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void registerMessages(SimpleChannel channel){
        channel.registerMessage(1, EntityAnimationClientUpdatePacket.class,
                EntityAnimationClientUpdatePacket::toBytes,
                EntityAnimationClientUpdatePacket::new,
                EntityAnimationClientUpdatePacket::handle);
    }
}
