package com.example.likelionfinalproject.service;

public class TestService {

    public int addDigits(int num) {
        int sum = 0;
        /*6563 이면 */
        while (num != 0) {
            sum += num % 10; // 3 + 6 + 5 + 6
            num /= 10; // 656 -> 65 -> 6
        }

        return sum;
    }
}
