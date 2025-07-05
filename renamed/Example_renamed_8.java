public class Example465 {
    public static void main(String[] args) {
        int ch_581 = 3;
        String un_847 = "str36";
        String un_438 = "str52";
        int un_111 = 161;
        int[] un_618 = { 8, 8, 5 };
        int va_404 = 0, va_773 = 0, va_70 = 0;
        int te_542 = 10, te_222 = 20, te_900 = 30;
        switch (ch_581) {
            case 0:
                va_404 = te_542 * 2;
                va_773 = te_222 + 5;
                va_70 = te_900 - 3;
                break;
            case 1:
                va_404 = te_542 + te_222;
                va_773 = te_222 * te_900;
                va_70 = te_900 / te_542;
                break;
            case 2:
                va_404 = te_542 - te_222;
                va_773 = te_222 / te_900;
                va_70 = te_900 * te_542;
                break;
            default:
                va_404 = te_542;
                va_773 = te_222;
                va_70 = te_900;
                break;
        }
        int re_324 = va_404 + va_773;
        int re_615 = va_773 + va_70;
        int re_514 = va_404 + va_70;
        System.out.println(va_404);
        System.out.println(va_773);
        System.out.println(va_70);
        System.out.println(re_324);
        System.out.println(re_615);
        System.out.println(re_514);
    }
}