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
    public int dataSeq;
    public int dataLen;
    public byte[] data;

    public static final byte TYPE_START = 0;
    public static final byte TYPE_CLOSE_REQ = 1;
    public static final byte TYPE_CLOSE_RES = 2;
    public static final byte TYPE_REQ = 3;
    public static final byte TYPE_RES = 4;
    public static final byte TYPE_OPEN_REQ = 5;
    public static final byte TYPE_OPEN_RES = 6;

    public void write(OutputStream out) throws IOException {
        writeByte(out, (byte) '$');
        writeByte(out, (byte) '$');
        writeByte(out, dataType);
        writeInt(out, srcId);
        writeInt(out, srcPort);
        writeInt(out, dstId);
        writeInt(out, dstPort);
        writeInt(out, dataSeq);
        writeInt(out, dataLen);
        writeByte(out, (byte) '@');
        writeByte(out, (byte) '@');
        out.write(Arrays.copyOf(data, dataLen));
    }

    public static BridgeData read(InputStream in) throws IOException, BridgeDataException {
        BridgeData b = new BridgeData();
        byte header1 = readByte(in);
        if (header1 != '$') {
            throw new BridgeDataException("protocol error $" + header1);
        }
        byte header2 = readByte(in);
        if (header2 != '$') {
            throw new BridgeDataException("protocol error $$ " + header2);
        }
        b.dataType = readByte(in);
        b.srcId = readInt(in);
        b.srcPort = readInt(in);
        b.dstId = readInt(in);
        b.dstPort = readInt(in);
        b.dataSeq = readInt(in);
        b.dataLen = readInt(in);
        byte footer1 = readByte(in);
        if (footer1 != '@') {
            throw new BridgeDataException("protocol error @ " + footer1);
        }
        byte footer2 = readByte(in);
        if (footer2 != '@') {
            throw new BridgeDataException("protocol error @@ " + footer2);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int offset = 0;
        int len = b.dataLen;
        b.data = new byte[b.dataLen];
        while (len > 0) {
            int r = in.read(b.data, offset, len);
            if (r == -1) {
                throw new IOException("end of connection");
            }
            offset += r;
            len -= r;
        }
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
        return String.format("type: %s, src: %d %d, dst: %d %d, seq: %d, len: %d%s",
                typeToStr(dataType), srcId, srcPort,
                dstId, dstPort, dataSeq, dataLen,
                ""
        //                + HexDump.dumpHexString(baos.toByteArray())
        );
    }

    String typeToStr(byte t) {
        String ret = "_";
        if (t == TYPE_CLOSE_REQ) {
            ret = ">C";
        }
        if (t == TYPE_CLOSE_RES) {
            ret = "<C";
        }
        if (t == TYPE_OPEN_REQ) {
            ret = ">O";
        }
        if (t == TYPE_OPEN_RES) {
            ret = "<O";
        }
        if (t == TYPE_REQ) {
            ret = ">D";
        }
        if (t == TYPE_RES) {
            ret = "<D";
        }
        if (t == TYPE_START) {
            ret = "S";
        }
        return ret;
    }

}
