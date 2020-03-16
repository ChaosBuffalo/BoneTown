package com.chaosbuffalo.bonetown.core.bonemf;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.utils.ByteBufferBackedInputStream;
import com.upokecenter.cbor.CBORObject;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4d;
import org.joml.Vector4d;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class BoneMFModelLoader {

    public static InputStream asInputStream(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            // use heap buffer; no array is created; only the reference is used
            return new ByteArrayInputStream(buffer.array());
        }
        return new ByteBufferBackedInputStream(buffer);
    }

    private static Vector4d vec4dFromCBOR(CBORObject cbor){
        return new Vector4d(cbor.get(0).AsDouble(), cbor.get(1).AsDouble(),
                cbor.get(2).AsDouble(), cbor.get(3).AsDouble());
    }

    private static BoneMFVertex parseVertex(CBORObject cbor){
        double nX = cbor.get("nX").AsDouble();
        double nY = cbor.get("nY").AsDouble();
        double nZ = cbor.get("nZ").AsDouble();
        double x = cbor.get("x").AsDouble();
        double y = cbor.get("y").AsDouble();
        double z = cbor.get("z").AsDouble();
        double u = cbor.get("u").AsDouble();
        double v = 1.0 - cbor.get("v").AsDouble();
        BoneMFVertex vertex = new BoneMFVertex(x, y, z, nX, nY, nZ, u, v);
        CBORObject boneWeights = cbor.get("boneWeights");
        if (!boneWeights.isNull()){
            for (CBORObject boneWeight : boneWeights.getValues()){
                String boneName = boneWeight.get("boneName").AsString();
                double weight = boneWeight.get("weight").AsDouble();
                vertex.addBoneWeight(boneName, weight);
            }
        }
        return vertex;
    }

    private static BoneMFAttribute parseAttribute(CBORObject cbor, BoneMFNode owner){
        String attrType = cbor.get("type").AsString();
        BoneMFAttribute.AttributeTypes type = BoneMFAttribute.getAttributeTypeFromString(attrType);
        BoneMFAttribute attr;
        switch (type){
            case MESH:
            {
                CBORObject mesh = cbor.get("mesh");
                CBORObject triangles = mesh.get("triangles");
                BoneMFMeshAttribute meshAttr = new BoneMFMeshAttribute(owner);
                for (int i = 0; i < triangles.size() / 3; i++){
                    int realIndex = i * 3;
                    meshAttr.addTriangle(triangles.get(realIndex).AsInt32(),
                            triangles.get(realIndex + 1).AsInt32(),
                            triangles.get(realIndex + 2).AsInt32());
                }
                for (CBORObject vert : mesh.get("vertices").getValues()){
                    meshAttr.addVertex(parseVertex(vert));
                }

                attr = meshAttr;
                break;
            }
            case SKELETON:
            case NULL: {
                attr = new BoneMFAttribute(type, owner);
                break;
            }
            default:
                attr = null;
                BoneTown.LOGGER.error("Failed to parse attribute: %s", cbor.toString());
        }
        return attr;
    }

    private static Matrix4d parseMatrix(CBORObject cborMat){
        Vector4d[] rows = new Vector4d[4];
        int i = 0;
        for (CBORObject row : cborMat.getValues()){
            rows[i] = parseVector(row);
            i++;
        }
        Matrix4d mat = new Matrix4d();
        mat.m00(rows[0].x);
        mat.m10(rows[0].y);
        mat.m20(rows[0].z);
        mat.m30(rows[3].x);
        mat.m01(rows[1].x);
        mat.m11(rows[1].y);
        mat.m21(rows[1].z);
        mat.m31(rows[3].y);
        mat.m02(rows[2].x);
        mat.m12(rows[2].y);
        mat.m22(rows[2].z);
        mat.m32(rows[3].z);
        mat.m03(rows[0].w);
        mat.m13(rows[1].w);
        mat.m23(rows[2].w);
        mat.m33(rows[3].w);
        return mat;
    }

    private static Vector4d parseVector(CBORObject vec){
        return new Vector4d(vec.get(0).AsDouble(), vec.get(1).AsDouble(),
                vec.get(2).AsDouble(), vec.get(3).AsDouble());
    }

    private static BoneMFNode parseNode(CBORObject node){
        String nodeName = node.get("name").AsString();
        BoneTown.LOGGER.info("CBOR Found node: {}", nodeName);
        CBORObject children = node.get("children");
        BoneMFNode mfNode = new BoneMFNode(nodeName);
        String inheritType = node.get("inheritType").AsString();
        mfNode.setInheritType(BoneMFNode.getInheritTypeFromString(inheritType));
        mfNode.setPostRotation(vec4dFromCBOR(node.get("postRotation")));
        mfNode.setPreRotation(vec4dFromCBOR(node.get("preRotation")));
        mfNode.setRotation(vec4dFromCBOR(node.get("rotation")));
        mfNode.setRotationOffset(vec4dFromCBOR(node.get("rotationOffset")));
        mfNode.setRotationPivot(vec4dFromCBOR(node.get("rotationPivot")));
        mfNode.setScaling(vec4dFromCBOR(node.get("scaling")));
        mfNode.setScalingOffset(vec4dFromCBOR(node.get("scalingOffset")));
        mfNode.setScalingPivot(vec4dFromCBOR(node.get("scalingPivot")));
        mfNode.setTranslation(vec4dFromCBOR(node.get("translation")));
        mfNode.setGlobalTransform(parseMatrix(node.get("global")));

        CBORObject attributes = node.get("attributes");
        if (!attributes.isNull()){
            for (CBORObject attr : attributes.getValues()){
                BoneMFAttribute boneAttr = parseAttribute(attr, mfNode);
                mfNode.addAttribute(boneAttr);
            }
        }

        if (!children.isNull()){
            for (CBORObject child : children.getValues()){
                BoneMFNode childNode = parseNode(child);
                childNode.setParent(mfNode);
                mfNode.addChild(childNode);
            }
        }
        BoneTown.LOGGER.info(mfNode.toString());
        return mfNode;
    }

    public static BoneMFModel load(ByteBuffer resource, ResourceLocation name)
            throws Exception {

        InputStream stream = asInputStream(resource);
        CBORObject cbor = CBORObject.Read(stream);
        CBORObject nodes = cbor.get("nodes");
        BoneMFNode root = new BoneMFNode(name.toString());
        for (CBORObject node : nodes.getValues()){
            BoneMFNode mfNode = parseNode(node);
            root.addChild(mfNode);
        }
        BoneMFNode firstChild = root.getChildren().get(0);
        root.setInheritType(firstChild.getInheritType());
        return new BoneMFModel(root);
    }

}
