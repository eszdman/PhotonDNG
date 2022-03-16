package dngCamera.parser;
import data.Rational;
import util.Log.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

class TagParser {
    static HashMap<Integer,TIFFTag> parse(ByteBuffer wrap) {
        short tagCount = wrap.getShort();
        HashMap<Integer,TIFFTag> tags = new HashMap<>(tagCount);

        for (int tagNum = 0; tagNum < tagCount; tagNum++) {
            int tag = wrap.getShort() & 0xFFFF;
            int type = wrap.getShort() & 0xFFFF;
            int elementCount = wrap.getInt();
            int elementSize = TIFF.TYPE_SIZES.get(type);

            byte[] buffer = new byte[Math.max(4, elementCount * elementSize)];
            if (buffer.length == 4) {
                wrap.get(buffer);
            } else {
                int dataPos = wrap.getInt();
                independentMove(wrap, dataPos).get(buffer);
            }

            ByteBuffer valueWrap = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
            Object[] values = new Object[elementCount];
            for (int elementNum = 0; elementNum < elementCount; elementNum++) {
                if (type == TIFF.TYPE_Byte || type == TIFF.TYPE_Undef) {
                    values[elementNum] = valueWrap.get();
                } else if (type == TIFF.TYPE_String) {
                    values[elementNum] = (char) valueWrap.get();
                } else if (type == TIFF.TYPE_UInt_16) {
                    values[elementNum] = valueWrap.getShort() & 0xFFFF;
                } else if (type == TIFF.TYPE_UInt_32) {
                    values[elementNum] = valueWrap.getInt();
                } else if (type == TIFF.TYPE_UFrac) {
                    values[elementNum] = new Rational(valueWrap.getInt(), valueWrap.getInt());
                } else if (type == TIFF.TYPE_Frac) {
                    values[elementNum] = new Rational(valueWrap.getInt(), valueWrap.getInt());
                } else if (type == TIFF.TYPE_Double) {
                    values[elementNum] = valueWrap.getDouble();
                }
            }

            tags.put(tag, new TIFFTag(type, values));
        }

        return tags;
    }

    private static ByteBuffer independentMove(ByteBuffer wrap, int position) {
        return wrap.duplicate().order(ByteOrder.LITTLE_ENDIAN).position(position);
    }
}
