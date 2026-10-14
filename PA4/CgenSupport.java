// This is a project skeleton file

import java.io.PrintStream;

/** This class aggregates all kinds of support routines and constants
    for the code generator; all routines are statics, so no instance of
    this class is even created. */
class CgenSupport {

    // Newline
    final static String NEWLINE             = System.getProperty("line.separator");

    // Useful constants
    final static int MAXINT                 = 100000000;
    final static int WORD_SIZE              = 4;
    final static int LOG_WORD_SIZE          = 2;     // for logical shifts

    // Global names
    final static String CLASSNAMETAB        = "class_nameTab";
    final static String CLASSOBJTAB         = "class_objTab";
    final static String INTTAG              = "_int_tag";
    final static String BOOLTAG             = "_bool_tag";
    final static String STRINGTAG           = "_string_tag";
    final static String HEAP_START          = "heap_start";

    // Naming conventions
    final static String DISPTAB_SUFFIX      = "_dispTab";
    final static String METHOD_SEP          = ".";
    final static String CLASSINIT_SUFFIX    = "_init";
    final static String PROTOBJ_SUFFIX      = "_protObj";
    final static String OBJECTPROTOBJ       = "Object" + PROTOBJ_SUFFIX;
    final static String INTCONST_PREFIX     = "int_const";
    final static String STRCONST_PREFIX     = "str_const";
    final static String BOOLCONST_PREFIX    = "bool_const";

    final static int    EMPTYSLOT           = 0;
    final static String LABEL               = ":" + NEWLINE;

    // information about object headers
    final static int DEFAULT_OBJFIELDS      = 3;
    final static int TAG_OFFSET             = 0;
    final static int SIZE_OFFSET            = 1;
    final static int DISPTABLE_OFFSET       = 2;

    final static int STRING_SLOTS           = 1;
    final static int INT_SLOTS              = 1;
    final static int BOOL_SLOTS             = 1;

    final static String GLOBAL              = "  .globl  ";
    final static String ALIGN               = "  .align  2" + NEWLINE;
    final static String WORD                = "  .word   ";

    // register names,
    final static String ZERO                = "x0";     // Zero register
    final static String ACC                 = "a0";     // Accumulator
    final static String A1                  = "a1";     // For arguments to prim funcs
    final static String SELF                = "s0";     // Ptr to self (callee saves)
    final static String S1                  = "s1";     // save things
    final static String T1                  = "t1";     // Temporary 1
    final static String T2                  = "t2";     // Temporary 2
    final static String T3                  = "t3";     // Temporary 3
    final static String SP                  = "sp";     // Stack pointer
    final static String FP                  = "tp";     // Frame pointer
    final static String RA                  = "ra";     // Return address

    // Opcodes
    final static String JALR                = "  jalr  ";
    final static String JAL                 = "  jal   ";
    final static String RET                 = "  ret   ";

    final static String SW                  = "  sw    ";
    final static String LW                  = "  lw    ";
    final static String LI                  = "  li    ";
    final static String LA                  = "  la    ";

    final static String MOVE                = "  mv    ";
    final static String NEG                 = "  neg   ";
    final static String ADD                 = "  add   ";
    final static String ADDI                = "  addi  ";
    final static String DIV                 = "  div   ";
    final static String MUL                 = "  mul   ";
    final static String SUB                 = "  sub   ";
    final static String SLLI                = "  slli  ";
    final static String BEQZ                = "  beqz  ";
    final static String JUMP                = "  j     ";
    final static String BEQ                 = "  beq   ";
    final static String BNE                 = "  bne   ";
    final static String BLEQ                = "  ble   ";
    final static String BLT                 = "  blt   ";
    final static String BGT                 = "  bgt   ";

