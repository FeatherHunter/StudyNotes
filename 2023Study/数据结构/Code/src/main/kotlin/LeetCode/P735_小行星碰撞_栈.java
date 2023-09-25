package LeetCode;

public class P735_小行星碰撞_栈 {
    static int[] stack = new int[10001];
    static int top = 0;
    static int valid = 0;

    public int[] asteroidCollision(int[] asteroids) {
        top = valid = 0;
        for (int i = 0; i < asteroids.length; i++) {
            if (asteroids[i] >= 0) {
                stack[top++] = asteroids[i];
            } else {
                while (top > valid && stack[top - 1] < -asteroids[i]) {
                    top--;
                }
                // 找到了 >= 该星星体积的星球，该星星损毁了，不要加入
                if (top == valid) {
                    // 清空了栈，还是没遇到能打的
                    valid++; // 栈底部增加
                    stack[top++] = asteroids[i]; // 加入该星星
                } else {
                    if (stack[top - 1] == -asteroids[i]) {
                        top--; // 销毁该相同星球
                    }
                }
            }
        }
        int[] res = new int[top];
        for (int i = 0; i < top; i++) {
            res[i] = stack[i];
        }
        return res;
    }

}
