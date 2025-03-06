package util;

import picocli.CommandLine;

public class MemoryConverter implements CommandLine.ITypeConverter<Integer> {

    @Override
    public Integer convert(String value) throws Exception {
        String input = value.trim().toUpperCase();
        long multiplier;

        if (input.matches(".*\\d+[KMGT]?B?$")) {
            if (input.endsWith("TB") || input.endsWith("T")) {
                multiplier = 1024 * 1024;
                input = input.replaceAll("(TB|T)$", "");
            } else if (input.endsWith("GB") || input.endsWith("G")) {
                multiplier = 1024;
                input = input.replaceAll("(GB|G)$", "");
            } else if (input.endsWith("MB") || input.endsWith("M")) {
                multiplier = 1;
                input = input.replaceAll("(MB|M)$", "");
            } else if (input.endsWith("KB") || input.endsWith("K")) {
                multiplier = 1 / 1024; // KB → MB
                input = input.replaceAll("(KB|K)$", "");
            } else {
                multiplier = 1;
                input = input.replaceAll("MB?$", "");
            }
            try {
                double val = Double.parseDouble(input.trim());
                return (int) Math.ceil(val * multiplier);
            } catch (NumberFormatException e) {
                throw new CommandLine.TypeConversionException("Invalid memory size: " + input);
            }
        } else {
            throw new CommandLine.TypeConversionException("Invalid memory format: " + input);
        }
    }
}
