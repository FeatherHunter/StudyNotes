package LeetCode;

/**
 * 16ms 击败 99.96%使用 Java 的用户
 */
public class P933_最近的请求次数_队列 {
    static class RecentCounter {

        static int[] queue = new int[10001];
        static int head = 0;
        static int tail = 0;

        public RecentCounter() {
            head = tail = 0;
        }

        public int ping(int t) {
            queue[tail++] = t;
            while (head < tail){ // head的移动，提高性能效率
                if(queue[head] >= t - 3000){
                    break;
                }
                head++;
            }
            return tail - head;
        }
    }
}
