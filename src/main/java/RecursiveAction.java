import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveTask;

public class RecursiveAction extends RecursiveTask<StringBuffer> {

    Set<String> allLinks = new HashSet<>();

    public static String rootUrl;
    String url;

    String matchUrl = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)";

    public RecursiveAction(String url) {
        this.url = url;
    }

    public RecursiveAction(String url, String rootUrl) {
        this.url = url;
        RecursiveAction.rootUrl = rootUrl;
    }

    @Override
    protected StringBuffer compute() {

        Set<RecursiveAction> tasks = new HashSet<>();

        StringBuffer link = new StringBuffer();

        try {
            getLinks(tasks);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        for (RecursiveAction item : tasks) {
            link.append(item.join());
            // allLinks.addAll(item.join());
        }

        return link;
    }

    private void getLinks(Set<RecursiveAction> tasks) throws IOException {

        Document document = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).timeout(10000).get();
        Elements elements = document.select("a[href]");

        for (Element element : elements) {
            String link = element.attr("abs:href");
            if (linkFilter(link)) {
                RecursiveAction recursiveAction = new RecursiveAction(link);
                recursiveAction.fork();
                tasks.add(recursiveAction);
                allLinks.add(link);
            }
        }
    }

    private boolean linkFilter(String url) {

        return (!url.isEmpty()
                && url.startsWith(rootUrl)
                && !allLinks.contains(url)
                && !url.contains("#")
                && !url.matches(matchUrl));
    }
}