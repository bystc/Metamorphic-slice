public class Example899 {
    public static void main(String[] args) {
        int ch_361 = 3;
        double un_162 = 68.71;
        int un_458 = 112;
        int un_444 = 134;
        int[] un_794 = { 2, 4, 3 };
        int lo_219 = 0;
        for (int i_804 = 0; i_804 < 3; i_804++) {
            lo_219 += i_804 * 2;
            if (i_804 % 2 == 0) {
                lo_219 -= 1;
            }
        }
        int wh_256 = 0;
        int j_820 = 0;
        while (j_820 < 2) {
            wh_256 += j_820 * 3;
            j_820++;
        }
        int va_86 = 0, va_378 = 0, va_12 = 0;
        int te_890 = 10, te_721 = 20, te_336 = 30;
        switch (ch_361) {
            case 0:
                va_86 = te_890 * 2;
                va_378 = te_721 + 5;
                va_12 = te_336 - 3;
                break;
            case 1:
                va_86 = te_890 + te_721;
                va_378 = te_721 * te_336;
                va_12 = te_336 / te_890;
                break;
            case 2:
                va_86 = te_890 - te_721;
                va_378 = te_721 / te_336;
                va_12 = te_336 * te_890;
                break;
            default:
                va_86 = te_890;
                va_378 = te_721;
                va_12 = te_336;
                break;
        }
        int re_589 = va_86 + va_378;
        int re_80 = va_378 + va_12;
        int re_7 = va_86 + va_12;
        System.out.println(va_86);
        System.out.println(va_378);
        System.out.println(va_12);
        System.out.println(re_589);
        System.out.println(re_80);
        System.out.println(re_7);
    }
}