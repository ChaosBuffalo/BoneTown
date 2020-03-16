package com.chaosbuffalo.bonetown.core.bonemf;

import com.chaosbuffalo.bonetown.BoneTown;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class BoneMFNode {

    private final List<BoneMFNode> children;

    private BoneMFNode parent;

    private final String name;

    private final List<BoneMFAttribute> attributes;

    private Vector4d translation;
    private Vector4d rotation;
    private Vector4d preRotation;
    private Vector4d postRotation;
    private Vector4d scaling;
    private Vector4d scalingOffset;
    private Vector4d scalingPivot;
    private Vector4d rotationOffset;
    private Vector4d rotationPivot;
    private Matrix4d globalTransform;

    private Matrix4d _globalTransform;

    public enum InheritTypes {
        UNKNOWN,
        RRSS,
        RSRS,
        RRS
    }

    public static InheritTypes getInheritTypeFromString(String typeIn){
        switch (typeIn){
            case "RSrs":
                return InheritTypes.RSRS;
            case "RrSs":
                return InheritTypes.RRSS;
            case "Rrs":
                return InheritTypes.RRS;
            default:
                return InheritTypes.UNKNOWN;
        }
    }

    private InheritTypes inheritType;

    @Override
    public String toString() {
        StringBuilder attrs  = new StringBuilder();
        Iterator<BoneMFAttribute> iter = attributes.iterator();
        while(iter.hasNext())
        {
            attrs.append(iter.next().toString());
            if(iter.hasNext()){
                attrs.append(",");
            }
        }
        return String.format("<BoneMFNode name='%s' " +
                "translation='%s' rotation='%s' scaling='%s' numChildren='%d' attributes='%s'>", getName(),
                getTranslation().toString(), getRotation().toString(),
                getScaling().toString(), getChildren().size(), attrs.toString());
    }

    private boolean dirty;

    public BoneMFNode(String name){
        this.name = name;
        this.children = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.dirty = false;
        this.parent = null;
        this._globalTransform = null;
        this.inheritType = InheritTypes.UNKNOWN;
        this.setTranslation(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setRotation(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setScaling(new Vector4d(1.0, 1.0, 1.0, 1.0));
        this.setPreRotation(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setPostRotation(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setScalingPivot(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setRotationPivot(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setRotationPivot(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setRotationOffset(new Vector4d(0.0, 0.0, 0.0, 1.0));
        this.setScalingOffset(new Vector4d(0.0, 0.0, 0.0, 1.0));
    }

    public void setGlobalTransform(Matrix4d globalTransform) {
        this.globalTransform = globalTransform;
    }

    public Matrix4d getGlobalTransform() {
        return globalTransform;
    }

    @Nullable
    public <T extends BoneMFAttribute> T getAttributeForType(BoneMFAttribute.AttributeTypes type,
                                                             Class<T> clazz){
        for (BoneMFAttribute attr : getAttributes()){
            if (attr.getType() == type && clazz.isInstance(attr)){
                return (T)attr;
            }
        }
        return null;
    }

    @Nullable
    public BoneMFMeshAttribute getMesh(){
        return getAttributeForType(BoneMFAttribute.AttributeTypes.MESH, BoneMFMeshAttribute.class);
    }

    public boolean hasAttribute(BoneMFAttribute.AttributeTypes type){
        for (BoneMFAttribute attr : getAttributes()){
            if (attr.getType() == type){
                return true;
            }
        }
        return false;
    }

    @Nullable
    public BoneMFNode getNodeByName(String name){
        return getNodeWithCondition((BoneMFNode node) -> node.getName().equals(name));
    }

    public List<BoneMFNode> getNodesWithCondition(Function<BoneMFNode, Boolean> condition){
        List<BoneMFNode> ret = new ArrayList<>();
        getNodesWithCondition(condition, ret);
        return ret;
    }

    public List<BoneMFNode> getNodesOfType(BoneMFAttribute.AttributeTypes type){
        return getNodesWithCondition((BoneMFNode node) -> node.hasAttribute(type));
    }

    private void getNodesWithCondition(Function<BoneMFNode, Boolean> condition, List<BoneMFNode> acc){
        if (condition.apply(this)){
            acc.add(this);
        }
        for (BoneMFNode child : getChildren()){
            child.getNodesWithCondition(condition, acc);
        }
    }

    @Nullable
    public BoneMFNode getNodeWithCondition(Function<BoneMFNode, Boolean> condition){
        BoneMFNode result = null;
        if (condition.apply(this)){
            result = this;
        } else {
            for (BoneMFNode child : getChildren()){
                result = child.getNodeWithCondition(condition);
                if (result != null){
                    break;
                }
            }
        }
        return result;
    }

    @Nullable
    public BoneMFNode getNodeWithAttributeType(BoneMFAttribute.AttributeTypes type){
        return getNodeWithCondition((BoneMFNode node) -> node.hasAttribute(type));
    }

    public void setInheritType(InheritTypes inheritType) {
        this.inheritType = inheritType;
        this.markDirty();
    }

    public InheritTypes getInheritType() {
        return inheritType;
    }

    public List<BoneMFAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(BoneMFAttribute attribute){
        this.attributes.add(attribute);
    }

    public void addChild(BoneMFNode node){
        this.children.add(node);
    }

    public List<BoneMFNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public Vector4d getTranslation() {
        return translation;
    }

    public Vector4d getPostRotation() {
        return postRotation;
    }

    public Vector4d getPreRotation() {
        return preRotation;
    }

    public BoneMFNode getParent() {
        return parent;
    }

    public Vector4d getRotation() {
        return rotation;
    }

    public Vector4d getRotationOffset() {
        return rotationOffset;
    }

    public Vector4d getRotationPivot() {
        return rotationPivot;
    }

    public Vector4d getScaling() {
        return scaling;
    }

    public Vector4d getScalingOffset() {
        return scalingOffset;
    }

    public Vector4d getScalingPivot() {
        return scalingPivot;
    }

    public void setPreRotation(Vector4d preRotation) {
        this.preRotation = preRotation;
        markDirty();
    }

    private void markDirty(){
        this.dirty = true;
        for (BoneMFNode child : this.getChildren()){
            child.markDirty();
        }
    }

    private Vector3d fromVec4d(Vector4d otherVec){
        return new Vector3d(otherVec.x, otherVec.y, otherVec.z);
    }

    private Vector4d toVec4d(Vector3d otherVec) { return new Vector4d(otherVec.x, otherVec.y, otherVec.z, 1.0); }

    public Matrix4d calculateGlobalTransform(Vector4d translation, Vector4d rotation, Vector4d scale){
        Matrix4d mTranslation = new Matrix4d().translateLocal(fromVec4d(translation));
        Matrix4d mRotation = new Matrix4d().rotateAffineXYZ(rotation.x, rotation.y, rotation.z);
        Matrix4d mPostRotation = new Matrix4d().rotateAffineXYZ(getPostRotation().x, getPostRotation().y, getPostRotation().z);
        Matrix4d mPreRotation = new Matrix4d().rotateAffineXYZ(getPreRotation().x, getPreRotation().y, getPreRotation().z);
        Matrix4d mScaling = new Matrix4d().scale(fromVec4d(scale));
        Matrix4d mScalingOffset = new Matrix4d().translateLocal(fromVec4d(getScalingOffset()));
        Matrix4d mScalingPivot = new Matrix4d().translateLocal(fromVec4d(getScalingPivot()));
        Matrix4d mRotationOffset = new Matrix4d().translateLocal(fromVec4d(getRotationOffset()));
        Matrix4d mRotationPivot = new Matrix4d().translateLocal(fromVec4d(getRotationPivot()));

        Matrix4d parentGlobal;
        if (getParent() != null){
            parentGlobal = this.getParent().calculateGlobalTransform();
        } else {
            parentGlobal = new Matrix4d();
        }

        Quaterniond rotQuat = new Quaterniond();
        parentGlobal.getUnnormalizedRotation(rotQuat);
        Vector3d eulerRot = new Vector3d();
        rotQuat.getEulerAnglesXYZ(eulerRot);

        Matrix4d parentGlobalRot = new Matrix4d().rotateAffineXYZ(eulerRot.x, eulerRot.y, eulerRot.z);
        Matrix4d localRotMat = new Matrix4d().mulLocalAffine(mPreRotation).mulLocalAffine(mRotation)
                .mulLocalAffine(mPostRotation);

        Vector3d parentTrans = new Vector3d();
        parentGlobal.getTranslation(parentTrans);
        Matrix4d parentGlobalTrans = new Matrix4d().translateLocal(parentTrans);
        Matrix4d parentGlobalRotScale = new Matrix4d(parentGlobalTrans).invertAffine().mulLocalAffine(parentGlobal);
        Matrix4d parentGlobalScale = new Matrix4d(parentGlobalRot).invertAffine().mulLocalAffine(parentGlobalRotScale);
        Matrix4d localScaling = new Matrix4d(mScaling);

        Matrix4d globalRotScale;
        switch (getInheritType()){
            case RRSS: {
                globalRotScale = new Matrix4d(parentGlobalRot).mulLocalAffine(localRotMat)
                        .mulLocalAffine(parentGlobalScale).mulLocalAffine(localScaling);
                break;
            }
            case RSRS: {
                globalRotScale = new Matrix4d(parentGlobalRot).mulLocalAffine(parentGlobalScale)
                        .mulLocalAffine(localRotMat).mulLocalAffine(localScaling);
                break;
            }
            case RRS: {
                Matrix4d parentLocalScaling = new Matrix4d();
                Vector4d parentScaling = getParent().getScaling();
                parentLocalScaling.scale(fromVec4d(parentScaling));
                Matrix4d parentGlobalScalingNoLocal = parentGlobalScale.mulLocalAffine(parentLocalScaling.invertAffine());
                globalRotScale = new Matrix4d(parentGlobalRot).mulLocalAffine(localRotMat)
                        .mulLocalAffine(parentGlobalScalingNoLocal).mulLocalAffine(localScaling);
                break;
            }
            case UNKNOWN:
            default: {
                BoneTown.LOGGER.info("Unknown inheritance type, rotation probably broken");
                globalRotScale = new Matrix4d();
                break;
            }
        }
        Matrix4d mRotationPivotInverse = new Matrix4d(mRotationPivot).invertAffine();
        Matrix4d mScalingPivotInverse = new Matrix4d(mScalingPivot).invertAffine();


        Matrix4d localTransform = new Matrix4d(mTranslation).mulLocalAffine(mRotationOffset).mulLocalAffine(mRotationPivot)
                .mulLocalAffine(mPreRotation).mulLocalAffine(mRotation).mulLocalAffine(mPostRotation)
                .mulLocalAffine(mRotationPivotInverse).mulLocalAffine(mScalingOffset)
                .mulLocalAffine(mScalingPivot).mulLocalAffine(mScaling).mulLocalAffine(mScalingPivotInverse);
        Vector3d localTranslateWithPivotsOffsets = new Vector3d();
        localTransform.getTranslation(localTranslateWithPivotsOffsets);

        Vector4d globalTranslation = parentGlobal.transformAffine(toVec4d(localTranslateWithPivotsOffsets));
        Matrix4d mGlobalTranslation = new Matrix4d();
        mGlobalTranslation.translateLocal(fromVec4d(globalTranslation));
        return mGlobalTranslation.mulLocalAffine(globalRotScale);
    }

    public Matrix4d calculateGlobalTransform(){
        if (_globalTransform == null || dirty) {
            _globalTransform = calculateGlobalTransform(getTranslation(),
                    getRotation(), getScaling());
            dirty = false;
        }
        return _globalTransform;
    }

    public void setParent(BoneMFNode parent) {
        this.parent = parent;
        markDirty();
    }

    public void setPostRotation(Vector4d postRotation) {
        this.postRotation = postRotation;
        markDirty();
    }

    public void setRotation(Vector4d rotation) {
        this.rotation = rotation;
        markDirty();
    }

    public void setRotationOffset(Vector4d rotationOffset) {
        this.rotationOffset = rotationOffset;
        markDirty();
    }

    public void setRotationPivot(Vector4d rotationPivot) {
        this.rotationPivot = rotationPivot;
        markDirty();
    }

    public void setScaling(Vector4d scaling) {
        this.scaling = scaling;
        markDirty();
    }

    public void setScalingOffset(Vector4d scalingOffset) {
        this.scalingOffset = scalingOffset;
        markDirty();
    }

    public void setScalingPivot(Vector4d scalingPivot) {
        this.scalingPivot = scalingPivot;
        markDirty();
    }

    public void setTranslation(Vector4d translation) {
        this.translation = translation;
        markDirty();
    }


}
