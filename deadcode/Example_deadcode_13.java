public class Example120 {

    public static void main(String[] args) {
        if (false) {
            val1 = val1 + 28;
            for (int j = 0; j < 5; j++) {
                val1 = val1 + j;
            }
        }
        for (int i = 0; i < 0; i++) {
            val1 = val1 + 54;
        }
        int choice = 3;
        int[] unrelatedArr0 = { 2, 4, 5 };
        int unrelatedInt1 = 112;
        String unrelatedStr2 = "str6";
        int unrelatedInt3 = 194;
        double unrelatedDouble4 = 87.23;
        int loopCounter = 0;
        for (int i = 0; i < 3; i++) {
            loopCounter += i * 2;
            if (i % 2 == 0) {
                loopCounter -= 1;
            }
        }
        int whileCounter = 0;
        int j = 0;
        while (j < 2) {
            whileCounter += j * 3;
            j++;
        }
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
