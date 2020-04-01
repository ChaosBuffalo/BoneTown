package com.chaosbuffalo.bonetown.client.render.render_data;

import com.chaosbuffalo.bonetown.core.model.BakedAnimatedMesh;
import com.chaosbuffalo.bonetown.client.render.platform.GlStateManagerExtended;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.chaosbuffalo.bonetown.core.bonemf.BoneMFModel.MAX_WEIGHTS;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

@OnlyIn(Dist.CLIENT)
public class BTAnimatedMeshRenderData extends BTMeshRenderData {

    private final BakedAnimatedMesh animatedMesh;

    public BTAnimatedMeshRenderData(BakedAnimatedMesh mesh){
        super(mesh);
        this.animatedMesh = mesh;
    }


    @Override
    public void uploadBuffers() {
        super.uploadBuffers();

        // weights
        int vboId = genVBO();
        ByteBuffer weightsByteBuffer = GLAllocation.createDirectByteBuffer(animatedMesh.weights.length * 4);
        FloatBuffer weightsBuffer = weightsByteBuffer.asFloatBuffer();
        weightsBuffer.put(animatedMesh.weights).flip();
        GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, vboId);
        GlStateManagerExtended.bufferData(GL_ARRAY_BUFFER, weightsByteBuffer, GL_STATIC_DRAW);
        GlStateManagerExtended.enableVertexAttribArray(3);
        GlStateManagerExtended.vertexAttribPointer(3, MAX_WEIGHTS, GL_FLOAT, false, 0, 0);

        // bone ids
        vboId = genVBO();
        ByteBuffer boneIdsByteBuffer = GLAllocation.createDirectByteBuffer(animatedMesh.boneIds.length * 4);
        IntBuffer boneIdsBuffer = boneIdsByteBuffer.asIntBuffer();
        boneIdsBuffer.put(animatedMesh.boneIds).flip();
        GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, vboId);
        GlStateManagerExtended.bufferData(GL_ARRAY_BUFFER, boneIdsByteBuffer, GL_STATIC_DRAW);
        GlStateManagerExtended.enableVertexAttribArray(4);
        GlStateManagerExtended.vertexAttribPointer(4, MAX_WEIGHTS, GL_FLOAT, false, 0, 0);
    }
}
