public class Example498 {
    public static void main(String[] args) {
        int ch_938 = 2;
        int[] un_421 = { 4, 8, 7 };
        int un_493 = 176;
        int un_140 = 136;
        String un_862 = "str8";
        int un_103 = 143;
        int lo_316 = 0;
        for (int i_700 = 0; i_700 < 3; i_700++) {
            lo_316 += i_700 * 2;
            if (i_700 % 2 == 0) {
                lo_316 -= 1;
            }
        }
        int wh_802 = 0;
        int j_808 = 0;
        while (j_808 < 2) {
            wh_802 += j_808 * 3;
            j_808++;
        }
        int va_876 = 0, va_351 = 0, va_695 = 0;
        int te_392 = 10, te_812 = 20, te_497 = 30;
        switch (ch_938) {
            case 0:
                va_876 = te_392 * 2;
                va_351 = te_812 + 5;
                va_695 = te_497 - 3;
                break;
            case 1:
                va_876 = te_392 + te_812;
                va_351 = te_812 * te_497;
                va_695 = te_497 / te_392;
                break;
            case 2:
                va_876 = te_392 - te_812;
                va_351 = te_812 / te_497;
                va_695 = te_497 * te_392;
                break;
            default:
                va_876 = te_392;
                va_351 = te_812;
                va_695 = te_497;
                break;
        }
        int re_887 = va_876 + va_351;
        int re_3 = va_351 + va_695;
        int re_898 = va_876 + va_695;
        System.out.println(va_876);
        System.out.println(va_351);
        System.out.println(va_695);
        System.out.println(re_887);
        System.out.println(re_3);
        System.out.println(re_898);
    }
}