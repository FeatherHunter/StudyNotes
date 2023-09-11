package Tree;

import java.util.Arrays;
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
public class Code_02_TrieTree_array {

    public static class Trie {
        static int n = 70001; // n根据空间判断
        static int m = 26;// 例如只有字符a b c，就是3个
        /**
         * 0下标数据，默认弃用。则
         * 数组内数据为0,0,0代表没有路
         * 1->2 1{2,0,0} 2{0,0,0} cnt = 2 代表目前2个数据
         * 2->3 2{0,0,3}
         * 3->4 3{0,4,0}
         */
        static int[][] trie = new int[n][m];
        static int[] pass = new int[n]; // n个节点，i对应下标的数据++，0默认弃用
        static int[] end = new int[n]; // 结束数组

        static int cnt = 1; // 每新增节点就用++cnt的数值给他。
        int size = 0; // 有多少个字符串构成的。

        public Trie() {
            // 还原数据
            for (int i = 0; i < trie.length; i++) {
                Arrays.fill(trie[i], 0);
            }
            Arrays.fill(pass, 0);
            Arrays.fill(end, 0);
        }

        public void insert(String word) {
            size++; //调用几次insert插入几个字符串，null也会算在内
            if (word == null) {
                return;
            }
            char[] arr = word.toCharArray();
            int cur = 1;
            pass[cur]++; // 头结点 ++
            for (char c : arr) { // 遍历好后
                int path = c - 'a';
                if (trie[cur][path] == 0) {
                    trie[cur][path] = ++cnt;
                }
                cur = trie[cur][path];
                pass[cur]++;
            }
            end[cur]++;
        }

        // 搜索word
        public boolean search(String word) {
            if (word == null) {
                return false;
            }
            int cur = 1;
            char[] arr = word.toCharArray();
            for (char c : arr) {
                int path = c - 'a';
                if (trie[cur][path] == 0) {
                    return false;
                }
                cur = trie[cur][path];
            }
            return end[cur] > 0; // end > 0 才代表存在该字符
        }

        public boolean startsWith(String prefix) {
            if (prefix == null) {
                return false;
            }
            int cur = 1;
            char[] arr = prefix.toCharArray();
            for (char c : arr) {
                int path = c - 'a';
                if (trie[cur][path] == 0) {
                    return false;
                }
                cur = trie[cur][path];
            }
            return pass[cur] > 0; // 有经过该字符的
        }

        public void delete(String word) {
            if (!search(word)) {
                return;
            }
            // 找到了哦
            int cur = 1;
            char[] arr = word.toCharArray();
            for (char c : arr) {
                int path = c - 'a';
                pass[trie[cur][path]]--;
                if (pass[trie[cur][path]] == 0) {
                    trie[cur][path] = 0;
                    return; // 结束了，直接斩断一切
                }
                cur = trie[cur][path];
            }
            end[cur]--; // 到最后一个节点，end--
        }
    }
}
