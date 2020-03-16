package com.chaosbuffalo.bonetown.core;


public class BoneTownConstants {

    public static String BASE_DIR = "bonetown";
    public static String MODELS_DIR_NAME = "models";
    public static String ANIMATIONS_DIR_NAME = "animations";
    public static String BONETOWN_MODELS_DIR = BASE_DIR + "/" + MODELS_DIR_NAME;
    public static String BONETOWN_ANIMATIONS_DIR = BASE_DIR + "/" + ANIMATIONS_DIR_NAME;



    public enum MeshTypes {
        FBX,
        BONEMF
    }

    public static String stringFromMeshType(MeshTypes type){
        switch (type) {
            case FBX:
                return "fbx";
            case BONEMF:
                return "bonemf";
            default:
                return "";
        }
    }
}
