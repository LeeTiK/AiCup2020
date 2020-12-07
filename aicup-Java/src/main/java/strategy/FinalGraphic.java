package strategy;

import model.*;

public class FinalGraphic {

    final static public Color COLOR_RED = new Color(1.0f, 0.0f, 0.0f, 0.7f);
    final static public Color COLOR_BLUE = new Color(0.0f, 0.0f, 1.0f, 0.7f);
    final static public Color COLOR_GREEN = new Color(0.0f, 1.0f, 0.0f, 0.7f);
    final static public Color COLOR_WHITE = new Color(1.0f, 1.0f, 1.0f, 0.7f);
    final static public Color COLOR_BLACK = new Color(0.0f, 0.0f, 0.0f, 0.7f);

    static public void sendSquare(DebugInterface debugInterface, Vec2Int start, int size, Color color) {

        ColoredVertex[] coloredVertices1 = new ColoredVertex[3];
        coloredVertices1[0] = new ColoredVertex(start.getVec2Float(), new Vec2Float(0, 0), color);
        coloredVertices1[1] = new ColoredVertex(start.add(size, 0).getVec2Float(), new Vec2Float(0, 0), color);
        coloredVertices1[2] = new ColoredVertex(start.add(0, size).getVec2Float(), new Vec2Float(0, 0), color);

        DebugData.Primitives primitives = new DebugData.Primitives(coloredVertices1, PrimitiveType.TRIANGLES);

        DebugCommand.Add add = new DebugCommand.Add(primitives);
        debugInterface.send(add);

        ColoredVertex[] coloredVertices2 = new ColoredVertex[3];
        coloredVertices2[0] = new ColoredVertex(start.add(size, size).getVec2Float(), new Vec2Float(0, 0), color);
        coloredVertices2[1] = new ColoredVertex(start.add(size, 0).getVec2Float(), new Vec2Float(0, 0), color);
        coloredVertices2[2] = new ColoredVertex(start.add(0, size).getVec2Float(), new Vec2Float(0, 0), color);

        DebugData.Primitives primitives1 = new DebugData.Primitives(coloredVertices2, PrimitiveType.TRIANGLES);

        DebugCommand.Add add1 = new DebugCommand.Add(primitives1);
        debugInterface.send(add1);
    }

    static public void sendText(DebugInterface debugInterface, Vec2Int start, int size, String text) {
        sendText(debugInterface, start.getVec2Float(), size, text);
    }

    static public void sendText(DebugInterface debugInterface, Vec2Float start, int size, String text) {
        ColoredVertex coloredVertices1 = new ColoredVertex(start, new Vec2Int(0, 0).getVec2Float(), COLOR_BLACK);

        DebugData.PlacedText placedText = new DebugData.PlacedText(coloredVertices1, text, 0f, size);

        DebugCommand.Add add = new DebugCommand.Add(placedText);
        debugInterface.send(add);
    }

    static public Color getColorDinamic(int sum, int max) {

        float a = (1.0f * sum) / max;

        // System.out.println("sum: " + sum + " max: " + max + " a: " + a);
        if (a > 1.0f) {
            a = 1.0f;

            return new Color(0.0f, 1.0f, 0, 0.7f);
        }

        Color color = new Color(a, 1.0f - a, 0, 0.7f);

        return color;
    }

    static public Color getColorDinamicRED(int sum, int max) {

        float a = (1.0f * sum) / max;

        // System.out.println("sum: " + sum + " max: " + max + " a: " + a);
        if (a > 1.0f) {
            a = 1.0f;

            return new Color(1.0f, 0.0f, 0, 0.7f);
        }

        Color color = new Color(a, 0, 0, 0.7f);

        return color;
    }

    static public Color getColorDinamicBLUE(int sum, int max) {

        float a = (1.0f * sum) / max;

        // System.out.println("sum: " + sum + " max: " + max + " a: " + a);
        if (a > 1.0f) {
            a = 1.0f;

            return new Color(0, 0.0f, 1.0f, 0.7f);
        }

        Color color = new Color(0, 0, a, 0.7f);

        return color;
    }
}


