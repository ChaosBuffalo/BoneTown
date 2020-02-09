package com.chaosbuffalo.bonetown.client.render.assimp;

import com.chaosbuffalo.bonetown.core.mesh_data.BTMeshData;
import com.chaosbuffalo.bonetown.core.mesh_data.BoneTownMaterial;
import com.chaosbuffalo.bonetown.core.mesh_data.BoneTownMesh;
import com.chaosbuffalo.bonetown.platform.GlStateManagerExtended;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.EntityRendererManager;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;


public class BTEntityRenderData {
    private BTMeshData model;
    public static final int MAX_WEIGHTS = 4;
    private EntityRendererManager manager;
    private boolean initialized;

    private final HashMap<String, BTMeshRenderData> meshData = new HashMap<>();

    public BTEntityRenderData(BTMeshData modelIn, EntityRendererManager managerIn){
        model = modelIn;
        manager = managerIn;
        initialized = false;
    }

    public void GLinit(){
        for (BoneTownMesh mesh : model.getMeshes()){
            meshData.put(mesh.name, new BTMeshRenderData(mesh, manager));
        }
        initialized = true;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public void GLclear(){
        for (BTMeshRenderData meshRenderData : meshData.values()){
            meshRenderData.cleanup();
        }
        meshData.clear();
        initialized = false;
    }

    public void render(){
        if (!initialized){
            return;
        }
        for (BTMeshRenderData meshRenderData : meshData.values()){
            meshRenderData.render();
        }
    }

    private class BTMeshRenderData {
        private final BoneTownMesh mesh;
        protected final int vaoId;
        protected final List<Integer> vboIdList;
        private final int vertexCount;
        private EntityRendererManager manager;

        BTMeshRenderData(BoneTownMesh mesh, EntityRendererManager manager){

            ByteBuffer posByteBuffer;
            ByteBuffer textCoordsByteBuffer;
            ByteBuffer vecNormalsByteBuffer;
            FloatBuffer weightsBuffer = null;
            IntBuffer jointIndicesBuffer = null;
            ByteBuffer indicesByteBuffer;
            this.mesh = mesh;
            this.manager = manager;
            vertexCount = mesh.indices.length;
            vboIdList = new ArrayList<>();

            vaoId = GlStateManagerExtended.genVertexArrays();
            GlStateManagerExtended.bindVertexArray(vaoId);
            int vboId = GlStateManagerExtended.genBuffers();
            vboIdList.add(vboId);
            posByteBuffer = GLAllocation.createDirectByteBuffer(mesh.positions.length * 4);
            FloatBuffer posBuffer = posByteBuffer.asFloatBuffer();
            posBuffer.put(mesh.positions).flip();
            GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, vboId);
            GlStateManagerExtended.bufferData(GL_ARRAY_BUFFER, posByteBuffer, GL_STATIC_DRAW);
            GlStateManagerExtended.enableVertexAttribArray(0);
            GlStateManagerExtended.vertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            vboId = GlStateManagerExtended.genBuffers();
            vboIdList.add(vboId);
            textCoordsByteBuffer = GLAllocation.createDirectByteBuffer(mesh.textCoords.length * 4);
            FloatBuffer textCoordsBuffer = textCoordsByteBuffer.asFloatBuffer();
            textCoordsBuffer.put(mesh.textCoords).flip();
            GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, vboId);
            GlStateManagerExtended.bufferData(GL_ARRAY_BUFFER, textCoordsByteBuffer, GL_STATIC_DRAW);
            GlStateManagerExtended.enableVertexAttribArray(1);
            GlStateManagerExtended.vertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            vboId = GlStateManagerExtended.genBuffers();
            vboIdList.add(vboId);
            vecNormalsByteBuffer = GLAllocation.createDirectByteBuffer(mesh.normals.length * 4);
            FloatBuffer vecNormalsBuffer = vecNormalsByteBuffer.asFloatBuffer();
            vecNormalsBuffer.put(mesh.normals).flip();
            GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, vboId);
            GlStateManagerExtended.bufferData(GL_ARRAY_BUFFER, vecNormalsByteBuffer, GL_STATIC_DRAW);
            GlStateManagerExtended.enableVertexAttribArray(2);
            GlStateManagerExtended.vertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            vboId = GlStateManagerExtended.genBuffers();
            vboIdList.add(vboId);
            indicesByteBuffer = GLAllocation.createDirectByteBuffer(mesh.indices.length * 4);
            IntBuffer indicesBuffer = indicesByteBuffer.asIntBuffer();
            indicesBuffer.put(mesh.indices).flip();
            GlStateManagerExtended.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            GlStateManagerExtended.bufferData(GL_ELEMENT_ARRAY_BUFFER, indicesByteBuffer, GL_STATIC_DRAW);

            GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, 0);
            GlStateManagerExtended.bindVertexArray(0);

        }

        protected void initRender() {
            BoneTownMaterial material = mesh.getMaterial();
            if (material.getTexture() != null) {
                GlStateManagerExtended.activeTexture(GL_TEXTURE0);
                this.manager.textureManager.bindTexture(material.getTexture());
            }
            // Draw the mesh
            GlStateManagerExtended.bindVertexArray(vaoId);
        }

        protected void endRender(){
            GlStateManagerExtended.bindVertexArray(0);
            GlStateManagerExtended.bindTexture(0);
        }

        public void render() {
            initRender();
            GlStateManagerExtended.drawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
            endRender();
        }

        public void cleanup(){
            GlStateManagerExtended.disableVertexAttribArray(0);

            // Delete the VBOs
            GlStateManagerExtended.bindBuffer(GL_ARRAY_BUFFER, 0);
            for (int vboId : vboIdList) {
                GlStateManagerExtended.deleteBuffers(vboId);
            }


            // Delete the VAO
            GlStateManagerExtended.bindVertexArray(0);
            GlStateManagerExtended.deleteVertexArrays(vaoId);
        }
    }
}
