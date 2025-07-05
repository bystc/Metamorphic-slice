public class Example469 {
    public static void main(String[] args) {
        int ch_272 = 0;
        double un_59 = 12.78;
        String un_127 = "str86";
        int[] un_296 = { 7, 5, 4 };
        int[] un_935 = { 9, 0, 6 };
        int lo_842 = 0;
        for (int i_754 = 0; i_754 < 3; i_754++) {
            lo_842 += i_754 * 2;
            if (i_754 % 2 == 0) {
                lo_842 -= 1;
            }
        }
        int wh_819 = 0;
        int j_198 = 0;
        while (j_198 < 2) {
            wh_819 += j_198 * 3;
            j_198++;
        }
        int va_307 = 0, va_473 = 0, va_528 = 0;
        int te_987 = 10, te_853 = 20, te_82 = 30;
        switch (ch_272) {
            case 0:
                va_307 = te_987 * 2;
                va_473 = te_853 + 5;
                va_528 = te_82 - 3;
                break;
            case 1:
                va_307 = te_987 + te_853;
                va_473 = te_853 * te_82;
                va_528 = te_82 / te_987;
                break;
            case 2:
                va_307 = te_987 - te_853;
                va_473 = te_853 / te_82;
                va_528 = te_82 * te_987;
                break;
            default:
                va_307 = te_987;
                va_473 = te_853;
                va_528 = te_82;
                break;
        }
        int re_969 = va_307 + va_473;
        int re_588 = va_473 + va_528;
        int re_509 = va_307 + va_528;
        System.out.println(va_307);
        System.out.println(va_473);
        System.out.println(va_528);
        System.out.println(re_969);
        System.out.println(re_588);
        System.out.println(re_509);
    }
}