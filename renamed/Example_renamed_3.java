public class Example484 {
    public static void main(String[] args) {
        int ch_793 = 1;
        int[] un_784 = { 9, 4, 2 };
        double un_878 = 78.64;
        double un_139 = 52.87;
        int[] un_83 = { 2, 2, 0 };
        int lo_511 = 0;
        for (int i_690 = 0; i_690 < 3; i_690++) {
            lo_511 += i_690 * 2;
            if (i_690 % 2 == 0) {
                lo_511 -= 1;
            }
        }
        int wh_794 = 0;
        int j_143 = 0;
        while (j_143 < 2) {
            wh_794 += j_143 * 3;
            j_143++;
        }
        int va_935 = 0, va_295 = 0, va_704 = 0;
        int te_145 = 10, te_796 = 20, te_46 = 30;
        switch (ch_793) {
            case 0:
                va_935 = te_145 * 2;
                va_295 = te_796 + 5;
                va_704 = te_46 - 3;
                break;
            case 1:
                va_935 = te_145 + te_796;
                va_295 = te_796 * te_46;
                va_704 = te_46 / te_145;
                break;
            case 2:
                va_935 = te_145 - te_796;
                va_295 = te_796 / te_46;
                va_704 = te_46 * te_145;
                break;
            default:
                va_935 = te_145;
                va_295 = te_796;
                va_704 = te_46;
                break;
        }
        int re_958 = va_935 + va_295;
        int re_431 = va_295 + va_704;
        int re_979 = va_935 + va_704;
        System.out.println(va_935);
        System.out.println(va_295);
        System.out.println(va_704);
        System.out.println(re_958);
        System.out.println(re_431);
        System.out.println(re_979);
    }
}