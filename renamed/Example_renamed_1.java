public class Example139 {
    public static void main(String[] args) {
        int ch_679 = 0;
        int un_407 = 194;
        String un_97 = "str54";
        String un_841 = "str57";
        int un_648 = 156;
        int lo_573 = 0;
        for (int i_60 = 0; i_60 < 3; i_60++) {
            lo_573 += i_60 * 2;
            if (i_60 % 2 == 0) {
                lo_573 -= 1;
            }
        }
        int wh_201 = 0;
        int j_671 = 0;
        while (j_671 < 2) {
            wh_201 += j_671 * 3;
            j_671++;
        }
        int va_114 = 0, va_819 = 0, va_74 = 0;
        int te_880 = 10, te_613 = 20, te_120 = 30;
        switch (ch_679) {
            case 0:
                va_114 = te_880 * 2;
                va_819 = te_613 + 5;
                va_74 = te_120 - 3;
                break;
            case 1:
                va_114 = te_880 + te_613;
                va_819 = te_613 * te_120;
                va_74 = te_120 / te_880;
                break;
            case 2:
                va_114 = te_880 - te_613;
                va_819 = te_613 / te_120;
                va_74 = te_120 * te_880;
                break;
            default:
                va_114 = te_880;
                va_819 = te_613;
                va_74 = te_120;
                break;
        }
        int re_124 = va_114 + va_819;
        int re_241 = va_819 + va_74;
        int re_580 = va_114 + va_74;
        System.out.println(va_114);
        System.out.println(va_819);
        System.out.println(va_74);
        System.out.println(re_124);
        System.out.println(re_241);
        System.out.println(re_580);
    }
}