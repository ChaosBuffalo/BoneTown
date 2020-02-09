package com.chaosbuffalo.bonetown.core.mesh_data;

import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.ResourceLocation;


public class BoneTownMaterial {

    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Vector4f diffuseColour;

    private Vector4f specularColour;

    private float shininess;

    private float reflectance;

    private ResourceLocation texture;


    public BoneTownMaterial() {
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        this.reflectance = 0;
    }

    public BoneTownMaterial(Vector4f colour, float reflectance) {
        this(colour, colour, null, reflectance);
    }

    public BoneTownMaterial(ResourceLocation texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0);
    }

    public BoneTownMaterial(ResourceLocation texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
    }

    public BoneTownMaterial(Vector4f diffuseColour, Vector4f specularColour, float reflectance) {
        this(diffuseColour, specularColour, null, reflectance);
    }

    public BoneTownMaterial(Vector4f diffuseColour, Vector4f specularColour, ResourceLocation texture, float reflectance) {
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour) {
        this.specularColour = specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }
}