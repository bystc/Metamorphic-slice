public class Example744 {
    public static void main(String[] args) {
        int ch_340 = 0;
        int[] un_743 = { 2, 0, 8 };
        int un_940 = 193;
        String un_542 = "str98";
        int va_604 = 0, va_246 = 0, va_854 = 0;
        int te_545 = 10, te_208 = 20, te_544 = 30;
        switch (ch_340) {
            case 0:
                va_604 = te_545 * 2;
                va_246 = te_208 + 5;
                va_854 = te_544 - 3;
                break;
            case 1:
                va_604 = te_545 + te_208;
                va_246 = te_208 * te_544;
                va_854 = te_544 / te_545;
                break;
            case 2:
                va_604 = te_545 - te_208;
                va_246 = te_208 / te_544;
                va_854 = te_544 * te_545;
                break;
            default:
                va_604 = te_545;
                va_246 = te_208;
                va_854 = te_544;
                break;
        }
        int re_514 = va_604 + va_246;
        int re_192 = va_246 + va_854;
        int re_336 = va_604 + va_854;
        System.out.println(va_604);
        System.out.println(va_246);
        System.out.println(va_854);
        System.out.println(re_514);
        System.out.println(re_192);
        System.out.println(re_336);
    }
}