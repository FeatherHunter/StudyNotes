package LeetCode;

public class P2130_链表最大孪生和_链表 {
    // 1.找到中间节点
    // 2.反转链表
    // 3.2个链表遍历，求最大值
    // 4.还原链表
    public int pairSum(ListNode head) {
        // 1.找到中间节点，反转链表
        ListNode slow = head.next;
        ListNode fast = head.next.next;
        if (fast == null) {
            return slow.val + head.val;
        }
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        // 2、反转链表
        ListNode p2 = fast.next;
        ListNode pre = slow;
        ListNode cur = slow.next;
        ListNode temp;
        while (cur != null) {
            temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }
        slow.next = null; // 准备好节点
        // 3、遍历
        ListNode p1 = head;
        int max = Integer.MIN_VALUE;
        while (p1 != null && p2 != null) {
            max = Math.max(max, p1.val + p2.val);
            p1 = p1.next;
            p2 = p2.next;
        }
        // 4、还原
        pre = null; // p2反转后，末尾指向null
        cur = p2;
        while (cur != null) {
            temp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = temp;
        }

        return max;
    }

}
