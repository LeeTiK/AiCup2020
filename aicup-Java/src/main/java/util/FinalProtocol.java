package util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class FinalProtocol {

    public static boolean decoderBooleanByteBuffer(ByteBuffer byteBuffer) {
        byte bool = byteBuffer.get();
        if (bool == 1) return true;
        else return false;
    }

    public static void coderBooleanByteBuffer(ByteBuffer byteBuffer, boolean bool) {
        byteBuffer.put(bool == true ? (byte) 1 : (byte) 0);
    }

    public static String decoderStringByteBufferUTF8(ByteBuffer byteBuffer) {

        int length = byteBuffer.getInt();

        System.out.println("String length: " + length);

        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) bytes[i] = byteBuffer.get();

        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void coderStringByteBufferUTF16(ByteBuffer byteBuffer, String str) {
        int length = str.length();
        // StringBuilder buf = new StringBuilder();

        byteBuffer.putInt(length*2);

        for (int i = 0; i < str.length(); i++)
            byteBuffer.putChar(str.charAt(i));
    }

    public static void coderStringByteBufferUTF8(ByteBuffer byteBuffer, String str) {
        if (str == null) return;
        int length = str.length();

        try {
            byte[] bytes = str.getBytes("UTF-8");
            byteBuffer.putInt(bytes.length);
            // Log.v("Decoder", "str = " + length + " byte " + bytes.length);
            for (int i = 0; i < bytes.length; i++)
                byteBuffer.put(bytes[i]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
