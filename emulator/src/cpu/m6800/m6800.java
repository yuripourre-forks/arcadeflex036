package cpu.m6800;

import mame.cpuintrfH.cpu_interface;
import static mame.cpuintrfH.*;
import static mame.cpuintrfH.*;
import static mame.driverH.*;
import static mame.memoryH.*;
import static mame.memory.*;
import static cpu.m6800.m6800H.*;
import static arcadeflex.libc_old.*;

public class m6800 extends cpu_interface {

    public static FILE m6800log = null;//fopen("m6800.log", "wa");  //for debug purposes

    public static int[] m6800_ICount = new int[1];

    public m6800() {
        cpu_num = CPU_M6800;
        num_irqs = 1;
        default_vector = 0;
        overclock = 1.0;
        no_int = M6800_INT_NONE;
        irq_int = M6800_INT_IRQ;
        nmi_int = M6800_INT_NMI;
        address_shift = 0;
        address_bits = 16;
        endianess = CPU_IS_BE;
        align_unit = 1;
        max_inst_len = 4;
        abits1 = ABITS1_16;
        abits2 = ABITS2_16;
        abitsmin = ABITS_MIN_16;
        icount = m6800_ICount;
        icount[0] = 50000;
    }

    public static class PAIR {
        //L = low 8 bits
        //H = high 8 bits
        //D = whole 16 bits

        public int H, L, D;

        public void SetH(int val) {
            H = val;
            D = (H << 8) | L;
        }

        public void SetL(int val) {
            L = val;
            D = (H << 8) | L;
        }

        public void SetD(int val) {
            D = val;
            H = D >> 8 & 0xFF;
            L = D & 0xFF;
        }

        public void AddH(int val) {
            H = (H + val) & 0xFF;
            D = (H << 8) | L;
        }

        public void AddL(int val) {
            L = (L + val) & 0xFF;
            D = (H << 8) | L;
        }

        public void AddD(int val) {
            D = (D + val) & 0xFFFF;
            H = D >> 8 & 0xFF;
            L = D & 0xFF;
        }
    };

    public static class PAIRD {
        //L = low 16 bits
        //H = high 16 bits
        //D = whole 32 bits

        public long H, L, D;

        public void SetH(long val) {
            H = val;
            D = (H << 16) | L;
        }

        public void SetL(long val) {
            L = val;
            D = (H << 16) | L;
        }

        public void SetD(long val) {
            D = val;
            H = D >> 16 & 0xFFFF;
            L = D & 0xFFFF;
        }

        public void AddH(long val) {
            H = (H + val) & 0xFFFF;
            D = (H << 16) | L;
        }

        public void AddL(long val) {
            L = (L + val) & 0xFFFF;
            D = (H << 16) | L;
        }

        public void AddD(long val) {
            D = (D + val) & 0xFFFFFFFFL;
            H = D >> 16 & 0xFFFF;
            L = D & 0xFFFF;
        }
    };
    /* 6800 Registers */

    public static class m6800_Regs {

        //	int 	subtype;		/* CPU subtype */
        public /*PAIR*/ int ppc;			/* Previous program counter */

        public /*PAIR*/ int pc; 			/* Program counter */

        public PAIR s;				/* Stack pointer */

        public PAIR x;				/* Index register */

        public PAIR d;				/* Accumulators */

        public int /*UINT8*/ cc; 			/* Condition codes */

        public int /*UINT8*/ wai_state;		/* WAI opcode state ,(or sleep opcode state) */

        public int /*UINT8*/ nmi_state;		/* NMI line state */

        public int[] /*UINT8*/ irq_state = new int[2];	/* IRQ line state [IRQ1,TIN] */

        public int /*UINT8*/ ic_eddge;		/* InputCapture eddge , b.0=fall,b.1=raise */

        public irqcallbacksPtr irq_callback;
        int extra_cycles;	/* cycles used for interrupts */

