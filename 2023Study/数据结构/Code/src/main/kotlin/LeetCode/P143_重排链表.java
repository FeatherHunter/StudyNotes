package LeetCode;

public class P143_重排链表 {
    // 1ms 击败 99.82%使用 Java 的用户
    public void reorderList(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) return;
        ListNode slow = head.next;
        ListNode fast = head.next.next;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        // slow为中间节点。
        // 1、反转链表
        ListNode pre = slow;
        ListNode cur = slow.next;
        slow.next = null;
        ListNode temp;
        while (cur != null) {
            temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        // 2、此时head和pre是两个链表的开头
        ListNode p1 = head;
        ListNode p2 = pre;
        ListNode p1next = null;
        ListNode p2next = null;
        while (p1 != null && p2 != null) {
            p1next = p1.next;
            p2next = p2.next;
            p1.next = p2;
            p2.next = p1next;
            p1 = p1next;
            p2 = p2next;
        }
    }

}
