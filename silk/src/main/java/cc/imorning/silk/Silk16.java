package cc.imorning.silk;

public class Silk16 {

    public native int open(int compression);

    public native int decode(byte[] encoded, short[] lin, int size);

    public native int encode(short[] lin, int offset, byte[] encoded, int size);

    public native void close();

}