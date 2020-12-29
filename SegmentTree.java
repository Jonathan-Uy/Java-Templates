import java.io.*;
import java.util.*;

/**
 * Tested on https://dmoj.ca/problem/lazy/
 */
public class SegmentTree {
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
        int[] lo, hi;
        long[] min, delta, assign;

        public SegTree(int N, int[] A){
            this.N = N;
            lo = new int[4*N+1];
            hi = new int[4*N+1];
            min = new long[4*N+1];
            delta = new long[4*N+1];
            assign = new long[4*N+1];
            initialize(1, 0, N-1, A);
        }

        private void initialize(int i, int l, int r, int[] A){
            lo[i] = l;
            hi[i] = r;
            if(l == r) { delta[i] = A[l]; return; }
            int m = l + (r-l)/2;
            initialize(2*i, l, m, A);
            initialize(2*i+1, m+1, r, A);
            pullUp(i);
        }

        private void pullUp(int i){
            long l = min[2*i] + delta[2*i];
            long r = min[2*i+1] + delta[2*i+1];
            min[i] = Math.min(l, r);
        }

        private void pushDown(int i){
            if(assign[i] != 0){
                assign[2*i] = assign[i];
                assign[2*i+1] = assign[i];
                min[2*i] = assign[i];
                min[2*i+1] = assign[i];
                delta[2*i] = 0;
                delta[2*i+1] = 0;
                assign[i] = 0;
            }

            delta[2*i] += delta[i];
            delta[2*i+1] += delta[i];
            delta[i] = 0;
        }

        private void increment(int i, int l, int r, int v){
            if(l > hi[i] || r < lo[i])  return;
            if(l <= lo[i] && hi[i] <= r){
                delta[i] += v; return;
            }

            pushDown(i);
            increment(2*i, l, r, v);
            increment(2*i+1, l, r, v);
            pullUp(i);
        }

        private void assign(int i, int l, int r, int v){
            if(l > hi[i] || r < lo[i])  return;
            if(l <= lo[i] && hi[i] <= r){
                assign[i] = v; delta[i] = 0; min[i] = v; return;
            }

            pushDown(i);
            assign(2*i, l, r, v);
            assign(2*i+1, l, r, v);
            pullUp(i);
        }

        private long minimum(int i, int l, int r){
            if(l > hi[i] || r < lo[i])      return Long.MAX_VALUE;
            if(l <= lo[i] && hi[i] <= r)    return min[i] + delta[i];

            pushDown(i);
            long lmin = minimum(2*i, l, r);
            long rmin = minimum(2*i+1, l, r);
            pullUp(i);

            return Math.min(lmin, rmin);
        }

        public void increment(int l, int r, int v){
            increment(1, l-1, r-1, v);
        }

        public void assign(int l, int r, int v){
            assign(1, l-1, r-1, v);
        }

        public long minimum(int l, int r){
            return minimum(1, l-1, r-1);
        }
    }

    public static void main(String[] args) throws IOException {
        Reader in = new Reader();
        int N = in.nextInt();
        int Q = in.nextInt();

        int[] A = new int[N];
        for(int i = 0; i < N; i++)
            A[i] = in.nextInt();

        SegTree seg = new SegTree(N, A);

        for(int q = 0; q < Q; q++){
            int type = in.nextInt();
            int l = in.nextInt();
            int r = in.nextInt();
            int v = (type == 3 ? -1 : in.nextInt());

            if(type == 1)       seg.increment(l, r, v);
            else if(type == 2)  seg.assign(l, r, v);
            else if(type == 3)  System.out.println(seg.minimum(l, r));
        }
    }
}   