public class Example488 {
    public static void main(String[] args) {
        int ch_69 = 2;
        double un_197 = 13.44;
        String un_803 = "str55";
        int un_594 = 145;
        String un_82 = "str50";
        int lo_197 = 0;
        for (int i_536 = 0; i_536 < 3; i_536++) {
            lo_197 += i_536 * 2;
            if (i_536 % 2 == 0) {
                lo_197 -= 1;
            }
        }
        int wh_482 = 0;
        int j_247 = 0;
        while (j_247 < 2) {
            wh_482 += j_247 * 3;
            j_247++;
        }
        int va_501 = 0, va_827 = 0, va_98 = 0;
        int te_71 = 10, te_213 = 20, te_405 = 30;
        switch (ch_69) {
            case 0:
                va_501 = te_71 * 2;
                va_827 = te_213 + 5;
                va_98 = te_405 - 3;
                break;
            case 1:
                va_501 = te_71 + te_213;
                va_827 = te_213 * te_405;
                va_98 = te_405 / te_71;
                break;
            case 2:
                va_501 = te_71 - te_213;
                va_827 = te_213 / te_405;
                va_98 = te_405 * te_71;
                break;
            default:
                va_501 = te_71;
                va_827 = te_213;
                va_98 = te_405;
                break;
        }
        int re_685 = va_501 + va_827;
        int re_830 = va_827 + va_98;
        int re_882 = va_501 + va_98;
        System.out.println(va_501);
        System.out.println(va_827);
        System.out.println(va_98);
        System.out.println(re_685);
        System.out.println(re_830);
        System.out.println(re_882);
    }
}