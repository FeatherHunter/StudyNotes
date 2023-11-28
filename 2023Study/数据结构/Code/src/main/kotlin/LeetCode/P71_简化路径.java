package LeetCode;

import java.util.LinkedList;

public class P71_简化路径 {
    // split分割
    public String simplifyPath(String path) {
        LinkedList<String> queue = new LinkedList<>();
        String[] arr = path.split("/");
        for (String s : arr) {
            if (s.isEmpty()) {
                continue;
            } else if (s.equals(".")) {
                continue;
            } else if (s.equals("..")) {
                if (!queue.isEmpty()) {
                    queue.removeLast();
                }
            } else {
                queue.addLast(s);
            }
        }
        StringBuilder b = new StringBuilder("/");
        while (!queue.isEmpty()) {
            b.append(queue.removeFirst());
            if (!queue.isEmpty()) {
                b.append("/"); // 非最后一次，可以加“/”
            }
        }
        return b.toString();
    }

}
