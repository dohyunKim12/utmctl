package util;

import picocli.CommandLine;
import java.util.Optional;

public class TaskFieldConverter implements CommandLine.ITypeConverter<String> {
    @Override
    public String convert(String value) throws Exception {
        if (TaskField.isValidField(value)) {
            return TaskField.get(value);
        }
        throw new CommandLine.TypeConversionException("Invalid field name: " + value);
    }
}
