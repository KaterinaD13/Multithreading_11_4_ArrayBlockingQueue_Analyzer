import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> A = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> B = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> C = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        String[] texts = new String[10_000];

        new Thread(() -> {
            for (int i = 0; i < texts.length; i++) {
                String text = generateText("abc", 100_000);
                try {
                    A.put(text);
                    B.put(text);
                    C.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Thread a = new Thread(() -> {
            char letter = 'a';
            int maxA = findMaxCharCount(A, letter);
            System.out.println("Строка с самым большим количеством символов 'a': " + maxA);
        });
        a.start();

        Thread b = new Thread(() -> {
            char letter = 'b';
            int maxB = findMaxCharCount(B, letter);
            System.out.println("Строка с самым большим количеством символов 'b': " + maxB);
        });
        b.start();

        Thread c = new Thread(() -> {
            char letter = 'c';
            int maxC = findMaxCharCount(C, letter);
            System.out.println("Строка с самым большим количеством символов 'c': " + maxC);
        });
        c.start();

        a.join();
        b.join();
        c.join();
    }

    private static int findMaxCharCount(BlockingQueue queue, char letter) {
        int count = 0;
        int max = 0;
        try {
            for (int i = 0; i < 10_000; i++) {
                String text = (String) queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                }
                count = 0;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
