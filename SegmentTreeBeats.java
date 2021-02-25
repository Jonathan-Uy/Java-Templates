import java.io.*;
import java.util.*;

/**
 * Tested on http://acm.hdu.edu.cn/showproblem.php?pid=5306
 */
public class SegmentTreeBeats {
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

        public void close() throws IOException {
            if (din == null)
                return;
            din.close();
        }
    }

    static class SegTree {
        int N;
        int[] lo, hi, tag, max1, max2, count;
        long[] sum;

        public SegTree(int N, int[] A){
            this.N = N;
            lo = new int[4*N];
            hi = new int[4*N];
            tag = new int[4*N];
            max1 = new int[4*N];
            max2 = new int[4*N];
            count = new int[4*N];
            sum = new long[4*N];
            initialize(1, 1, N, A);
            Arrays.fill(tag, Integer.MAX_VALUE);
        }

        private void apply(int i, int v){
            if(max1[i] <= v)    return;
            tag[i] = Math.min(tag[i], v);
            sum[i] -= (long) (max1[i]-v) * count[i];
            max1[i] = v;
        }

        private void pushDown(int i){
            if(tag[i] != Integer.MAX_VALUE){
                apply(2*i, tag[i]);
                apply(2*i+1, tag[i]);
                tag[i] = Integer.MAX_VALUE;
            }
        }

        private void pullUp(int i){
            if(lo[i] == hi[i])  return;
            sum[i] = sum[2*i] + sum[2*i+1];
            if(max1[2*i] > max1[2*i+1]){
                max1[i] = max1[2*i];
                count[i] = count[2*i];
                max2[i] = Math.max(max1[2*i+1], max2[2*i]);
            } else if(max1[2*i] < max1[2*i+1]){
                max1[i] = max1[2*i+1];
                count[i] = count[2*i+1];
                max2[i] = Math.max(max1[2*i], max2[2*i+1]);
            } else {
                max1[i] = max1[2*i];
                count[i] = count[2*i] + count[2*i+1];
                max2[i] = Math.max(max2[2*i], max2[2*i+1]);
            }
        }

        public void initialize(int i, int l, int r, int[] A){
            lo[i] = l; hi[i] = r;
            if(l == r){
                sum[i] = max1[i] = A[l-1];
                count[i] = 1;
                return;
            }
            int m = (l+r)/2;
            initialize(2*i, l, m, A);
            initialize(2*i+1, m+1, r, A);
            pullUp(i);
        }

        private void minimize(int i, int l, int r, int v){
            // Non-standard break and tag conditions
            if(l > hi[i] || r < lo[i] || max1[i] <= v)  return;
            if(l <= lo[i] && hi[i] <= r && max2[i] < v){
                apply(i, v);
                return;
            }

            pushDown(i);
            minimize(2*i, l, r, v);
            minimize(2*i+1, l, r, v);
            pullUp(i);
        }

        private int maximum(int i, int l, int r){
            if(l > hi[i] || r < lo[i])      return Integer.MIN_VALUE;
            if(l <= lo[i] && hi[i] <= r)    return max1[i];

            pushDown(i);
            int leftmax = maximum(2*i, l, r);
            int rightmax = maximum(2*i+1, l, r);
            pullUp(i);

            return Math.max(leftmax, rightmax);
        }

        private long sum(int i, int l, int r){
            if(l > hi[i] || r < lo[i])      return 0;
            if(l <= lo[i] && hi[i] <= r)    return sum[i];

            pushDown(i);
            long leftsum = sum(2*i, l, r);
            long rightsum = sum(2*i+1, l, r);
            pullUp(i);

            return leftsum + rightsum;
        }

        public void minimize(int l, int r, int v){
            minimize(1, l, r, v);
        }

        public int maximum(int l, int r){
            return maximum(1, l, r);
        }

        public long sum(int l, int r){
            return sum(1, l, r);
        }
    }

    public static void main(String[] args) throws IOException {
        Reader in = new Reader();
        int T = in.nextInt();
        for(int t = 0; t < T; t++){
            int N = in.nextInt();
            int M = in.nextInt();

            int[] A = new int[N];
            for(int i = 0; i < N; i++)
                A[i] = in.nextInt();

            SegTree seg = new SegTree(N, A);
            for(int i = 0; i < M; i++){
                int type = in.nextInt();
                int l = in.nextInt();
                int r = in.nextInt();
                if(type == 0){
                    int v = in.nextInt();
                    seg.minimize(l, r, v);
                } else if(type == 1){
                    System.out.println(seg.maximum(l, r));
                } else if(type == 2){
                    System.out.println(seg.sum(l, r));
                }
            }
        }
    }
}