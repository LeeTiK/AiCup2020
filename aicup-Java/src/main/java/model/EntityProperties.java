package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class EntityProperties {
    private int size;
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    private int buildScore;
    public int getBuildScore() { return buildScore; }
    public void setBuildScore(int buildScore) { this.buildScore = buildScore; }
    private int destroyScore;
    public int getDestroyScore() { return destroyScore; }
    public void setDestroyScore(int destroyScore) { this.destroyScore = destroyScore; }
    private boolean canMove;
    public boolean isCanMove() { return canMove; }
    public void setCanMove(boolean canMove) { this.canMove = canMove; }
    private int populationProvide;
    public int getPopulationProvide() { return populationProvide; }
    public void setPopulationProvide(int populationProvide) { this.populationProvide = populationProvide; }
    private int populationUse;
    public int getPopulationUse() { return populationUse; }
    public void setPopulationUse(int populationUse) { this.populationUse = populationUse; }
    private int maxHealth;
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    private int cost;
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    private int sightRange;
    public int getSightRange() { return sightRange; }
    public void setSightRange(int sightRange) { this.sightRange = sightRange; }
    private int resourcePerHealth;
    public int getResourcePerHealth() { return resourcePerHealth; }
    public void setResourcePerHealth(int resourcePerHealth) { this.resourcePerHealth = resourcePerHealth; }
    private model.BuildProperties build;
    public model.BuildProperties getBuild() { return build; }
    public void setBuild(model.BuildProperties build) { this.build = build; }
    private model.AttackProperties attack;
    public model.AttackProperties getAttack() { return attack; }
    public void setAttack(model.AttackProperties attack) { this.attack = attack; }
    private model.RepairProperties repair;
    public model.RepairProperties getRepair() { return repair; }
    public void setRepair(model.RepairProperties repair) { this.repair = repair; }
    public EntityProperties() {}
    public EntityProperties(int size, int buildScore, int destroyScore, boolean canMove, int populationProvide, int populationUse, int maxHealth, int cost, int sightRange, int resourcePerHealth, model.BuildProperties build, model.AttackProperties attack, model.RepairProperties repair) {
        this.size = size;
        this.buildScore = buildScore;
        this.destroyScore = destroyScore;
        this.canMove = canMove;
        this.populationProvide = populationProvide;
        this.populationUse = populationUse;
        this.maxHealth = maxHealth;
        this.cost = cost;
        this.sightRange = sightRange;
        this.resourcePerHealth = resourcePerHealth;
        this.build = build;
        this.attack = attack;
        this.repair = repair;
    }
    public static EntityProperties readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        EntityProperties result = new EntityProperties();
        result.size = inputByteBuffer.getInt();
        result.buildScore = inputByteBuffer.getInt();
        result.destroyScore = inputByteBuffer.getInt();
        result.canMove = FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer);
        result.populationProvide = inputByteBuffer.getInt();
        result.populationUse = inputByteBuffer.getInt();
        result.maxHealth = inputByteBuffer.getInt();
        result.cost =inputByteBuffer.getInt();
        result.sightRange = inputByteBuffer.getInt();
        result.resourcePerHealth = inputByteBuffer.getInt();
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.build = model.BuildProperties.readFrom(inputByteBuffer);
        } else {
            result.build = null;
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.attack = model.AttackProperties.readFrom(inputByteBuffer);
        } else {
            result.attack = null;
        }
        if (FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer)) {
            result.repair = model.RepairProperties.readFrom(inputByteBuffer);
        } else {
            result.repair = null;
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, size);
        StreamUtilBAD.writeInt(stream, buildScore);
        StreamUtilBAD.writeInt(stream, destroyScore);
        StreamUtilBAD.writeBoolean(stream, canMove);
        StreamUtilBAD.writeInt(stream, populationProvide);
        StreamUtilBAD.writeInt(stream, populationUse);
        StreamUtilBAD.writeInt(stream, maxHealth);
        StreamUtilBAD.writeInt(stream, cost);
        StreamUtilBAD.writeInt(stream, sightRange);
        StreamUtilBAD.writeInt(stream, resourcePerHealth);
        if (build == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            build.writeTo(stream);
        }
        if (attack == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            attack.writeTo(stream);
        }
        if (repair == null) {
            StreamUtilBAD.writeBoolean(stream, false);
        } else {
            StreamUtilBAD.writeBoolean(stream, true);
            repair.writeTo(stream);
        }
    }
}
