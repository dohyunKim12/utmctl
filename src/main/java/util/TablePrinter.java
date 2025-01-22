package util;

import java.util.HashMap;
import java.util.Map;

public abstract class TablePrinter {
    protected final Map<String, Integer> maxLengths;
    protected final StringBuilder sb;

    protected TablePrinter() {
        this.maxLengths = new HashMap<>();
        this.sb = new StringBuilder();
        initializeMaxLengths();
    }

    private void initializeMaxLengths() {
        maxLengths.put("taskId", 6);
        maxLengths.put("username", 8);
        maxLengths.put("licenseType", 11);
        maxLengths.put("licenseCount", 12);
        maxLengths.put("license", 7);
        maxLengths.put("directory", 9);
        maxLengths.put("uuid", 4);
        maxLengths.put("command", 7);
        maxLengths.put("shortCmd", 8);
        maxLengths.put("timeout", 7);
        maxLengths.put("cpu", 3);
        maxLengths.put("submit", 6);
        maxLengths.put("start", 5);
        maxLengths.put("end", 3);
        maxLengths.put("status", 6);
    }

    protected abstract void setMaxLengths();
    protected abstract void printHeader();
    protected abstract void printData();

    public void print() {
        setMaxLengths();
        printHeader();
        printData();
        System.out.println(sb);
    }

}
