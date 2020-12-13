package model;

import util.FinalProtocol;
import util.StreamUtilBAD;

import java.nio.ByteBuffer;

public abstract class DebugData {
    public abstract void writeTo(java.io.OutputStream stream) throws java.io.IOException;
    public static DebugData readFrom(java.io.InputStream stream) throws java.io.IOException {
        switch (StreamUtilBAD.readInt(stream)) {
            case Log.TAG:
                return Log.readFrom(stream);
            case Primitives.TAG:
                return Primitives.readFrom(stream);
            case PlacedText.TAG:
                return PlacedText.readFrom(stream);
            default:
                throw new java.io.IOException("Unexpected tag value");
        }
    }

    public static class Log extends DebugData {
        public static final int TAG = 0;
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Log() {}
        public Log(String text) {
            this.text = text;
        }
        public static Log readFrom(java.io.InputStream stream) throws java.io.IOException {
            Log result = new Log();
            result.text = StreamUtilBAD.readString(stream);
            return result;
        }
        @Override
        public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
            StreamUtilBAD.writeInt(stream, TAG);
            StreamUtilBAD.writeString(stream, text);
        }
    }

    public static class Primitives extends DebugData {
        public static final int TAG = 1;
        private model.ColoredVertex[] vertices;
        public model.ColoredVertex[] getVertices() { return vertices; }
        public void setVertices(model.ColoredVertex[] vertices) { this.vertices = vertices; }
        private model.PrimitiveType primitiveType;
        public model.PrimitiveType getPrimitiveType() { return primitiveType; }
        public void setPrimitiveType(model.PrimitiveType primitiveType) { this.primitiveType = primitiveType; }
        public Primitives() {}
        public Primitives(model.ColoredVertex[] vertices, model.PrimitiveType primitiveType) {
            this.vertices = vertices;
            this.primitiveType = primitiveType;
        }
        public static Primitives readFrom(ByteBuffer inputByteBuffer) throws java.io.IOException {
            Primitives result = new Primitives();
            result.vertices = new model.ColoredVertex[inputByteBuffer.getInt()];
            for (int i = 0; i < result.vertices.length; i++) {
                result.vertices[i] = model.ColoredVertex.readFrom(inputByteBuffer);
            }
            switch (inputByteBuffer.getInt()) {
            case 0:
                result.primitiveType = model.PrimitiveType.LINES;
                break;
            case 1:
                result.primitiveType = model.PrimitiveType.TRIANGLES;
                break;
            default:
                throw new java.io.IOException("Unexpected tag value");
            }
            return result;
        }
        @Override
        public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
            StreamUtilBAD.writeInt(stream, TAG);
            StreamUtilBAD.writeInt(stream, vertices.length);
            for (model.ColoredVertex verticesElement : vertices) {
                verticesElement.writeTo(stream);
            }
            StreamUtilBAD.writeInt(stream, primitiveType.tag);
        }
    }

    public static class PlacedText extends DebugData {
        public static final int TAG = 2;
        private model.ColoredVertex vertex;
        public model.ColoredVertex getVertex() { return vertex; }
        public void setVertex(model.ColoredVertex vertex) { this.vertex = vertex; }
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        private float alignment;
        public float getAlignment() { return alignment; }
        public void setAlignment(float alignment) { this.alignment = alignment; }
        private float size;
        public float getSize() { return size; }
        public void setSize(float size) { this.size = size; }
        public PlacedText() {}
        public PlacedText(model.ColoredVertex vertex, String text, float alignment, float size) {
            this.vertex = vertex;
            this.text = text;
            this.alignment = alignment;
            this.size = size;
        }
        public static PlacedText readFrom(ByteBuffer byteBuffer) throws java.io.IOException {
            PlacedText result = new PlacedText();
            result.vertex = model.ColoredVertex.readFrom(byteBuffer);
            result.text = FinalProtocol.decoderStringByteBufferUTF8(byteBuffer);
            result.alignment = byteBuffer.getFloat();
            result.size =byteBuffer.getFloat();
            return result;
        }
        @Override
        public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
            StreamUtilBAD.writeInt(stream, TAG);
            vertex.writeTo(stream);
            StreamUtilBAD.writeString(stream, text);
            StreamUtilBAD.writeFloat(stream, alignment);
            StreamUtilBAD.writeFloat(stream, size);
        }
    }
}
