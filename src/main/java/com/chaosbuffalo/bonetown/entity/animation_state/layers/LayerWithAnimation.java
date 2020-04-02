package com.chaosbuffalo.bonetown.entity.animation_state.layers;


import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationLayerMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.ChangeLayerAnimationMessage;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class LayerWithAnimation<T extends Entity & IBTAnimatedEntity> extends AnimationLayerBase<T> {
    private BakedAnimation animation;

    public LayerWithAnimation(String name, ResourceLocation animName, T entity){
        super(name, entity);
        setAnimation(animName);
        addMessageCallback(ChangeLayerAnimationMessage.CHANGE_ANIMATION_TYPE, this::consumeChangeAnimation);
    }

    private void setAnimation(ResourceLocation anim){
        BoneMFSkeleton skeleton = getEntity().getSkeleton();
        if (skeleton != null){
            animation = skeleton.getBakedAnimation(anim);
            if (animation != null){
                this.isValid = true;
            } else {
                this.isValid = false;
                BoneTown.LOGGER.error("Animation {} not found for entity: {}",
                        anim.toString(), getEntity().toString());
            }
        }
    }

    protected void changeAnimationHandler(ChangeLayerAnimationMessage message){
        if (message.getSlot() == 0){
            setAnimation(message.getAnim());
        }
    }

    private void consumeChangeAnimation(AnimationLayerMessage message){
        if (message instanceof ChangeLayerAnimationMessage){
            ChangeLayerAnimationMessage changeMessage = (ChangeLayerAnimationMessage) message;
            changeAnimationHandler(changeMessage);
        }
    }

    public BakedAnimation getAnimation() {
        return animation;
    }
}
