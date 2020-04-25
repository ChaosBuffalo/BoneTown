package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.client.render.entity.BTAnimatedEntityRenderer;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.IHasHandBones;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimatedHeldItemLayer<T extends LivingEntity & IBTAnimatedEntity<T> & IHasHandBones>
        extends BTAnimatedLayerRenderer<T> {


    public AnimatedHeldItemLayer(BTAnimatedEntityRenderer<T> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int packedLight, T entityIn,
                       IPose pose, float partialTicks, float ageInTicks, IBTMaterial currentMaterial,
                       Matrix4f projectionMatrix) {
        boolean isRightHanded = entityIn.getPrimaryHand() == HandSide.RIGHT;
        ItemStack leftHandItem = isRightHanded ? entityIn.getHeldItemOffhand() : entityIn.getHeldItemMainhand();
        ItemStack rightHandItem = isRightHanded ? entityIn.getHeldItemMainhand() : entityIn.getHeldItemOffhand();
        if (!leftHandItem.isEmpty() || !rightHandItem.isEmpty()) {
            renderItemInHand(entityIn, pose, rightHandItem, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    HandSide.RIGHT, matrixStack, renderBuffer, packedLight);
            renderItemInHand(entityIn, pose, leftHandItem, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                    HandSide.LEFT, matrixStack, renderBuffer, packedLight);
        }
    }

    private void renderItemInHand(T entity, IPose pose, ItemStack itemStack,
                                  ItemCameraTransforms.TransformType cameraTransform,
                                  HandSide handSide, MatrixStack matrixStack,
                                  IRenderTypeBuffer bufferIn, int packedLight) {
        if (!itemStack.isEmpty()) {
            matrixStack.push();
            boolean isLeftHand = handSide == HandSide.LEFT;
            BTAnimatedEntityRenderer<T> renderer = getEntityRenderer();
            String boneName = isLeftHand ? entity.getLeftHandBoneName() : entity.getRightHandBoneName();
            renderer.moveMatrixStackToBone(entity, boneName, matrixStack, pose);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0f));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, itemStack, cameraTransform,
                    isLeftHand, matrixStack, bufferIn, packedLight);
            matrixStack.pop();
        }

    }
}
