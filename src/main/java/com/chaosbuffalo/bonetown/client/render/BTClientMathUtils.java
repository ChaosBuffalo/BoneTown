package com.chaosbuffalo.bonetown.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public class BTClientMathUtils {

    public static void applyTransformToStack(org.joml.Matrix4d inMat, MatrixStack inStack){
        Vector3d translation = new Vector3d();
        inMat.getTranslation(translation);
        Quaterniond quat = new Quaterniond();
        inMat.getUnnormalizedRotation(quat);
        Vector3d scale = new Vector3d();
        inMat.getScale(scale);
        Quaternion mcQuat = new Quaternion((float) quat.x(), (float) quat.y(), (float) quat.z(), (float)quat.w());
        inStack.translate(translation.x(), translation.y(), translation.z());
        inStack.rotate(mcQuat);
        inStack.scale((float) scale.x(), (float) scale.y(), (float) scale.z());
    }

    public static Matrix4f fromJOMLMat(org.joml.Matrix4f inMat){
        float[] matArr = new float[16];
        matArr[0] = inMat.m00();
        matArr[1] = inMat.m10();
        matArr[2] = inMat.m20();
        matArr[3] = inMat.m30();
        matArr[4] = inMat.m01();
        matArr[5] = inMat.m11();
        matArr[6] = inMat.m21();
        matArr[7] = inMat.m31();
        matArr[8] = inMat.m02();
        matArr[9] = inMat.m12();
        matArr[10] = inMat.m22();
        matArr[11] = inMat.m32();
        matArr[12] = inMat.m03();
        matArr[13] = inMat.m13();
        matArr[14] = inMat.m23();
        matArr[15] = inMat.m33();
        return new Matrix4f(matArr);
    }

    public static org.joml.Matrix4f toJOMLMat(Matrix4f inMat){
        FloatBuffer matIn = FloatBuffer.allocate(16);
        inMat.write(matIn);
        org.joml.Matrix4f result = new org.joml.Matrix4f();
        result.m00(matIn.get(0));
        result.m10(matIn.get(1));
        result.m20(matIn.get(2));
        result.m30(matIn.get(3));
        result.m01(matIn.get(4));
        result.m11(matIn.get(5));
        result.m21(matIn.get(6));
        result.m31(matIn.get(7));
        result.m02(matIn.get(8));
        result.m12(matIn.get(9));
        result.m22(matIn.get(10));
        result.m32(matIn.get(11));
        result.m03(matIn.get(12));
        result.m13(matIn.get(13));
        result.m23(matIn.get(14));
        result.m33(matIn.get(15));
        return result;
    }
}
