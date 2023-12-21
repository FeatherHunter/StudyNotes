package LeetCode;

public class P116_填充每个节点的下一个右侧节点指针 {
    public Node connect(Node root) {
        if (root == null) return null;
        LinkedList<Node> queue = new LinkedList();
        queue.addFirst(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            Node pre = queue.poll();
            if (pre.left != null) {
                queue.offer(pre.left);
            }
            if (pre.right != null) {
                queue.offer(pre.right);
            }
            for (int i = 0; i < size - 1; i++) {
                Node cur = queue.poll();
                if (cur.left != null) {
                    queue.offer(cur.left);
                }
                if (cur.right != null) {
                    queue.offer(cur.right);
                }
                pre.next = cur;
                pre = cur;
            }
            pre.next = null;
        }
        return root;
    }

}
