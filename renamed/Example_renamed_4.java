public class Example691 {
    public static void main(String[] args) {
        int ch_926 = 2;
        String un_246 = "str25";
        double un_518 = 87.75;
        double un_593 = 97.00;
        int lo_421 = 0;
        for (int i_139 = 0; i_139 < 3; i_139++) {
            lo_421 += i_139 * 2;
            if (i_139 % 2 == 0) {
                lo_421 -= 1;
            }
        }
        int wh_65 = 0;
        int j_217 = 0;
        while (j_217 < 2) {
            wh_65 += j_217 * 3;
            j_217++;
        }
        int va_575 = 0, va_873 = 0, va_488 = 0;
        int te_952 = 10, te_553 = 20, te_184 = 30;
        switch (ch_926) {
            case 0:
                va_575 = te_952 * 2;
                va_873 = te_553 + 5;
                va_488 = te_184 - 3;
                break;
            case 1:
                va_575 = te_952 + te_553;
                va_873 = te_553 * te_184;
                va_488 = te_184 / te_952;
                break;
            case 2:
                va_575 = te_952 - te_553;
                va_873 = te_553 / te_184;
                va_488 = te_184 * te_952;
                break;
            default:
                va_575 = te_952;
                va_873 = te_553;
                va_488 = te_184;
                break;
        }
        int re_12 = va_575 + va_873;
        int re_299 = va_873 + va_488;
        int re_276 = va_575 + va_488;
        System.out.println(va_575);
        System.out.println(va_873);
        System.out.println(va_488);
        System.out.println(re_12);
        System.out.println(re_299);
        System.out.println(re_276);
    }
}