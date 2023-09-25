package LeetCode;

// https://leetcode.cn/problems/odd-even-linked-list/
public class P328_奇偶链表 {
    // 标兵节点，提高效率
    public ListNode oddEvenList(ListNode head) {
        if (head == null) return head;
        ListNode odd = new ListNode();
        ListNode even = new ListNode();
        ListNode oddPre = odd;
        ListNode evenPre = even;
        int index = 1;
        ListNode cur = head;
        while (cur != null) {
            if (index % 2 == 0) {
                evenPre.next = cur;
                evenPre = cur;
            } else {
                oddPre.next = cur;
                oddPre = cur;
            }
            index++;
            cur = cur.next;
        }
        oddPre.next = even.next; // 奇-head 偶-tail
        evenPre.next = null; // 偶是末尾
        return odd.next; //返回偶
    }

}
