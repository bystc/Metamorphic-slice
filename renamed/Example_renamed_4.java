public class Example540 {
    public static void main(String[] args) {
        int ch_810 = 3;
        int un_441 = 191;
        int[] un_926 = { 6, 3, 1 };
        String un_765 = "str44";
        double un_750 = 93.84;
        int lo_582 = 0;
        for (int i_562 = 0; i_562 < 3; i_562++) {
            lo_582 += i_562 * 2;
            if (i_562 % 2 == 0) {
                lo_582 -= 1;
            }
        }
        int wh_867 = 0;
        int j_906 = 0;
        while (j_906 < 2) {
            wh_867 += j_906 * 3;
            j_906++;
        }
        int va_577 = 0, va_55 = 0, va_367 = 0;
        int te_403 = 10, te_93 = 20, te_287 = 30;
        switch (ch_810) {
            case 0:
                va_577 = te_403 * 2;
                va_55 = te_93 + 5;
                va_367 = te_287 - 3;
                break;
            case 1:
                va_577 = te_403 + te_93;
                va_55 = te_93 * te_287;
                va_367 = te_287 / te_403;
                break;
            case 2:
                va_577 = te_403 - te_93;
                va_55 = te_93 / te_287;
                va_367 = te_287 * te_403;
                break;
            default:
                va_577 = te_403;
                va_55 = te_93;
                va_367 = te_287;
                break;
        }
        int re_897 = va_577 + va_55;
        int re_765 = va_55 + va_367;
        int re_670 = va_577 + va_367;
        System.out.println(va_577);
        System.out.println(va_55);
        System.out.println(va_367);
        System.out.println(re_897);
        System.out.println(re_765);
        System.out.println(re_670);
    }
}