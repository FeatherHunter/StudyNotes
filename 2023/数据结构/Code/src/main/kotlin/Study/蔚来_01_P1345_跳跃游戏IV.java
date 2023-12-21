package Study;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 解题思路：宽度优先遍历（层序遍历）
 */
public class 蔚来_01_P1345_跳跃游戏IV{

    public int minJumps(int[] arr) {
        int n = arr.length;
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            if(!map.containsKey(arr[i])){
                map.put(arr[i], new ArrayList<>());
            }
            ArrayList<Integer> list = map.get(arr[i]);
            list.add(i);
        }
        boolean[] visited = new boolean[n]; // 访问过的不再访问
        int[] queue = new int[n];
        int l = 0;
        int r = 0;
        queue[r++] = 0;
        visited[0] = true;
        int jump = 0;
        while (l != r){// 队列中有数据
            int temp = r;
            for (;l < temp; l++){ // 弹出
                int cur = queue[l]; // 队列头部取出
                if (cur == n - 1){
                    return jump; // 结束啦
                }
                if(cur + 1 < n && !visited[cur + 1]){
                    queue[r++] = cur + 1;
                    visited[cur + 1] = true;
                }
                if(cur - 1 >= 0 && !visited[cur - 1]){
                    queue[r++] = cur - 1;
                    visited[cur - 1] = true;
                }
                ArrayList<Integer> list = map.get(arr[cur]); // 拿到队列下标
                for (Integer same : list) {
                    if(same != cur && !visited[same]){
                        queue[r++] = same;
                        visited[same] = true;
                    }
                }
                map.get(arr[cur]).clear(); // 减树枝，后面不需要判断了
            }
            jump++; // 上面这些情况都是走了一个步骤
        }
        return -1;
    }

    public int minJumps2(int[] arr) {
        return zuo(arr, 0, new HashMap<>());

    }
    // 当前位置i，开始跳跃到末尾需要的最少步数
    public int zuo(int[] arr, int i, HashMap<Integer,Integer> map){
        if(arr.length == 1){
            return 0;
        }
        if(i >= arr.length){
            return -1;
        }
        if(i < 0){
            return -1;
        }

        if(map.containsKey(i)){
            return map.get(i);
        }
        int z1 = zuo(arr, i + 1, map);
        int p1 = 1 + z1;
        int z2 = zuo(arr, i - 1, map);
        int p2 = 1 + z2;
        int target = -1;
        for (int j = arr.length - 1; j > i; j--) {
            if(arr[j] == arr[i]){
                target = j;
            }
        }
        int z3 = zuo(arr, target, map);
        int p3 = 1 + z3;

        int ans = (z1 == -1)?p2 : ((z2 == -1) ? p1 : Math.min(p1, p2));
        if(z3 != -1){
            ans = Math.min(ans, p3);
        }
        map.put(i, ans);
        return ans;
    }
}
