package tree;

public class Main {
    // 책에서 나온 트리를 생성하는 예제
    private static Tree buildTreeByBook(){
        Tree alan = new Tree("Alan", 50, null, null);
        Tree georgie = new Tree("Georgie", 23, null, null);
        Tree emily = new Tree("Emily", 20, alan, georgie);

        Tree raoul = new Tree("raoul", 23, null, null);
        Tree tian = new Tree("Tian", 29, null, raoul);

        Tree marry = new Tree("Marry", 22, emily, tian);
        return marry;
    }

    private static void updateTest(){
        Tree tree = buildTreeByBook();

        Tree newTree = TreeProcessor.update("Emily", 50, tree);

        System.out.println(TreeProcessor.lookup("Emily", 0, tree));
        System.out.println(TreeProcessor.lookup("Emily", 0, newTree));
    }

    private static void functionalUpdateTest(){
        Tree tree = buildTreeByBook();

        Tree newTree = TreeProcessor.functionalUpdate("Emily", 50, tree);

        System.out.println(TreeProcessor.lookup("Emily", 0, tree));
        System.out.println(TreeProcessor.lookup("Emily", 0, newTree));
    }

    public static void main(String[] args){
//        updateTest();
        functionalUpdateTest();
    }
}
