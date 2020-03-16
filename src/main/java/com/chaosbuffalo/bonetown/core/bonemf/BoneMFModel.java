package com.chaosbuffalo.bonetown.core.bonemf;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.Utils;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedMesh;
import com.chaosbuffalo.bonetown.core.model.BTMesh;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class BoneMFModel {
    public static int MAX_WEIGHTS = 4;
    private final BoneMFNode rootNode;
    private final boolean hasSkeleton;
    private final BoneMFSkeleton skeleton;


    public BoneMFModel(BoneMFNode rootNode){
        this.rootNode = rootNode;
        BoneMFNode skeletonRoot = rootNode.getNodeWithAttributeType(BoneMFAttribute.AttributeTypes.SKELETON);

        if (skeletonRoot != null){
            BoneTown.LOGGER.info("Found skeleton root for {}: {}",
                    rootNode.getName(), skeletonRoot.toString());
            hasSkeleton = true;
            skeleton = new BoneMFSkeleton(skeletonRoot);

        } else {
            hasSkeleton = false;
            BoneTown.LOGGER.warn("Found no skeleton for {}", rootNode.getName());
            skeleton = null;
        }
    }

    public Optional<BoneMFSkeleton> getSkeleton() {
        return Optional.ofNullable(skeleton);
    }

    public boolean hasSkeleton(){
        return hasSkeleton;
    }

    public List<BTAnimatedMesh> getBakeAsAnimatedMeshes(){
        List<BTMesh> meshes = bakeMeshes();
        List<BTAnimatedMesh> animated = new ArrayList<>();
        for (BTMesh mesh : meshes){
            if (mesh instanceof BTAnimatedMesh){
                animated.add((BTAnimatedMesh) mesh);
            }
        }
        return animated;
    }

    public List<BTMesh> bakeMeshes(){
        List<BTMesh> meshes = new ArrayList<>();
        List<BoneMFNode> meshNodes = getRootNode().getNodesOfType(BoneMFAttribute.AttributeTypes.MESH);
        for (BoneMFNode meshNode : meshNodes){
            BoneMFMeshAttribute meshAttribute = meshNode.getMesh();
            if (meshAttribute == null){
                BoneTown.LOGGER.warn("Failed to find mesh attribute for {}, skipping bake.",
                        meshNode.getName());
                continue;
            }
            List<Integer> triangles = meshAttribute.getTriangles();
            List<BoneMFVertex> vertices = meshAttribute.getVertices();
            List<Float> positions = new ArrayList<>();
            List<Float> uvs = new ArrayList<>();
            List<Float> normals = new ArrayList<>();
            for (BoneMFVertex vertex : vertices){
                positions.add((float) vertex.x);
                positions.add((float) vertex.y);
                positions.add((float) vertex.z);
                uvs.add((float) vertex.u);
                uvs.add((float) vertex.v);
                normals.add((float) vertex.nX);
                normals.add((float) vertex.nY);
                normals.add((float) vertex.nZ);
            }
            BTMesh mesh;
            if (getSkeleton().isPresent()){
                BoneMFSkeleton skeleton = getSkeleton().get();
                List<Integer> boneIds = new ArrayList<>();
                List<Float> boneWeights = new ArrayList<>();
                for (BoneMFVertex vertex : vertices) {
                    int size = vertex.boneWeights.size();

                    for (int i = 0; i < MAX_WEIGHTS; i++) {
                        if (i < size) {
                            Tuple<String, Double> weight = vertex.boneWeights.get(i);
                            boneWeights.add(weight.getB().floatValue());
                            boneIds.add(skeleton.getBoneId(weight.getA()));
                        } else {
                            boneWeights.add(0.0f);
                            boneIds.add(0);
                        }
                    }
                }
                mesh = new BTAnimatedMesh(meshNode.getName(), Utils.listToArray(positions),
                        Utils.listToArray(uvs), Utils.listToArray(normals), Utils.listIntToArray(triangles),
                        Utils.listToArray(boneWeights), Utils.listIntToArray(boneIds));
            } else {
                mesh = new BTMesh(meshNode.getName(), Utils.listToArray(positions),
                        Utils.listToArray(uvs), Utils.listToArray(normals), Utils.listIntToArray(triangles));
            }
            meshes.add(mesh);
        }
        return meshes;
    }

    public BoneMFNode getRootNode() {
        return rootNode;
    }
}
