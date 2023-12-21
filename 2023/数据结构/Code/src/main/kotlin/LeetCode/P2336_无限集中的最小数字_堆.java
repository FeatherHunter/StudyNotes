package LeetCode;

import java.util.PriorityQueue;

public class P2336_无限集中的最小数字_堆 {
    class SmallestInfiniteSet {
        PriorityQueue<Integer> heap;

        public SmallestInfiniteSet() {
            heap = new PriorityQueue<>(1000);
            for (int i = 1; i <= 1000; i++) {
                heap.add(i);
            }
        }

        public int popSmallest() {
            if (!heap.isEmpty()) {
                return heap.poll();
            }
            return -1;
        }

        public void addBack(int num) {
            if (!heap.contains(num)) {
                heap.add(num);
            }
        }
    }

}
