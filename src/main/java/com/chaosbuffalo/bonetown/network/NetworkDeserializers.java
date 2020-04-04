package com.chaosbuffalo.bonetown.network;

import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;

public class NetworkDeserializers {

    public static final StringTypeNetworkDeserializer<AnimationLayerMessage> layerMessageDeserializer =
            new StringTypeNetworkDeserializer<>();

    public static final StringTypeNetworkDeserializer<AnimationMessage> animationMessageDeserializer =
            new StringTypeNetworkDeserializer<>();
}