        public opcode[] insn;	/* instruction table */
        /*const UINT8*/ public int[] cycles;			/* clock cycle of instruction table */
        /* internal registers */

        public int /*UINT8*/ port1_ddr;
        public int /*UINT8*/ port2_ddr;
        public int /*UINT8*/ port1_data;
        public int /*UINT8*/ port2_data;
        public int /*UINT8*/ tcsr;			/* Timer Control and Status Register */

        public int /*UINT8*/ pending_tcsr;	/* pending IRQ flag for clear IRQflag process */

        public int /*UINT8*/ irq2;			/* IRQ2 flags */

        public int /*UINT8*/ ram_ctrl;
        public PAIRD counter;		/* free running counter */

        public PAIRD output_compare;	/* output compare       */

        public int /*UINT16*/ input_capture;	/* input capture        */

        public PAIRD timer_over;
    }
    public static m6800_Regs m6800 = new m6800_Regs();
    /* point of next timer event */
    static /*UINT32*/ long timer_next;

    static int cycles_6800[]
            = {
                /* 0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F */
                /*0*/0, 2, 0, 0, 0, 0, 2, 2, 4, 4, 2, 2, 2, 2, 2, 2,
                /*1*/ 2, 2, 0, 0, 0, 0, 2, 2, 0, 2, 0, 2, 0, 0, 0, 0,
                /*2*/ 4, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                /*3*/ 4, 4, 4, 4, 4, 4, 4, 4, 0, 5, 0, 10, 0, 0, 9, 12,
                /*4*/ 2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2,
                /*5*/ 2, 0, 0, 2, 2, 0, 2, 2, 2, 2, 2, 0, 2, 2, 0, 2,
                /*6*/ 7, 0, 0, 7, 7, 0, 7, 7, 7, 7, 7, 0, 7, 7, 4, 7,
                /*7*/ 6, 0, 0, 6, 6, 0, 6, 6, 6, 6, 6, 0, 6, 6, 3, 6,
                /*8*/ 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 2, 3, 8, 3, 0,
                /*9*/ 3, 3, 3, 0, 3, 3, 3, 4, 3, 3, 3, 3, 4, 0, 4, 5,
                /*A*/ 5, 5, 5, 0, 5, 5, 5, 6, 5, 5, 5, 5, 6, 8, 6, 7,
                /*B*/ 4, 4, 4, 0, 4, 4, 4, 5, 4, 4, 4, 4, 5, 9, 5, 6,
                /*C*/ 2, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 2, 0, 0, 3, 0,
                /*D*/ 3, 3, 3, 0, 3, 3, 3, 4, 3, 3, 3, 3, 0, 0, 4, 5,
                /*E*/ 5, 5, 5, 0, 5, 5, 5, 6, 5, 5, 5, 5, 0, 0, 6, 7,
                /*F*/ 4, 4, 4, 0, 4, 4, 4, 5, 4, 4, 4, 4, 0, 0, 5, 6
            };

    public int RM(int addr) {
        return (cpu_readmem16(addr) & 0xFF);
    }

    public void WM(int addr, int value) {
        cpu_writemem16(addr, value);
    }

    public char ROP(int addr) {
        return cpu_readop(addr);
    }

    public char ROP_ARG(int addr) {
        return cpu_readop_arg(addr);
    }

    int RM16(int addr) {
        int i = RM(addr + 1 & 0xFFFF);
        i |= RM(addr) << 8;
        return i;
    }

    void WM16(int addr, int reg) {
        WM(addr + 1 & 0xFFFF, reg & 0xFF);
        WM(addr, reg >> 8);
    }

    public void PUSHBYTE(int w)//WM(SD,b); --S
    {
        WM(m6800.s.D, w);
        m6800.s.AddD(-1);

    }

