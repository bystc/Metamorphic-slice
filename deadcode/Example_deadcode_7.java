public class Example49 {

    public static void main(String[] args) {
        for (int i = 0; i < 0; i++) {
            if (val1 > 0) {
                val1 = val1 - 2;
            }
            val1 = val1 * 7 + 30;
        }
        for (int i = 0; i < 0; i++) {
            val1 = val1 + 88;
            if (val1 > 0) {
                val1 = val1 - 17;
            }
        }
        if (false) {
            val1 = val1 + 44;
        }
        int choice = 1;
        int unrelatedInt0 = 141;
        double unrelatedDouble1 = 98.41;
        int[] unrelatedArr2 = { 6, 5, 5 };
        int val1 = 0, val2 = 0, val3 = 0;
        int temp1 = 10, temp2 = 20, temp3 = 30;
        switch(choice) {
            case 0:
                val1 = temp1 * 2;
                val2 = temp2 + 5;
                val3 = temp3 - 3;
                break;
            case 1:
                val1 = temp1 + temp2;
                val2 = temp2 * temp3;
                val3 = temp3 / temp1;
                break;
            case 2:
                val1 = temp1 - temp2;
                val2 = temp2 / temp3;
                val3 = temp3 * temp1;
                break;
            default:
                val1 = temp1;
                val2 = temp2;
                val3 = temp3;
                break;
        }
        int result1 = val1 + val2;
        int result2 = val2 + val3;
        int result3 = val1 + val3;
        System.out.println(val1);
        System.out.println(val2);
        System.out.println(val3);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
    }
}
