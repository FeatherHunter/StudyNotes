package LeetCode;

import java.util.PriorityQueue;

public class P2462_雇佣K位工人的总代价 {
    public long totalCost(int[] costs, int k, int candidates) {
        PriorityQueue<Integer> lheap = new PriorityQueue<>();
        PriorityQueue<Integer> rheap = new PriorityQueue<>();
        int l = 0;
        int r = costs.length - 1;
        while (l < candidates && l <= r){
            lheap.offer(costs[l]);
            l++;
        }
        int i = 0;
        while (i < candidates && r >= l){
            rheap.offer(costs[r]);
            i++;
            r--;
        }
        long ans = 0;
        // k轮
        for (int j = 0; j < k; j++){
            if(rheap.isEmpty()){
                int a = lheap.poll();
                ans += a;
            }else if(lheap.isEmpty()){
                int b = rheap.poll();
                ans += b;
            }else{
                int a = lheap.peek();
                int b = rheap.peek();
                if(a <= b){
                    ans += a;
                    lheap.poll();
                    if (l <= r){
                        lheap.offer(costs[l]);
                        l++;
                    }
                }else{
                    ans += b;
                    rheap.poll();
                    if (l <= r){
                        rheap.offer(costs[r]);
                        r--;
                    }
                }
            }
        }
        return ans;
    }
}
