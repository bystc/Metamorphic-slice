public class Example164 {

    public static void main(String[] args) {
        int choice = 1;
        int[] unrelatedArr0 = { 8, 9, 7 };
        int[] unrelatedArr1 = { 5, 3, 9 };
        int unrelatedInt2 = 193;
        int loopCounter = -5;
        for (int i = 2; i < 3; i++) {
            loopCounter += i - 2;
            if (i % 2 == 0) {
                loopCounter -= 1;
            }
        }
        int whileCounter = 3;
        int j = -2;
        while (j < 2) {
            whileCounter += j / 3;
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
