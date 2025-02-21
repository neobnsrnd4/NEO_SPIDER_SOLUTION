package neo.spider.solution.flowcontrol.trie;

import java.util.HashMap;
import java.util.Map;

class BulkheadNode {
    Map<String, BulkheadNode> children = new HashMap<>();
    boolean isEndOfPattern;
    boolean hasWildCard;
}

public class BulkheadTrie {
    private final BulkheadNode root = new BulkheadNode();

    public void insert(String pattern) {
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1); // 첫 번째 "/" 제거
        }
        String[] parts = pattern.split("/");
        BulkheadNode node = root;
        for (String part : parts) {
            if (part.equals("*")) {
                node.hasWildCard = true;
                return;
            }
            node.children.putIfAbsent(part, new BulkheadNode());
            node = node.children.get(part);
        }
        node.isEndOfPattern = true;
    }

    public String search(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1); // 첫 번째 "/" 제거
        }
        String[] parts = path.split("/");
        String result = searchHelper(root, parts, 0, new StringBuilder(), "");
        return result.isEmpty() ? null : result;  // 🔥 빈 문자열이 아닌 null 반환하도록 수정
    }

    private String searchHelper(BulkheadNode node, String[] parts, int index, StringBuilder matched, String lastValidMatch) {
        // 🚀 만약 현재 노드가 와일드카드를 가지면, 해당 패턴을 저장
        if (node.hasWildCard) {
            lastValidMatch = matched.isEmpty() ? "/" : matched + "/*";
        }

        // 탐색이 끝나면 가장 긴 매칭된 패턴 반환
        if (index == parts.length) {
            return node.isEndOfPattern ? matched.toString() : lastValidMatch;
        }

        String part = parts[index];

        // 다음 노드로 탐색 진행
        if (node.children.containsKey(part)) {
            int prevLength = part.length();
            if (prevLength > 0) matched.append("/");
            matched.append(part);

            String newLastValidMatch = node.isEndOfPattern ? matched.toString() : lastValidMatch;
            String result = searchHelper(node.children.get(part), parts, index + 1, matched, newLastValidMatch);

            matched.setLength(prevLength);
            return result;
        }

        // 🔹 매칭이 실패하면, 현재까지 찾은 가장 긴 패턴 반환 (와일드카드 포함)
        return lastValidMatch;
    }

    public boolean delete(String pattern){
        if (pattern.startsWith("/")){
            pattern = pattern.substring(1);
        }
        String[] parts = pattern.split("/");
        return deleteHelper(root, parts, 0);
    }

    private boolean deleteHelper(BulkheadNode node, String[] parts, int index) {
        if (index == parts.length){
            if (!node.isEndOfPattern) return false;

            node.isEndOfPattern = false;
            return node.children.isEmpty();
        }

        String part = parts[index];
        if (!node.children.containsKey(part)) {
            return false;
        }

        boolean shouldDeleteChild = deleteHelper(node.children.get(part), parts, index + 1);
        if (shouldDeleteChild) {
            node.children.remove(part);
            return !node.isEndOfPattern && node.children.isEmpty();
        }
        return false;
    }

//
//    // insert 후 전체 Trie 구조를 출력하는 함수 추가
//    public void printTrie() {
//        printTrieHelper(root, new StringBuilder(), 0);
//    }
//
//    private void printTrieHelper(BulkheadNode node, StringBuilder path, int level) {
//        if (node.isEndOfPattern) {
//            System.out.println("✅ Stored Pattern: " + path.toString());
//        }
//        if (node.hasWildCard) {
//            System.out.println("⭐ Wildcard Pattern: " + path.toString() + "/*");
//        }
//        for (String key : node.children.keySet()) {
//            int prevLength = path.length();
//            if (prevLength > 0) path.append("/");
//            path.append(key);
//            printTrieHelper(node.children.get(key), path, level + 1);
//
//            path.setLength(prevLength);
//        }
//    }
}
