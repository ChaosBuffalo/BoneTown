package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.client.render.RenderDataManager;
import com.chaosbuffalo.bonetown.client.render.entity.BTAnimatedEntityRenderer;
import com.chaosbuffalo.bonetown.client.render.render_data.BTArmorRenderData;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class AnimatedArmorLayer<T extends LivingEntity & IBTAnimatedEntity<T>> extends BTAnimatedLayerRenderer<T> {
    private final BTAnimatedModel model;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = new HashMap<>();

    public AnimatedArmorLayer(BTAnimatedEntityRenderer<T> entityRenderer, BTAnimatedModel model) {
        super(entityRenderer);
        this.model = model;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int packedLight, T entityIn,
                       IPose pose, float partialTicks, float ageInTicks, IBTMaterial currentMaterial,
                       Matrix4f projectionMatrix) {
        renderArmorPart(matrixStack, entityIn, EquipmentSlotType.LEGS, packedLight, partialTicks, currentMaterial,
                projectionMatrix);
        renderArmorPart(matrixStack, entityIn, EquipmentSlotType.FEET, packedLight, partialTicks, currentMaterial,
                projectionMatrix);
        renderArmorPart(matrixStack, entityIn, EquipmentSlotType.CHEST, packedLight, partialTicks, currentMaterial,
                projectionMatrix);
        renderArmorPart(matrixStack, entityIn, EquipmentSlotType.HEAD, packedLight, partialTicks, currentMaterial,
                projectionMatrix);
    }

    private void renderArmorPart(MatrixStack matrixStack, T entityIn,
                                 EquipmentSlotType slot, int packedLight, float partialTicks,
                                 IBTMaterial material, Matrix4f projectionMatrix) {
        ItemStack itemStack = entityIn.getItemStackFromSlot(slot);
        if (itemStack.getItem() instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem) itemStack.getItem();
            model.getBakedArmorForMaterial(armorItem.getArmorMaterial()).ifPresent((meshes) -> {
                BTArmorRenderData renderData = RenderDataManager.MANAGER.getArmorRenderDataForModel(meshes);
                ResourceLocation armorResource = getArmorResource(entityIn, itemStack, slot, null);
                RenderType renderType = RenderType.entityCutoutNoCull(armorResource);
                int packedOverlay = LivingRenderer.getPackedOverlay(entityIn, partialTicks);
                material.initRender(renderType, matrixStack, projectionMatrix, packedLight, packedOverlay);
                if (!renderData.isInitialized()){
                    renderData.upload();
                }
                renderData.renderSlot(slot);
                material.endRender(renderType);
            });
        }

    }

    public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlotType slot,
                                             @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(58);
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String textureLoc = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
                slot == EquipmentSlotType.LEGS ? 2 : 1, type == null ? "" : String.format("_%s", type));
        textureLoc = ForgeHooksClient.getArmorTexture(entity, stack, textureLoc, slot, type);
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(textureLoc);
        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(textureLoc);
            ARMOR_TEXTURE_RES_MAP.put(textureLoc, resourcelocation);
        }
        return resourcelocation;
    }
}