    public void PUSHWORD(int w)//WM(SD,w.b.l); --S; WM(SD,w.b.h); --S
    {
        WM(m6800.s.D, w & 0xFF);
        m6800.s.AddD(-1);
        WM(m6800.s.D, w >> 8);
        m6800.s.AddD(-1);
    }

    public void CHANGE_PC() {
        change_pc16(m6800.pc & 0xFFFF);//ensure it's 16bit just in case
    }
    /* macros to set status flags */

    public void SEC() {
        m6800.cc |= 0x01l;
    }

    public void CLC() {
        m6800.cc &= 0xfe;
    }

    public void SEZ() {
        m6800.cc |= 0x04;
    }

    public void CLZ() {
        m6800.cc &= 0xfb;
    }

    public void SEN() {
        m6800.cc |= 0x08;
    }

    public void CLN() {
        m6800.cc &= 0xf7;
    }

    public void SEV() {
        m6800.cc |= 0x02;
    }

    public void CLV() {
        m6800.cc &= 0xfd;
    }

    public void SEH() {
        m6800.cc |= 0x20;
    }

    public void CLH() {
        m6800.cc &= 0xdf;
    }

    public void SEI() {
        m6800.cc |= 0x10;
    }

    public void CLI() {
        m6800.cc &= ~0x10;
    }

    /* mnemonicos for the Timer Control and Status Register bits */
    public static final int TCSR_OLVL = 0x01;
    public static final int TCSR_IEDG = 0x02;
    public static final int TCSR_ETOI = 0x04;
    public static final int TCSR_EOCI = 0x08;
    public static final int TCSR_EICI = 0x10;
    public static final int TCSR_TOF = 0x20;
    public static final int TCSR_OCF = 0x40;
    public static final int TCSR_ICF = 0x80;

    public void MODIFIED_tcsr() {
        m6800.irq2 = (m6800.tcsr & (m6800.tcsr << 3)) & (TCSR_ICF | TCSR_OCF | TCSR_TOF);
    }

    public void SET_TIMRE_EVENT() {
        timer_next = (m6800.output_compare.D < m6800.timer_over.D) ? m6800.output_compare.D : m6800.timer_over.D;
    }
    /* cleanup high-word of counters */

    public void CLEANUP_conters() {
        m6800.output_compare.SetH(m6800.output_compare.H - m6800.counter.H);//OCH -= CTH;
        m6800.timer_over.SetL(m6800.timer_over.H - m6800.counter.H);//TOH -= CTH;
        m6800.counter.SetH(0);//CTH = 0;								
        SET_TIMRE_EVENT();
    }

    public void INCREMENT_COUNTER(int amount) {
        m6800_ICount[0] -= amount;
        m6800.timer_over.SetD(m6800.timer_over.D + amount);//CTD += amount;					
        if (m6800.timer_over.D >= timer_next) {
            check_timer_event();
        }
    }

    public void EAT_CYCLES() {
        int cycles_to_eat;

        cycles_to_eat = (int) (timer_next - m6800.counter.D);
        if (cycles_to_eat > m6800_ICount[0]) {
            cycles_to_eat = m6800_ICount[0];
        }
        if (cycles_to_eat > 0) {
            INCREMENT_COUNTER(cycles_to_eat);
        }
    }
    /* check OCI or TOI */

    public void check_timer_event() {
        /* OCI */
        if (m6800.timer_over.D >= m6800.output_compare.D) {
            m6800.output_compare.AddH(1);//OCH++;	// next IRQ point
            m6800.tcsr |= TCSR_OCF;
            m6800.pending_tcsr |= TCSR_OCF;
            MODIFIED_tcsr();
            if ((m6800.cc & 0x10) == 0 && (m6800.tcsr & TCSR_EOCI) != 0) {
                TAKE_OCI();
            }
        }
        /* TOI */
        if (m6800.counter.D >= m6800.timer_over.D) {
            m6800.timer_over.AddL(1);	// next IRQ point

            m6800.tcsr |= TCSR_TOF;
            m6800.pending_tcsr |= TCSR_TOF;
            MODIFIED_tcsr();
            if ((m6800.cc & 0x10) == 0 && (m6800.tcsr & TCSR_ETOI) != 0) {
                TAKE_TOI();
            }
        }
        /* set next event */
        SET_TIMRE_EVENT();
    }
    /* take interrupt */

