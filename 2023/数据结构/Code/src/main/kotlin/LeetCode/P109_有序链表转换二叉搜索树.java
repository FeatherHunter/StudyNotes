package LeetCode;

import Tree.TreeNode;

public class P109_有序链表转换二叉搜索树 {
    public TreeNode sortedListToBST(ListNode head) {
        return toBSTDfs(head);
    }

    public TreeNode toBSTDfs(ListNode head) {
        if (head == null) return null;
        ListNode left = null;
        ListNode right = head.next;
        ListNode pre = head;
        ListNode cur = head;
        if (head.next != null && head.next.next != null) {
            ListNode slow = head.next;
            ListNode fast = head.next.next;
            while (fast.next != null && fast.next.next != null) {
                pre = slow; // 记录slow的前一个
                slow = slow.next;
                fast = fast.next.next;
            }
            cur = slow; // 中间节点
            left = head;
            pre.next = null; // 左子树的结尾点为null
            right = cur.next;
        }
        TreeNode root = new TreeNode(cur.val);
        root.left = toBSTDfs(left);
        root.right = toBSTDfs(right);
        return root;
    }

}
