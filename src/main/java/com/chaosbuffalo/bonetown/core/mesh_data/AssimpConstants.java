package com.chaosbuffalo.bonetown.core.mesh_data;


import java.io.File;

public class AssimpConstants {

    public static String BASE_DIR = "bone_town";
    public static String ASSIMP_MODELS = "models";
    public static String ASSIMP_TEXTURES = "textures";
    public static String ASSIMP_MODELS_DIR = BASE_DIR + "/" + ASSIMP_MODELS;
    public static String ASSIMP_TEXTURES_DIR = BASE_DIR + "/" + ASSIMP_TEXTURES;



    public enum MeshTypes {
        FBX
    }

    public static String stringFromMeshType(MeshTypes type){
        switch (type) {
            case FBX:
                return "fbx";
            default:
                return "";
        }
    }
}
