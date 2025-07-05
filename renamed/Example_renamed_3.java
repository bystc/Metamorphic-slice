public class Example678 {
    public static void main(String[] args) {
        int ch_924 = 3;
        int[] un_165 = { 1, 2, 5 };
        int[] un_657 = { 0, 6, 1 };
        int un_745 = 142;
        int lo_122 = 0;
        for (int i_407 = 0; i_407 < 3; i_407++) {
            lo_122 += i_407 * 2;
            if (i_407 % 2 == 0) {
                lo_122 -= 1;
            }
        }
        int wh_224 = 0;
        int j_776 = 0;
        while (j_776 < 2) {
            wh_224 += j_776 * 3;
            j_776++;
        }
        int va_363 = 0, va_599 = 0, va_954 = 0;
        int te_179 = 10, te_574 = 20, te_979 = 30;
        switch (ch_924) {
            case 0:
                va_363 = te_179 * 2;
                va_599 = te_574 + 5;
                va_954 = te_979 - 3;
                break;
            case 1:
                va_363 = te_179 + te_574;
                va_599 = te_574 * te_979;
                va_954 = te_979 / te_179;
                break;
            case 2:
                va_363 = te_179 - te_574;
                va_599 = te_574 / te_979;
                va_954 = te_979 * te_179;
                break;
            default:
                va_363 = te_179;
                va_599 = te_574;
                va_954 = te_979;
                break;
        }
        int re_556 = va_363 + va_599;
        int re_123 = va_599 + va_954;
        int re_929 = va_363 + va_954;
        System.out.println(va_363);
        System.out.println(va_599);
        System.out.println(va_954);
        System.out.println(re_556);
        System.out.println(re_123);
        System.out.println(re_929);
    }
}