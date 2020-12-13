package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public class AttackProperties {
    private int attackRange;
    public int getAttackRange() { return attackRange; }
    public void setAttackRange(int attackRange) { this.attackRange = attackRange; }
    private int damage;
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    private boolean collectResource;
    public boolean isCollectResource() { return collectResource; }
    public void setCollectResource(boolean collectResource) { this.collectResource = collectResource; }
    public AttackProperties() {}
    public AttackProperties(int attackRange, int damage, boolean collectResource) {
        this.attackRange = attackRange;
        this.damage = damage;
        this.collectResource = collectResource;
    }
    public static AttackProperties readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
        AttackProperties result = new AttackProperties();
        result.attackRange = inputByteBuffer.getInt();
        result.damage = inputByteBuffer.getInt();
        result.collectResource = FinalProtocol.decoderBooleanByteBuffer(inputByteBuffer);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtilBAD.writeInt(stream, attackRange);
        StreamUtilBAD.writeInt(stream, damage);
        StreamUtilBAD.writeBoolean(stream, collectResource);
    }
}
