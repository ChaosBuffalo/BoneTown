package com.chaosbuffalo.bonetown.entity.animation_state.layers;


import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.ChangeLayerAnimationMessage;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class LayerWithAnimation<T extends Entity & IBTAnimatedEntity<T>> extends AnimationLayerBase<T> {
    public static final String BASE_SLOT = "BASE";

    private final Map<String, BakedAnimation> slots;

    public LayerWithAnimation(String name, ResourceLocation animName, T entity){
        super(name, entity);
        slots = new HashMap<>();
        setAnimation(animName, BASE_SLOT);
        addMessageCallback(ChangeLayerAnimationMessage.CHANGE_ANIMATION_TYPE, this::consumeChangeAnimation);
    }

    public void setAnimation(ResourceLocation anim, String slotName){
        BoneMFSkeleton skeleton = getEntity().getSkeleton();
        if (skeleton != null){
            BakedAnimation animation = skeleton.getBakedAnimation(anim);
            slots.put(slotName, animation);
            if (animation == null){
                BoneTown.LOGGER.error("Animation {} not found for entity: {}",
                        anim.toString(), getEntity().toString());
            }
        }
    }

    protected void changeAnimationHandler(ChangeLayerAnimationMessage message){
        setAnimation(message.getAnim(), message.getSlot());
    }

    private void consumeChangeAnimation(AnimationLayerMessage message){
        if (message instanceof ChangeLayerAnimationMessage){
            ChangeLayerAnimationMessage changeMessage = (ChangeLayerAnimationMessage) message;
            changeAnimationHandler(changeMessage);
        }
    }

    @Nullable
    public BakedAnimation getAnimation(String slotName) {
        return slots.get(slotName);
    }
}
