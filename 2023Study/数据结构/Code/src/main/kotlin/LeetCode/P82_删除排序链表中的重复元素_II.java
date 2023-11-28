package LeetCode;

public class P82_删除排序链表中的重复元素_II {
    // 0ms 击败 100.00%使用 Java 的用户
    public ListNode deleteDuplicates(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode flag = new ListNode();
        flag.next = head;
        ListNode pre = flag;
        ListNode cur = head; // cur也是flag
        while (cur != null) {
            if (cur.next == null || cur.val != cur.next.val) {
                //cur是独一无二的
                pre.next = cur;
                pre = cur;
            } else {
                while (cur.next != null && cur.val == cur.next.val) {
                    cur = cur.next;
                }
            }
            cur = cur.next; // cur为当前节点的下一个
        }
        pre.next = cur; // 末尾需要收尾
        return flag.next;
    }

}
