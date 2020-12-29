import java.io.*;
import java.util.*;

public class FenwickTree {
    static int N, M;
    static int[] A;
    static long[] bit;
    static int[] freq;

    public static int LSB(int x){
        return (x)&(-x);
    }

    public static void update(int pos, int val){
        int diff = val - A[pos];
        int old = A[pos];
        A[pos] = val;

        for(int i = pos; i <= N; i += LSB(i))
            bit[i] += diff;
        for(int i = val; i <= 100000; i += LSB(i))
            freq[i] += 1;
        for(int i = old; i <= 100000; i += LSB(i))
            freq[i] -= 1;
    }

    public static long sum(int pos){
        long sum = 0;
        for(int i = pos; i > 0; i -= LSB(i))
            sum += bit[i];
        return sum;
    }

    public static int count(int val){
        int count = 0;
        for(int i = val; i > 0; i -= LSB(i))
            count += freq[i];
        return count;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        freq = new int[100001];
        bit = new long[N+1];
        A = new int[N+1];

        /** Initialize A */
        st = new StringTokenizer(br.readLine());
        for(int i = 1; i <= N; i++){
            int x = Integer.parseInt(st.nextToken());
            A[i] = x;
        }

        /** Initialize BIT and FREQ */
        int temp;
        for(int i = 1; i<= N; i++){
            temp = i;
            while(temp <= N){
                bit[temp] += A[i]; temp += LSB(temp);
            }
            temp = A[i];
            while(temp <= 100000){
                freq[temp] += 1; temp += LSB(temp);
            }
        }

        for(int i = 0; i < M; i++){
            st = new StringTokenizer(br.readLine());
            char c = st.nextToken().charAt(0);

            if(c == 'C'){
                int x = Integer.parseInt(st.nextToken());
                int v = Integer.parseInt(st.nextToken());
                update(x, v);
            } else if(c == 'S'){
                int l = Integer.parseInt(st.nextToken());
                int r = Integer.parseInt(st.nextToken());
                System.out.println(sum(r) - sum(l-1));
            } else {
                int v = Integer.parseInt(st.nextToken());
                System.out.println(count(v));
            }
        }

    }
}