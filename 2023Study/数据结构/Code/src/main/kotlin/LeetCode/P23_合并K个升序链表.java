package LeetCode;

public class P23_合并K个升序链表 {
    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public static ListNode mergeKLists(ListNode[] lists) {
        ListNode flag = new ListNode();
        ListNode pre = flag;
        while (true){
            ListNode min = new ListNode(Integer.MAX_VALUE);
            int index = -1;
            for (int i = 0; i < lists.length; i++) {
                if(lists[i] != null && lists[i].val < min.val){
                    min = lists[i];
                    index = i;
                }
            }
            // 找到最小的节点la
            if(min.val == Integer.MAX_VALUE){
                return flag.next;
            }
            pre.next = min;
            lists[index] = min.next; // 往下赋值，直到为null
            pre = min;
        }
    }
}
