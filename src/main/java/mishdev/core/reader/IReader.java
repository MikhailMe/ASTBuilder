package mishdev.core.reader;

import org.jetbrains.annotations.NotNull;

public interface IReader {
    void init(@NotNull String fileName);
    void read(@NotNull String program);
}
