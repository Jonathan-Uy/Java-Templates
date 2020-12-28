import java.io.*;

/**
 * Tested on https://dmoj.ca/problem/det
 */
public class MatrixDeterminant {
    static class Reader {
        final private int BUFFER_SIZE = 1 << 16;
        private DataInputStream din;
        private byte[] buffer;
        private int bufferPointer, bytesRead;

        public Reader() {
            din = new DataInputStream(System.in);
            buffer = new byte[BUFFER_SIZE];
            bufferPointer = bytesRead = 0;
        }

        public int nextInt() throws IOException {
            int ret = 0;
            byte c = Read();
            while (c <= ' ')
                c = Read();
            boolean neg = (c == '-');
            if (neg)
                c = Read();
            do {
                ret = ret * 10 + c - '0';
            }  while ((c = Read()) >= '0' && c <= '9');
            return neg ? -ret : ret;
        }

        public long nextLong() throws IOException {
            long ret = 0;
            byte c = Read();
            while (c <= ' ')
                c = Read();
            boolean neg = (c == '-');
            if (neg)
                c = Read();
            do {
                ret = ret * 10 + c - '0';
            } while ((c = Read()) >= '0' && c <= '9');
            return neg ? -ret : ret;
        }

        private void fillBuffer() throws IOException {
            bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
            if (bytesRead == -1)
                buffer[0] = -1;
        }

        private byte Read() throws IOException {
            if (bufferPointer == bytesRead)
                fillBuffer();
            return buffer[bufferPointer++];
        }
    }

    static final long MOD = (long) (1e9+7);

    public static long inv(long A){
        A %= MOD;
        long ans = 1;
        long N = 1000000005L;
        while(N > 0){
            if((N&1) != 0)
                ans = ans * A % MOD;
            A = A * A % MOD;
            N >>= 1;
        }
        return ans;
    }

    public static void main(String[] args) throws IOException {
        Reader in = new Reader();
        int N = in.nextInt();

        long[][] A = new long[N][N];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                A[i][j] = in.nextLong();
                if(A[i][j] < 0) A[i][j] += MOD;
            }
        }

        long det = 1;
        for(int i = 0; i < N; i++){
            int maxN = i;
            for(int j = i+1; j < N; j++)
                if(A[j][i] > A[maxN][i])
                    maxN = j;

            if(maxN != i){
                for(int j = 0; j < N; j++){
                    // Slightly more efficient swap?
                    A[i][j] ^= A[maxN][j];
                    A[maxN][j] ^= A[i][j];
                    A[i][j] ^= A[maxN][j];
                }
                det = -det;
            }

            for(int j = i+1; j < N; j++){
                long x = A[j][i] * inv(A[i][i]) % MOD;
                for(int k = i; k < N; k++){
                    A[j][k] -= x * A[i][k];
                    A[j][k] = (A[j][k] + MOD) % MOD;
                }
            }
        }

        for(int i = 0; i < N; i++)
            det = (det * A[i][i]) % MOD;
        det = (det + MOD) % MOD;
        System.out.println(det);
    }
}