    public void TAKE_ICI() {
        ENTER_INTERRUPT("M6800#%d take ICI\n", 0xfff6);
    }

    public void TAKE_OCI() {
        ENTER_INTERRUPT("M6800#%d take OCI\n", 0xfff4);
    }

    public void TAKE_TOI() {
        ENTER_INTERRUPT("M6800#%d take TOI\n", 0xfff2);
    }

    public void TAKE_SCI() {
        ENTER_INTERRUPT("M6800#%d take SCI\n", 0xfff0);
    }

    public void TAKE_TRAP() {
        ENTER_INTERRUPT("M6800#%d take TRAP\n", 0xffee);
    }
    /* IRQ enter */

    void ENTER_INTERRUPT(String message, int irq_vector) {
        //LOG((errorlog, message, cpu_getactivecpu()));
        if ((m6800.wai_state & (M6800_WAI | M6800_SLP)) != 0) {
            if ((m6800.wai_state & M6800_WAI) != 0) {
                m6800.extra_cycles += 4;
            }
            m6800.wai_state &= ~(M6800_WAI | M6800_SLP);
        } else {
            PUSHWORD(m6800.pc);
            PUSHWORD(m6800.x.D);
            PUSHBYTE(m6800.d.H);
            PUSHBYTE(m6800.d.L);
            PUSHBYTE(m6800.cc);
            m6800.extra_cycles += 12;
        }
        SEI();
        m6800.pc = RM16(irq_vector);
        CHANGE_PC();
    }
    static opcode[] m6800_insn = { /*illegal,nop,	illegal,illegal,illegal,illegal,tap,	tpa,
     inx,	dex,	clv,	sev,	clc,	sec,	cli,	sei,
     sba,	cba,	illegal,illegal,illegal,illegal,tab,	tba,
     illegal,daa,	illegal,aba,	illegal,illegal,illegal,illegal,
     bra,	brn,	bhi,	bls,	bcc,	bcs,	bne,	beq,
     bvc,	bvs,	bpl,	bmi,	bge,	blt,	bgt,	ble,
     tsx,	ins,	pula,	pulb,	des,	txs,	psha,	pshb,
     illegal,rts,	illegal,rti,	illegal,illegal,wai,	swi,
     nega,	illegal,illegal,coma,	lsra,	illegal,rora,	asra,
     asla,	rola,	deca,	illegal,inca,	tsta,	illegal,clra,
     negb,	illegal,illegal,comb,	lsrb,	illegal,rorb,	asrb,
     aslb,	rolb,	decb,	illegal,incb,	tstb,	illegal,clrb,
     neg_ix, illegal,illegal,com_ix, lsr_ix, illegal,ror_ix, asr_ix,
     asl_ix, rol_ix, dec_ix, illegal,inc_ix, tst_ix, jmp_ix, clr_ix,
     neg_ex, illegal,illegal,com_ex, lsr_ex, illegal,ror_ex, asr_ex,
     asl_ex, rol_ex, dec_ex, illegal,inc_ex, tst_ex, jmp_ex, clr_ex,
     suba_im,cmpa_im,sbca_im,illegal,anda_im,bita_im,lda_im, sta_im,
     eora_im,adca_im,ora_im, adda_im,cmpx_im,bsr,	lds_im, sts_im,
     suba_di,cmpa_di,sbca_di,illegal,anda_di,bita_di,lda_di, sta_di,
     eora_di,adca_di,ora_di, adda_di,cmpx_di,jsr_di, lds_di, sts_di,
     suba_ix,cmpa_ix,sbca_ix,illegal,anda_ix,bita_ix,lda_ix, sta_ix,
     eora_ix,adca_ix,ora_ix, adda_ix,cmpx_ix,jsr_ix, lds_ix, sts_ix,
     suba_ex,cmpa_ex,sbca_ex,illegal,anda_ex,bita_ex,lda_ex, sta_ex,
     eora_ex,adca_ex,ora_ex, adda_ex,cmpx_ex,jsr_ex, lds_ex, sts_ex,
     subb_im,cmpb_im,sbcb_im,illegal,andb_im,bitb_im,ldb_im, stb_im,
     eorb_im,adcb_im,orb_im, addb_im,illegal,illegal,ldx_im, stx_im,
     subb_di,cmpb_di,sbcb_di,illegal,andb_di,bitb_di,ldb_di, stb_di,
     eorb_di,adcb_di,orb_di, addb_di,illegal,illegal,ldx_di, stx_di,
     subb_ix,cmpb_ix,sbcb_ix,illegal,andb_ix,bitb_ix,ldb_ix, stb_ix,
     eorb_ix,adcb_ix,orb_ix, addb_ix,illegal,illegal,ldx_ix, stx_ix,
     subb_ex,cmpb_ex,sbcb_ex,illegal,andb_ex,bitb_ex,ldb_ex, stb_ex,
     eorb_ex,adcb_ex,orb_ex, addb_ex,illegal,illegal,ldx_ex, stx_ex*/};

