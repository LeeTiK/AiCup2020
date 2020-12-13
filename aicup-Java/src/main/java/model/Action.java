package model;

import util.StreamUtilBAD;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

public class Action {
    private java.util.Map<Integer, model.EntityAction> entityActions;
    public java.util.Map<Integer, model.EntityAction> getEntityActions() { return entityActions; }
    public void setEntityActions(java.util.Map<Integer, model.EntityAction> entityActions) { this.entityActions = entityActions; }
    public Action() {}
    public Action(java.util.Map<Integer, model.EntityAction> entityActions) {
        this.entityActions = entityActions;
    }
    public static Action readFrom(ByteBuffer byteBuffer) throws java.io.IOException {
        Action result = new Action();
        int entityActionsSize = byteBuffer.getInt();
        result.entityActions = new java.util.HashMap<>(entityActionsSize);
        for (int i = 0; i < entityActionsSize; i++) {
            int entityActionsKey;
            entityActionsKey = byteBuffer.getInt();
            model.EntityAction entityActionsValue;
            entityActionsValue = model.EntityAction.readFrom(byteBuffer);
            result.entityActions.put(entityActionsKey, entityActionsValue);
        }
        return result;
    }
    public void writeTo(DataOutputStream stream) throws java.io.IOException {
        stream.writeInt(entityActions.size());
        for (java.util.Map.Entry<Integer, model.EntityAction> entityActionsEntry : entityActions.entrySet()) {
            int entityActionsKey = entityActionsEntry.getKey();
            model.EntityAction entityActionsValue = entityActionsEntry.getValue();
            StreamUtilBAD.writeInt(stream, entityActionsKey);
            entityActionsValue.writeTo(stream);
        }
    }
}
