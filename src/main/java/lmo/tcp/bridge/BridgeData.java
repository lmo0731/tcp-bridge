/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.tcp.bridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author LMO
 */
public class BridgeData {

    public byte dataType;
    public int srcId;
    public int srcPort;
    public int dstId;
    public int dstPort;
    public int dataLen;
    public byte[] data;

    public static final byte TYPE_START = 0;
    public static final byte TYPE_CLOSE_REQ = 1;
    public static final byte TYPE_CLOSE_RES = 2;
    public static final byte TYPE_REQ = 3;
    public static final byte TYPE_RES = 4;

    public void write(OutputStream out) throws IOException {
        writeByte(out, (byte) '$');
        writeByte(out, (byte) '$');
        writeByte(out, dataType);
        writeInt(out, srcId);
        writeInt(out, srcPort);
        writeInt(out, dstId);
        writeInt(out, dstPort);
        writeInt(out, dataLen);
        writeByte(out, (byte) '@');
        writeByte(out, (byte) '@');
        out.write(data);
    }

    public static BridgeData read(InputStream in) throws IOException {
        BridgeData b = new BridgeData();
        byte header1 = readByte(in);
        if (header1 != '$') {
            throw new IOException("protocol error");
        }
        byte header2 = readByte(in);
        if (header2 != '$') {
            throw new IOException("protocol error");
        }
        b.dataType = readByte(in);
        b.srcId = readInt(in);
        b.srcPort = readInt(in);
        b.dstId = readInt(in);
        b.dstPort = readInt(in);
        b.dataLen = readInt(in);
        byte footer1 = readByte(in);
        if (footer1 != '@') {
            throw new IOException("protocol error");
        }
        byte footer2 = readByte(in);
        if (footer2 != '@') {
            throw new IOException("protocol error");
        }
        byte[] buffer = new byte[b.dataLen];
        int l = in.read(buffer);
        if (l == -1) {
            throw new IOException("end of connection");
        }
        b.data = Arrays.copyOf(buffer, l);
        return b;
    }

//    int checkSum() {
//        return 0x3722;
//    }
    public static void writeByte(OutputStream out, byte b) throws IOException {
        out.write(new byte[]{b});
    }

    public static void writeInt(OutputStream out, int b) throws IOException {
        out.write(new byte[]{(byte) ((b >> 8) & 0xff), (byte) (b & 0xff)});
    }

    public static byte readByte(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) {
            throw new IOException("end of connection");
        }
        return (byte) (b & 0x000ff);
    }

    public static int readInt(InputStream in) throws IOException {
        return ((readByte(in) & 0xff) << 8) | (readByte(in) & 0xff);
    }

    @Override
    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            this.write(baos);
        } catch (IOException ex) {
        }
        return String.format("type: %d, src: %d %d, dst: %d %d, len: %d%s",
                dataType, srcId, srcPort,
                dstId, dstPort, dataLen,
                ""
                + HexDump.dumpHexString(baos.toByteArray())
        );
    }

}
