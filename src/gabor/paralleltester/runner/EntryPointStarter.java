package gabor.paralleltester.runner;

public class EntryPointStarter {

    public static void main(String[] args) {
        try {
            ParallelStarter.start(args);
        } catch (Exception e) {
            System.exit(-3);
        }
    }
}
