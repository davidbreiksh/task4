import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private final static String url = "https://sendel.ru";
    private static final String path = "src/main/resources/map.txt";
    private static final Logger logger = LogManager.getLogger("FileAppender");

    public static void main(String[] args) throws IOException {

        Set<String> links = new HashSet<>();

        RecursiveAction action = new RecursiveAction(url, url);

        new ForkJoinPool().invoke(action);
        print(url, links);

    }

    private static void print(String url, Set<String> links) throws IOException {

        if (url == null) {
            return;
        }

        int size = links.size();
        String tab = String.join("", Collections.nCopies(size, "\t"));
        String link = tab + url + "\n";
        logger.info("ссылки " + link + " Thread name : " + Thread.currentThread().getName());
        writeToFile(link);
        links.forEach(each -> {
            try {
                print(each, links);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private static void writeToFile(String data) throws IOException {
        OutputStream outputStream = new FileOutputStream((Main.path), true);
        outputStream.write(data.getBytes(), 0, data.length());
    }
}