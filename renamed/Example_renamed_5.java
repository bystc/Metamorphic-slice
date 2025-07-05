public class Example439 {
    public static void main(String[] args) {
        int ch_702 = 1;
        String un_203 = "str68";
        double un_606 = 78.58;
        int[] un_712 = { 3, 3, 7 };
        int va_148 = 0, va_88 = 0, va_210 = 0;
        int te_559 = 10, te_666 = 20, te_563 = 30;
        switch (ch_702) {
            case 0:
                va_148 = te_559 * 2;
                va_88 = te_666 + 5;
                va_210 = te_563 - 3;
                break;
            case 1:
                va_148 = te_559 + te_666;
                va_88 = te_666 * te_563;
                va_210 = te_563 / te_559;
                break;
            case 2:
                va_148 = te_559 - te_666;
                va_88 = te_666 / te_563;
                va_210 = te_563 * te_559;
                break;
            default:
                va_148 = te_559;
                va_88 = te_666;
                va_210 = te_563;
                break;
        }
        int re_604 = va_148 + va_88;
        int re_712 = va_88 + va_210;
        int re_238 = va_148 + va_210;
        System.out.println(va_148);
        System.out.println(va_88);
        System.out.println(va_210);
        System.out.println(re_604);
        System.out.println(re_712);
        System.out.println(re_238);
    }
}