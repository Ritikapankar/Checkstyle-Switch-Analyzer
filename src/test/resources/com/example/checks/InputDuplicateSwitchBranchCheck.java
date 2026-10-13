package com.example.Checkstyle.Switch.Analyzer.checkstyle;

public class InputDuplicateSwitchBranchCheck {
    public void process(int type, int price) {
        int total = 0;
        switch (type) {
            case 1:
                total = price + tax;
                total++;
                total--;
                break;
            case 2:
                // Yeh block case 1 se bilkul duplicate hai (3 meaningful statements)
                total = price + tax;
                total++;
                total--;
                break;
            default:
                total = 0;
                break;
        }
    }
    private int tax = 10;
}