    /**
     * Emits the prologue of a method
     * @param size how much increment sp (size is multiplied by WORD SIZE)
     * @param s print stream
     */
    static void emitPrologue(int size, PrintStream s) {
        int realSize = size * WORD_SIZE;
        emitAddi(SP, SP, -realSize, s);
        emitStore(FP, DEFAULT_OBJFIELDS, SP, s);
        emitStore(SELF, DEFAULT_OBJFIELDS-1, SP, s);
        emitStore(RA, DEFAULT_OBJFIELDS-2, SP, s);
        emitAddi(FP, SP, 4, s);
        emitMove(SELF, ACC, s);
    }

    /**
     * Emits the epilogue of a method
     * @param size how much decrement sp (size is multiplied by WORD SIZE)
     * @param move move self object to acc
     * @param s print stream
     */
    static void emitEpilogue(int size, boolean move, PrintStream s) {
        int realSize = size * WORD_SIZE;
        if(move) emitMove(ACC, SELF, s);
        emitLoad(FP, DEFAULT_OBJFIELDS, SP, s);
        emitLoad(SELF, DEFAULT_OBJFIELDS-1, SP, s);
        emitLoad(RA, DEFAULT_OBJFIELDS-2, SP, s);
        emitAddi(SP, SP, realSize, s);
        emitReturn(s);
    }

    /** Emits an LW instruction.
     * @param dest_reg the destination register
     * @param offset the word offset from source register
     * @param source_reg the source register
     * @param s the output stream
     * */
    static void emitLoad(String dest_reg, int offset, String source_reg, PrintStream s) {
        s.println(LW + dest_reg + " " + (offset * WORD_SIZE) + "(" + source_reg + ")");
    }

    /** Emits an SW instruction.
     * @param dest_reg the destination register
     * @param offset the word offset from source register
     * @param source_reg the source register
     * @param s the output stream
     * */
    static void emitStore(String source_reg, int offset, String dest_reg, PrintStream s) {
        s.println(SW + source_reg + " " + (offset * WORD_SIZE) + "(" + dest_reg + ")");
    }

    /** Emits the LI instruction.
     * @param dest_reg the destination register
     * @param val the integer value
     * @param s the output stream
     * */
    static void emitLoadImm(String dest_reg, int val, PrintStream s) {
        s.println(LI + dest_reg + " " + val);
    }

    /** Emits an LA instruction.
     * @param dest_reg the destination register
     * @param address the address from which a word is loaded
     * @param s the output stream
     * */
    static void emitLoadAddress(String dest_reg, String address,PrintStream s){
        s.println(LA + dest_reg + " " + address);
    }

    /** Emits an LA instruction without the address part.
     * @param dest_reg the destination register
     * @param s the output stream
     * */
    static void emitPartialLoadAddress(String dest_reg, PrintStream s) {
        s.print(LA + dest_reg + " ");
    }

    /** Emits an instruction to load a boolean constant into a register.
     * @param dest_reg the destination register
     * @param b the boolean constant
     * @param s the output stream
     * */
    static void emitLoadBool(String dest_reg, BoolConst b, PrintStream s) {
        emitPartialLoadAddress(dest_reg, s);
        b.codeRef(s);
        s.println("");
    }

    /** Emits an instruction to load a string constant into a register.
     * @param dest_reg the destination register
     * @param str the string constant
     * @param s the output stream
     * */
    static void emitLoadString(String dest_reg, StringSymbol str, PrintStream s) {
        emitPartialLoadAddress(dest_reg, s);
        str.codeRef(s);
        s.println("");
    }

    /** Emits an instruction to load an integer constant into a register.
     * @param dest_reg the destination register
     * @param i the integer constant
     * @param s the output stream
     * */
    static void emitLoadInt(String dest_reg, IntSymbol i, PrintStream s) {
        emitPartialLoadAddress(dest_reg, s);
        i.codeRef(s);
        s.println("");
    }

    /** Emits a MOVE instruction.
     * @param dest_reg the destination register
     * @param source_reg the source register
     * @param s the output stream
     * */
    static void emitMove(String dest_reg, String source_reg, PrintStream s) {
        s.println(MOVE + dest_reg + " " + source_reg);
    }

