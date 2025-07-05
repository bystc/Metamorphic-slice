public class Example999 {
    public static void main(String[] args) {
        int ch_787 = 0;
        int un_439 = 179;
        int un_23 = 116;
        String un_135 = "str42";
        double un_741 = 34.23;
        String un_178 = "str80";
        int lo_915 = 0;
        for (int i_677 = 0; i_677 < 3; i_677++) {
            lo_915 += i_677 * 2;
            if (i_677 % 2 == 0) {
                lo_915 -= 1;
            }
        }
        int wh_632 = 0;
        int j_368 = 0;
        while (j_368 < 2) {
            wh_632 += j_368 * 3;
            j_368++;
        }
        int va_314 = 0, va_661 = 0, va_90 = 0;
        int te_816 = 10, te_912 = 20, te_986 = 30;
        switch (ch_787) {
            case 0:
                va_314 = te_816 * 2;
                va_661 = te_912 + 5;
                va_90 = te_986 - 3;
                break;
            case 1:
                va_314 = te_816 + te_912;
                va_661 = te_912 * te_986;
                va_90 = te_986 / te_816;
                break;
            case 2:
                va_314 = te_816 - te_912;
                va_661 = te_912 / te_986;
                va_90 = te_986 * te_816;
                break;
            default:
                va_314 = te_816;
                va_661 = te_912;
                va_90 = te_986;
                break;
        }
        int re_17 = va_314 + va_661;
        int re_241 = va_661 + va_90;
        int re_153 = va_314 + va_90;
        System.out.println(va_314);
        System.out.println(va_661);
        System.out.println(va_90);
        System.out.println(re_17);
        System.out.println(re_241);
        System.out.println(re_153);
    }
}