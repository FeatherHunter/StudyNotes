package Tree;

import java.io.StreamTokenizer;
import java.util.HashMap;

/**
 * @Time 2023年9月11日
 * 前缀树，组成部分：Node节点，Trie树
 * Trie树具有方法操作：
 * 1. insert 插入
 * 2. search 字符串加入过几次
 * 3. delete 删除
 * 4. pre 找到以参数作为前缀的个数
 * 5. findMaxPrefix 找到最大的前缀
 */
public class Code_02_TrieTree {
    // 小写字母
    public static class Node {
        int pass = 0;
        int end = 0;
        Node[] nexts = new Node[26];
        HashMap<Integer, Node> nextMap = new HashMap<>();
    }

    public static class Trie {
        Node root = new Node();
        int size = 0; // 有多少个字符串构成的。

        public void insert(String word) {
            size++; //调用几次insert插入几个字符串，null也会算在内
            if (word == null) {
                return;
            }
            char[] arr = word.toCharArray();
            Node cur = root;
            cur.pass++;
            for (char c : arr) {
                if (cur.nexts[c - 'a'] == null) {
                    cur.nexts[c - 'a'] = new Node();
                }
                cur = cur.nexts[c - 'a'];
                cur.pass++;
            }
            cur.end++;
        }

        // 搜索word加入过几次
        public int search(String word) {
            if (word == null) {
                return 0;
            }
            Node cur = root;
            char[] arr = word.toCharArray();
            for (char c : arr) {
                if (cur.nexts[c - 'a'] == null) {
                    return 0;
                }
                cur = cur.nexts[c - 'a'];
            }
            return cur.end;
        }

        public int pre(String word) {
            if (word == null) {
                return 0;
            }
            Node cur = root;
            char[] arr = word.toCharArray();
            for (char c : arr) {
                if (cur.nexts[c - 'a'] == null) {
                    return 0;
                }
                cur = cur.nexts[c - 'a'];
            }
            return cur.pass;
        }

        // 找到已有字符串中，包含的最大公共前缀。
        public String findMaxPrefix() {
            Node cur = root;
            int index = 0;
            String res = "";
            while (true) {
                // 这是在干嘛？遍历cur的next数组，找到第一个非空的路径，按这个顺序找下去哦。找到了末尾，或者，
                for (index = 0; index < cur.nexts.length; index++) {
                    if (cur.nexts[index] != null) {
                        break;
                    }
                }
                if (index == cur.nexts.length || cur.nexts[index].pass != size) { // 代表要么结束了，要么两个节点
                    return res;
                }
                cur = cur.nexts[index];
                res += (char) (index + 'a');
            }
        }

        public void delete(String word) {
            if (search(word) < 1) {
                return;
            }
            // 找到了哦
            Node cur = root;
            char[] arr = word.toCharArray();
            for (char c : arr) {
                cur.nexts[c - 'a'].pass--;
                if (cur.nexts[c - 'a'].pass == 0) {
                    cur.nexts[c - 'a'] = null;
                    return; // 结束了，直接斩断一切
                }
                cur = cur.nexts[c - 'a'];
            }
            cur.end--; // 到最后一个节点，end--
        }
    }
}