    /** Emits a NEG instruction.
     * @param dest_reg the destination register
     * @param source_reg the source register
     * @param s the output stream
     * */
    static void emitNeg(String dest_reg, String source_reg, PrintStream s) {
        s.println(NEG + dest_reg + " " + source_reg);
    }

    /** Emits an ADD instruction.
     * @param dest_reg the destination register
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param s the output stream
     * */
    static void emitAdd(String dest_reg, String src1, String src2, PrintStream s) {
        s.println(ADD + dest_reg + " " + src1 + " " + src2);
    }

    /** Emits an ADDI instruction.
     * @param dest_reg the destination register
     * @param src the source register
     * @param imm the immediate
     * @param s the output stream
     * */
    static void emitAddi(String dest_reg, String src, int imm, PrintStream s) {
        s.println(ADDI + dest_reg + " " + src + " " + imm);
    }

    /** Emits a DIV instruction.
     * @param dest_reg the destination register
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param s the output stream
     * */
    static void emitDiv(String dest_reg, String src1, String src2, PrintStream s) {
        s.println(DIV + dest_reg + " " + src1 + " " + src2);
    }

    /** Emits a MUL instruction.
     * @param dest_reg the destination register
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param s the output stream
     * */
    static void emitMul(String dest_reg, String src1, String src2, PrintStream s) {
        s.println(MUL + dest_reg + " " + src1 + " " + src2);
    }

    /** Emits a SUB instruction.
     * @param dest_reg the destination register
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param s the output stream
     * */
    static void emitSub(String dest_reg, String src1, String src2, PrintStream s) {
        s.println(SUB + dest_reg + " " + src1 + " " + src2);
    }

    /** Emits an SLL instruction.
     * @param dest_reg the destination register
     * @param src1 the source register 1
     * @param num the number of bits to shift
     * @param s the output stream
     * */
    static void emitSlli(String dest_reg, String src1, int num, PrintStream s) {
        s.println(SLLI + dest_reg + " " + src1 + " " + num);
    }

    /** Emits a JALR instruction.
     * @param dest_reg the register with target address
     * @param s the output stream
     * */
    static void emitJalr(String dest_reg, PrintStream s) {
        s.println(JALR + dest_reg);
    }

    /** Emits a JAL instruction.
     * @param dest the target address or label
     * @param s the output stream
     * */
    static void emitJal(String dest, PrintStream s) {
        s.println(JAL + dest);
    }

    /** Emits a RET instruction.
     * @param s the output stream
     * */
    static void emitReturn(PrintStream s) {
        s.println(RET);
    }

    /** Emits a reference to dispatch table.
     * @param sym the name of the class
     * @param s the output stream
     * */
    static void emitDispTableRef(AbstractSymbol sym, PrintStream s) {
        s.print(sym + DISPTAB_SUFFIX);
    }

    /** Emits a reference to class' init() method.
     * @param sym the name of the class
     * @param s the output stream
     * */
    static void emitInitRef(AbstractSymbol sym, PrintStream s) {
        s.print(sym + CLASSINIT_SUFFIX);
    }

    /** Emits a reference to class' prototype object.
     * @param sym the name of the class
     * @param s the output stream
     * */
    static void emitProtObjRef(AbstractSymbol sym, PrintStream s) {
        s.print(sym + PROTOBJ_SUFFIX);
    }

    /** Emits a reference to a method in a class
     * @param classname the name of the class
     * @param methodname the name of the method
     * @param s the output stream
     * */
    static void emitMethodRef(AbstractSymbol classname, AbstractSymbol methodname, PrintStream s) {
        s.print(classname + METHOD_SEP + methodname);
    }

    /** Emits a reference to a label
     * @param label the label number
     * @param s the output stream
     * */
    static void emitLabelRef(int label, PrintStream s) {
        s.print("label" + label);
    }

