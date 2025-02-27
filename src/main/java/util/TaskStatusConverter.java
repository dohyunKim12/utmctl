package util;

import picocli.CommandLine;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import config.Global.TaskStatus;

public class TaskStatusConverter implements CommandLine.ITypeConverter<List<TaskStatus>> {

    @Override
    public List<TaskStatus> convert(String value) {
        return Arrays.stream(value.split("[,\\s]+"))
                .map(String::trim)
                .map(TaskStatus::fromString)
                .collect(Collectors.toList());
    }
}
