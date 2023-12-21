package LeetCode;

// https://leetcode.cn/problems/delete-the-middle-node-of-a-linked-list/
public class P2095_删除链表的中间节点 {
    public ListNode deleteMiddle(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode pre = head;
        ListNode slow = head.next;
        ListNode fast = head.next.next; // 可能空，可能不为空
        while (fast != null && fast.next != null) {
            pre = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        // slow就是需要删除的节点
        pre.next = slow.next;
        return head;
    }

}