    @Override
    public void reset(Object param) {
        SEI();				/* IRQ disabled */

        m6800.pc = RM16(0xfffe);
        CHANGE_PC();

        /* HJB 990417 set CPU subtype (other reset functions override this) */
//	m6800.subtype   = SUBTYPE_M6800;
        m6800.insn = m6800_insn;
        m6800.cycles = cycles_6800;

        m6800.wai_state = 0;
        m6800.nmi_state = 0;
        m6800.irq_state[M6800_IRQ_LINE] = 0;
        m6800.irq_state[M6800_TIN_LINE] = 0;
        m6800.ic_eddge = 0;

        m6800.port1_ddr = 0x00;
        m6800.port2_ddr = 0x00;
        /* TODO: on reset port 2 should be read to determine the operating mode (bits 0-2) */
        m6800.tcsr = 0x00;
        m6800.pending_tcsr = 0x00;
        m6800.irq2 = 0;
        m6800.counter.SetD(0x0000);
        m6800.output_compare.SetD(0xffff);
        m6800.timer_over.SetD(0xffff);
        m6800.ram_ctrl |= 0x40;
    }

    @Override
    public void exit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int execute(int cycles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object init_context() {
        Object reg = new m6800_Regs();
        return reg;
    }

    @Override
    public Object get_context() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_context(Object reg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_pc() {
        return m6800.pc & 0xFFFF;
    }

    @Override
    public void set_pc(int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_sp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_sp(int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int get_reg(int regnum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_reg(int regnum, int val) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_nmi_line(int linestate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_irq_line(int irqline, int linestate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_irq_callback(irqcallbacksPtr callback) {
        m6800.irq_callback = callback;
    }

    @Override
    public void internal_interrupt(int type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cpu_state_save(Object file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cpu_state_load(Object file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String cpu_info(Object context, int regnum) {
        switch (regnum) {
            case CPU_INFO_NAME:
                return "M6800";
            case CPU_INFO_FAMILY:
                return "Motorola 6800";
            case CPU_INFO_VERSION:
                return "1.1";
            case CPU_INFO_FILE:
                return "m6800.c";
            case CPU_INFO_CREDITS:
                return "The MAME team.";
        }
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public int memory_read(int offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void memory_write(int offset, int data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set_op_base(int pc) {
        cpu_setOPbase16.handler(pc, 0);
    }

    public static abstract interface opcode {

        public abstract void handler();
    }
}
