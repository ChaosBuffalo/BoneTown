package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import net.minecraft.util.math.AxisAlignedBB;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class AnimationUtils {


    public static AxisAlignedBB GetBBoxForPose(IPose pose){
        double highestX = 0.0;
        double lowestX = 0.0;
        double highestY = 0.0;
        double highestZ = 0.0;
        double lowestY = 0.0;
        double lowestZ = 0.0;
        Vector3d workPos = new Vector3d();
        for (int i = 0; i < pose.getJointCount(); i++){
            Matrix4d mat = pose.getJointMatrix(i);
            getTranslationComponentEfficient(mat, workPos);
            if (workPos.x() > highestX){
                highestX = workPos.x();
            } else if (workPos.x() < lowestX){
                lowestX = workPos.x();
            }
            if (workPos.y() > highestY){
                highestY = workPos.y();
            } else if (workPos.y() < lowestY){
                lowestY = workPos.y();
            }
            if (workPos.z() > highestZ){
                highestZ = workPos.z();
            } else if (workPos.z() < lowestZ){
                lowestZ = workPos.z();
            }
        }
        AxisAlignedBB result = new AxisAlignedBB(lowestX, lowestY, lowestZ, highestX, highestY, highestZ);
        return result;
    }

    public static Vector3d getTranslationComponent(Matrix4d mat){
        Vector3d pos = new Vector3d();
        getTranslationComponentEfficient(mat, pos);
        return pos;
    }

    public static void getTranslationComponentEfficient(Matrix4d mat, Vector3d out){
        mat.getTranslation(out);
    }
}
