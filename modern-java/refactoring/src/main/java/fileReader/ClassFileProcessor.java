package fileReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

public class ClassFileProcessor {

    public String processFile(BufferedReaderProcessor processor) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/main/java/fileReader/ClassFileProcessor.java"))){
            return processor.process(reader);
        }
    }

    public void run() throws IOException {
        String twoLine = processFile((BufferedReader br) -> br.readLine() + br.readLine());
        System.out.println(twoLine);
    }
}
