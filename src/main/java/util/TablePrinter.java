package util;

import java.util.HashMap;
import java.util.List;
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
        maxLengths.put("jobId", 5);
        maxLengths.put("username", 8);
        maxLengths.put("description", 11);
        maxLengths.put("licenseType", 11);
        maxLengths.put("licenseCount", 12);
        maxLengths.put("license", 7);
        maxLengths.put("directory", 9);
        maxLengths.put("uuid", 19);
        maxLengths.put("command", 7);
        maxLengths.put("shortCmd", 8);
        maxLengths.put("timelimit", 9);
        maxLengths.put("cpu", 3);
        maxLengths.put("submit", 6);
        maxLengths.put("start", 5);
        maxLengths.put("end", 3);
        maxLengths.put("status", 6);
        maxLengths.put("submittedAt", 19);
    }

    protected abstract void setMaxLengths();
    protected abstract void setFilteredMaxLengths() throws Throwable;
    protected abstract void printHeader();
    protected abstract void printFilteredHeader();
    protected abstract void printData();
    protected abstract void printFilteredData() throws Throwable;

    public void print() {
        setMaxLengths();
        printHeader();
        printData();
        System.out.println(sb);
    }

    public void printFilter() {
        try {
            setFilteredMaxLengths();
            printFilteredHeader();
            printFilteredData();
            System.out.println(sb);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
