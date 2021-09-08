package info.hkzlab.ega2rgbs.epromgen.utilities;

public class EPROMTools {
    private static final int TOT_COMBINATIONS = 1024; // 2^10

    private static final int IN_MASK_MODE = 0x0001;
    private static final int IN_MASK_BH = 0x0002;
    private static final int IN_MASK_BL = 0x0004;
    private static final int IN_MASK_GH = 0x0008;
    private static final int IN_MASK_GL_I = 0x0010;
    private static final int IN_MASK_RH = 0x0020;
    private static final int IN_MASK_RL = 0x0040;
    private static final int IN_MASK_VS = 0x0080;
    private static final int IN_MASK_HS = 0x0100;
    private static final int IN_BRWNFIX = 0x0200;

    private static final int OUT_MASK_BH = 0x01;
    private static final int OUT_MASK_BL = 0x02;
    private static final int OUT_MASK_GH = 0x04;
    private static final int OUT_MASK_GL = 0x08;
    private static final int OUT_MASK_RH = 0x10;
    private static final int OUT_MASK_RL = 0x20;
    private static final int OUT_MASK_CS = 0x40;
    private static final int OUT_MASK_NCS = 0x80;

    private EPROMTools() {};
    
    static private boolean CGA_calcBROWN(boolean r, boolean g, boolean b, boolean i) {
        return r && g && !b && !i;
    }

    static private boolean EGA_calcBROWN(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl) {
        return r && g && !b && !rl && !bl && !gl;
    }

    static private boolean CGA_calcBH(boolean r, boolean g, boolean b, boolean i) {
        return b;
    }

    static private boolean CGA_calcBL(boolean r, boolean g, boolean b, boolean i) {
        return i;
    }

    static private boolean CGA_calcGH(boolean r, boolean g, boolean b, boolean i, boolean brwnFix) {
        boolean brown = CGA_calcBROWN(r, g, b, i);
        return brwnFix ? (brown != g) : g;
    }

    static private boolean CGA_calcGL(boolean r, boolean g, boolean b, boolean i, boolean brwnFix) {
        boolean brown = CGA_calcBROWN(r, g, b, i);
        return brwnFix ? (brown || i) : i;
    }

    static private boolean CGA_calcRH(boolean r, boolean g, boolean b, boolean i) {
        return r;
    }

    static private boolean CGA_calcRL(boolean r, boolean g, boolean b, boolean i) {
        return i;
    }

    static private boolean EGA_calcBH(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl) {
        return b;
    }

    static private boolean EGA_calcBL(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl) {
        return bl;
    }

    static private boolean EGA_calcGH(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl, boolean brwnFix) {
        boolean brown = EGA_calcBROWN(r, g, b, rl, gl, bl);
        return brwnFix ? (brown != g) : g;
    }

    static private boolean EGA_calcGL(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl, boolean brwnFix) {
        boolean brown = EGA_calcBROWN(r, g, b, rl, gl, bl);
        return brwnFix ? (brown || gl) : gl;
    }

    static private boolean EGA_calcRH(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl) {
        return r;
    }

    static private boolean EGA_calcRL(boolean r, boolean g, boolean b, boolean rl, boolean gl, boolean bl) {
        return rl;
    }

    static private boolean calcSYNC(boolean vs, boolean hs) {
        return vs != hs;
    }

    static private int calculateDataForAddress(int address) {
        int data = 0;

        boolean mode = (address & IN_MASK_MODE) != 0;
        boolean bh = (address & IN_MASK_BH) != 0;
        boolean bl = (address & IN_MASK_BL) != 0;
        boolean gh = (address & IN_MASK_GH) != 0;
        boolean gli = (address & IN_MASK_GL_I) != 0;
        boolean rh = (address & IN_MASK_RH) != 0;
        boolean rl = (address & IN_MASK_RL) != 0;
        boolean vs = (address & IN_MASK_VS) != 0;
        boolean hs = (address & IN_MASK_HS) != 0;
        boolean brwnFix = (address & IN_BRWNFIX) != 0;

        boolean out_bh, out_bl, out_gh, out_gl, out_rh, out_rl, out_cs;

        out_cs = calcSYNC(vs, hs);

        if(mode) { // CGA
            out_bh = CGA_calcBH(rh, gh, bh, gli);
            out_gh = CGA_calcGH(rh, gh, bh, gli, brwnFix);
            out_rh = CGA_calcRH(rh, gh, bh, gli);
            out_bl = CGA_calcBL(rh, gh, bh, gli);
            out_gl = CGA_calcGL(rh, gh, bh, gli, brwnFix);
            out_rl = CGA_calcRL(rh, gh, bh, gli);
        } else { // EGA
            out_bh = EGA_calcBH(rh, gh, bh, rl, gli, bl);
            out_gh = EGA_calcGH(rh, gh, bh, rl, gli, bl, /*brwnFix*/ false);
            out_rh = EGA_calcRH(rh, gh, bh, rl, gli, bl);
            out_bl = EGA_calcBL(rh, gh, bh, rl, gli, bl);
            out_gl = EGA_calcGL(rh, gh, bh, rl, gli, bl, /*brwnFix*/ false);
            out_rl = EGA_calcRL(rh, gh, bh, rl, gli, bl);
        }

        data |= out_bh ? OUT_MASK_BH : 0;
        data |= out_bl ? OUT_MASK_BL : 0;
        data |= out_gh ? OUT_MASK_GH : 0;
        data |= out_gl ? OUT_MASK_GL : 0;
        data |= out_rh ? OUT_MASK_RH : 0;
        data |= out_rl ? OUT_MASK_RL : 0;
        data |= out_cs ? OUT_MASK_CS : 0;
        data |= !out_cs ? OUT_MASK_NCS : 0;

        return data;
    }

    static public byte[] buildBuffer() {
        byte[] buf = new byte[TOT_COMBINATIONS];

        for(int idx = 0; idx < buf.length; idx++) buf[idx] = (byte)calculateDataForAddress(idx);

        return buf;
    }
}
