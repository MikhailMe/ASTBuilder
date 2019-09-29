package mishdev.core;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reader {

    public List<String> read(@NotNull final String fileName) {
        List<String> programText = new ArrayList<>();
        try (FileReader fr = new FileReader(fileName);
             BufferedReader bis = new BufferedReader(fr)) {
            String line = bis.readLine();
            while (line != null) {
                programText.add(line);
                line = bis.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return programText
                .stream()
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }
}