    /** Emits a definition of a label
     * @param label the label number
     * @param s the output stream
     * */
    static void emitLabelDef(int label, PrintStream s) {
        emitLabelRef(label, s);
        s.println(":");
    }

    /** Emits a BEQZ instruction.
     * @param src the source register
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBeqz(String src, int label, PrintStream s) {
        s.print(BEQZ + src + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a BEQ instruction.
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBeq(String src1, String src2, int label, PrintStream s) {
        s.print(BEQ + src1 + " " + src2 + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a BNE instruction.
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBne(String src1, String src2, int label, PrintStream s) {
        s.print(BNE + src1 + " " + src2 + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a BLEQ instruction.
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBleq(String src1, String src2, int label, PrintStream s) {
        s.print(BLEQ + src1 + " " + src2 + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a BLT instruction.
     * @param src1 the source register 1
     * @param src2 the source register 2
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBlt(String src1, String src2, int label, PrintStream s) {
        s.print(BLT + src1 + " " + src2 + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a BLTI instruction.
     * @param src the source register
     * @param imm the immediate
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBlti(String src, int imm, int label, PrintStream s) {
        emitLoadImm(T3, imm, s);
        s.print(BLT + src + " " + T3 + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a BGTI instruction.
     * @param src the source register
     * @param imm the immediate
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBgti(String src, int imm, int label, PrintStream s) {
        emitLoadImm(T3, imm, s);
        s.print(BGT + src + " " + T3 + " ");
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emits a JUMP instruction.
     * @param label the label number
     * @param s the output stream
     * */
    static void emitBranch(int label, PrintStream s) {
        s.print(JUMP);
        emitLabelRef(label, s);
        s.println("");
    }

    /** Emit a sequence of instructions to push a register onto stack.
     * Stack grows toward smaller addresses.
     * @param reg the register
     * @param s the output stream
     * */
    static void emitPush(String reg, PrintStream s) {
        emitStore(reg, 0, SP, s);
        emitAddi(SP, SP, -4, s);
    }

    /** Emits code to fetch the integer value of the Integer object.
     * @param source a pointer to the Integer object
     * @param dest the destination register for the value
     * @param s the output stream
     * */
    static void emitFetchInt(String dest, String source, PrintStream s) {
        emitLoad(dest, DEFAULT_OBJFIELDS, source, s);
    }

    /** Emits code to store the integer value of the Integer object.
     * @param source an integer value
     * @param dest the pointer to an Integer object
     * @param s the output stream
     * */
    static void emitStoreInt(String source, String dest, PrintStream s) {
        emitStore(source, DEFAULT_OBJFIELDS, dest, s);
    }

    private static boolean ascii = false;

    /** Switch output mode to ASCII.
     * @param s the output stream
     * */
    static void asciiMode(PrintStream s) {
        if (!ascii) {
            s.print("  .ascii  \"");
            ascii = true;
        }
    }

    /** Switch output mode to BYTE
     * @param s the output stream
     * */
    static void byteMode(PrintStream s) {
        if (ascii) {
            s.println("\"");
            ascii = false;
        }
    }

    /** Emits a string constant.
     * @param str the string constant
     * @param s the output stream
     * */
    static void emitStringConstant(String str, PrintStream s) {
        ascii = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\n':
                    asciiMode(s);
                    s.print("\\n");
                    break;
                case '\t':
                    asciiMode(s);
                    s.print("\\t");
                    break;
                case '\\':
                    byteMode(s);
                    s.println("  .byte  " + (byte) '\\');
                    break;
                case '"':
                    asciiMode(s);
                    s.print("\\\"");
                    break;
                default:
                    if (c >= 0x20 && c <= 0x7f) {
                        asciiMode(s);
                        s.print(c);
                    } else {
                        byteMode(s);
                        s.println("  .byte  " + (byte) c);
                    }
            }
        }
        byteMode(s);
        s.println("  .byte  0");
    }
}
