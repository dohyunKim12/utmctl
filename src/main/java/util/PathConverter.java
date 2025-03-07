package util;

import picocli.CommandLine;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter implements CommandLine.ITypeConverter<Path> {
    @Override
    public Path convert(String value) throws Exception {
        Path path = Paths.get(value);
        if (!Files.exists(path)) {
            throw new CommandLine.ParameterException(null,
                    String.format("Error: The file '%s' does not exist.", value));
        }
        if (!Files.isReadable(path)) {
            throw new CommandLine.ParameterException(null,
                    String.format("Error: The file '%s' is not readable.", value));
        }
        if (Files.isDirectory(path)) {
            throw new CommandLine.ParameterException(null,
                    String.format("Error: The path '%s' is a directory, not a file.", value));
        }
        if (Files.size(path) == 0) {
            throw new CommandLine.ParameterException(null,
                    String.format("Error: The file '%s' is empty.", value));
        }
        return path;
    }
}
