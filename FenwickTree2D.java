import java.io.*;
import java.util.*;

/**
 * Tested on https://dmoj.ca/problem/ioi01p1/
 */
public class FenwickTree2D {
    static int N;
    static int[][] ds;

    public static int LSB(int x){
        return (x)&(-x);
    }

    public static void update(int x, int y, int val){
        for(int i = x; i <= N+1; i += LSB(i))
            for(int j = y; j <= N+1; j += LSB(j))
                ds[i][j] += val;
    }

    public static int query(int x, int y){
        int sum = 0;
        for(int i = x; i > 0; i -= LSB(i))
            for(int j = y; j > 0; j -= LSB(j))
                sum += ds[i][j];
        return sum;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        st.nextToken();

        N = Integer.parseInt(st.nextToken());
        ds = new int[N+2][N+2];
        while(true){
            st = new StringTokenizer(br.readLine());
            int type = Integer.parseInt(st.nextToken());
            if(type == 1){
                int x = Integer.parseInt(st.nextToken()) + 2;
                int y = Integer.parseInt(st.nextToken()) + 2;
                int A = Integer.parseInt(st.nextToken());
                update(x, y, A);
            } else if(type == 2){
                int l = Integer.parseInt(st.nextToken()) + 2;
                int b = Integer.parseInt(st.nextToken()) + 2;
                int r = Integer.parseInt(st.nextToken()) + 2;
                int t = Integer.parseInt(st.nextToken()) + 2;
                System.out.println(query(r,t)-query(r,b-1)-query(l-1,t)+query(l-1,b-1));
            } else {
                return;
            }
        }
    }
}