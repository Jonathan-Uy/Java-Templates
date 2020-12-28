import java.io.*;
import java.util.*;

/**
 * Tested on https://dmoj.ca/problem/dmopc20c1p4
 */
public class MatrixExponentiation {
    static int T;
    static long K;
    static long MOD = (long) (1e9+7);

    /**
     * Generates the transform matrix.
     */
    public static long[][] generateTransform(){
        long[][] M = new long[T+1][T+1];
        for(int i = 0; i < T; i++)
            M[i][i+1] = 1;
        M[T][T] = 1;
        M[T][0] = K;
        return M;
    }

    /**
     * Generates the identity matrix.
     */
    public static long[][] generateIdentity(){
        long[][] M = new long[T+1][T+1];
        for(int i = 0; i <= T; i++)
            M[i][i] = 1;
        return M;
    }

    /**
     * Multiplies matrix A by matrix B.
     * Stores the result in A.
     * Runs in O(N^3).
     */
    public static void multiply(long a[][], long b[][]) {
        long[][] c = new long[T+1][T+1];
        for(int i = 0; i <= T; i++){
            for(int j = 0; j <= T; j++){
                for(int k = 0; k <= T; k++){
                    c[i][j] += a[i][k] * b[k][j];
                    c[i][j] %= MOD;
                }
            }
        }
        for(int i = 0; i <= T; i++)
            for(int j = 0; j <= T; j++)
                a[i][j] = c[i][j];
    }

    /**
     * Computes the transform matrix to the exponent of Z in O(N^3 log2(Z)).
     * Runs in O(N^3 log2(Z)).
     */
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        long N = Long.parseLong(st.nextToken());
        K = Long.parseLong(st.nextToken());
        T = Integer.parseInt(st.nextToken());
        long C = Long.parseLong(st.nextToken());

        long[][] M = generateTransform();
        long[][] A = generateIdentity();

        N--;
        while(N > 0){
            if((N&1) != 0)
                multiply(A, M);
            multiply(M, M);
            N >>= 1;
        }

        long ans = 0;
        for(int i = 0; i < T; i++){
            ans += C * (A[T][i]) % MOD;
            ans %= MOD;
        }
        ans += (C * 2 * A[T][T]) % MOD;
        ans %= MOD;
        System.out.println(ans);
    }
}