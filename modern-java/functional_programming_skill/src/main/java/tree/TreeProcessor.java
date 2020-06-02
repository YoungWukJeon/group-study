package tree;

public class TreeProcessor {
    /** 2진 탐색 트리 검색 */
    public static int lookup(String key, int defaultValue, Tree tree){
        if(tree == null){
            return defaultValue;
        }
        if(key.equals(tree.getKey())){
            return tree.getVal();
        }
        return lookup(key, defaultValue, key.compareTo(tree.getKey()) < 0 ? tree.getLeft() : tree.getRight());
    }

    /** 2진 탐색 트리 수정
     * - 찾으려는 key 가 이진 탐색 트리에 있다고 가정
     */
    public static Tree update(String key, int newValue, Tree tree){
        if(tree == null){
            tree = new Tree(key, newValue, null, null);
        }else if(key.equals(tree.getKey())){
            tree.setVal(newValue);
        }else if(key.compareTo(tree.getKey()) < 0){
            tree.setLeft(update(key, newValue, tree.getLeft()));
        }else{
            tree.setRight(update(key, newValue, tree.getRight()));
        }
        return tree;
    }

    /** 함수형을 적용한 update
     * - root 에서 업데이트 하려는 node 까지만 새로 생성한다
     */
    public static Tree functionalUpdate(String key, int newValue, Tree tree){
        if(tree == null){
            return new Tree(key, newValue, null, null);
        }else if(key.equals(tree.getKey())){
            return new Tree(key, newValue, tree.getLeft(), tree.getRight());
        }else if(key.compareTo(tree.getKey()) < 0){
            return new Tree(tree.getKey(), tree.getVal(), functionalUpdate(key, newValue, tree.getLeft()), tree.getRight());
        }else{
            return new Tree(tree.getKey(), tree.getVal(), tree.getLeft(), functionalUpdate(key, newValue, tree.getRight()));
        }
    }
}
