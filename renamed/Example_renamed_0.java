public class Example117 {
    public static void main(String[] args) {
        int ch_550 = 0;
        String un_280 = "str67";
        int un_856 = 125;
        int[] un_863 = { 3, 4, 0 };
        int un_990 = 159;
        double un_243 = 49.88;
        int lo_954 = 0;
        for (int i_928 = 0; i_928 < 3; i_928++) {
            lo_954 += i_928 * 2;
            if (i_928 % 2 == 0) {
                lo_954 -= 1;
            }
        }
        int wh_875 = 0;
        int j_862 = 0;
        while (j_862 < 2) {
            wh_875 += j_862 * 3;
            j_862++;
        }
        int va_978 = 0, va_402 = 0, va_508 = 0;
        int te_451 = 10, te_430 = 20, te_742 = 30;
        switch (ch_550) {
            case 0:
                va_978 = te_451 * 2;
                va_402 = te_430 + 5;
                va_508 = te_742 - 3;
                break;
            case 1:
                va_978 = te_451 + te_430;
                va_402 = te_430 * te_742;
                va_508 = te_742 / te_451;
                break;
            case 2:
                va_978 = te_451 - te_430;
                va_402 = te_430 / te_742;
                va_508 = te_742 * te_451;
                break;
            default:
                va_978 = te_451;
                va_402 = te_430;
                va_508 = te_742;
                break;
        }
        int re_653 = va_978 + va_402;
        int re_562 = va_402 + va_508;
        int re_406 = va_978 + va_508;
        System.out.println(va_978);
        System.out.println(va_402);
        System.out.println(va_508);
        System.out.println(re_653);
        System.out.println(re_562);
        System.out.println(re_406);
    }
}