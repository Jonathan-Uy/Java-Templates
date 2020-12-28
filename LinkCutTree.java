import java.io.*;

/**
 * Tested on https://dmoj.ca/problem/ds5easy/
 */
public class LinkCutTree {
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

        public void close() throws IOException {
            if (din == null)
                return;
            din.close();
        }
    }

    static class Node {
        int p, count;
        int[] c;
        long val, sum, min, max, assign, delta;
        boolean flip;
        public Node(long val){
            c = new int[2];
            this.count = 1; this.assign = -1;
            this.val = this.sum = this.min = this.max = val;
        }
    }

    static class LCT {
        Node[] T;

        public LCT(int N, long[] A){
            T = new Node[N+1];
            for(int i = 0; i <= N; i++)
                T[i] = new Node(A[i]);
        }

        private int count(int x){
            return x == 0 ? 0 : T[x].count;
        }

        private long sum(int x){
            return x == 0 ? 0 : T[x].sum;
        }

        private long min(int x){
            return x == 0 ? Long.MAX_VALUE : T[x].min;
        }

        private long max(int x){
            return x == 0 ? Long.MIN_VALUE : T[x].max;
        }

        private void increment(int x, long val){
            if(x == 0)  return;
            T[x].delta += val;
            T[x].val += val;
            T[x].min += val;
            T[x].max += val;
            T[x].sum += val * T[x].count;
        }

        private void assign(int x, long val){
            if(x == 0)  return;
            T[x].delta = 0;
            T[x].val = val;
            T[x].min = val;
            T[x].max = val;
            T[x].assign = val;
            T[x].sum = val * T[x].count;
        }

        private void flip(int x){
            if(x == 0)  return;
            else        T[x].flip = !T[x].flip;
        }

        private boolean notRoot(int x){
            return T[x].p != 0 && (T[T[x].p].c[0] == x || T[T[x].p].c[1] == x);
        }

        private void pullUp(int x){
            if(x == 0) return;
            T[x].count = 1 + count(T[x].c[0]) + count(T[x].c[1]);
            T[x].sum = T[x].val + sum(T[x].c[0]) + sum(T[x].c[1]);
            T[x].min = Math.min(T[x].val, Math.min(min(T[x].c[0]), min(T[x].c[1])));
            T[x].max = Math.max(T[x].val, Math.max(max(T[x].c[0]), max(T[x].c[1])));
        }

        private void pushDown(int x){
            if(x != 0 && T[x].flip){
                int temp = T[x].c[0];
                T[x].c[0] = T[x].c[1];
                T[x].c[1] = temp;
                flip(T[x].c[0]);
                flip(T[x].c[1]);
                T[x].flip = false;
            }
            if(x != 0 && T[x].assign != -1){
                assign(T[x].c[0], T[x].assign);
                assign(T[x].c[1], T[x].assign);
                T[x].assign = -1;
            }
            if(x != 0){
                increment(T[x].c[0], T[x].delta);
                increment(T[x].c[1], T[x].delta);
                T[x].delta = 0;
            }
        }

        private void rotate(int x) {
            int p = T[x].p;
            int d = (p != 0 && T[p].c[0] == x ? 1 : 0);
            T[x].p = T[p].p;
            if(notRoot(p)){
                if(T[T[x].p].c[0] == p) T[T[x].p].c[0] = x;
                else                    T[T[x].p].c[1] = x;
            }
            T[p].c[(d^1)] = T[x].c[d];
            if(T[p].c[(d^1)] != 0)
                T[T[p].c[(d^1)]].p = p;
            T[x].c[d] = p;
            T[p].p = x;
            pullUp(p);
        }

        private void splay(int x){
            while(notRoot(x)){
                int p = T[x].p;
                if(notRoot(p))
                    pushDown(T[p].p);
                pushDown(p);
                pushDown(x);
                if(notRoot(p)){
                    boolean d1 = (p != 0 && T[p].p != 0 && T[T[p].p].c[0] == p);
                    boolean d2 = (p != 0 && T[p].c[0] == x);
                    if(d1^d2)   rotate(x);
                    else        rotate(p);
                }
                rotate(x);
            }
            pushDown(x);
            pullUp(x);
        }

        public int access(int _u){
            int v = 0;
            for(int u = _u; u != 0; u = T[u].p){
                splay(u);
                T[u].c[1] = v;
                v = u;
            }
            splay(_u);
            return v;
        }

        public void makeRoot(int u){
            access(u);
            T[u].flip = true;
        }

        public void link(int u, int v){
            makeRoot(u);
            T[u].p = v;
        }

        public void cut(int u, int v){
            makeRoot(u);
            access(v);
            if(T[v].c[0] != 0)
                T[T[v].c[0]].p = 0;
            T[v].c[0] = 0;
            pullUp(v);
        }

        private int getPath(int u, int v){
            makeRoot(u); access(v); return v;
        }

        public void pathModify(int x, int y, long z){
            int u = getPath(x, y);
            assign(u, z);
        }

        public void pathIncrement(int x, int y, long z){
            int u = getPath(x, y);
            increment(u, z);
        }

        public long pathMin(int x, int y){
            int u = getPath(x, y);
            return T[u].min;
        }

        public long pathMax(int x, int y){
            int u = getPath(x, y);
            return T[u].max;
        }

        public long pathSum(int x, int y){
            int u = getPath(x, y);
            return T[u].sum;
        }

        public void changeParent(int root, int x, int y){
            if(x == LCA(root, x, y))  return;
            cut(root, x);
            link(x, y);
        }

        public int LCA(int root, int x, int y){
            makeRoot(root);
            access(x);
            return access(y);
        }
    }

    public static void main(String[] args) throws IOException {
        Reader in = new Reader();
        int N = in.nextInt();
        int M = in.nextInt();

        long[] A = new long[N+1];
        for(int i = 1; i <= N; i++)
            A[i] = in.nextLong();

        LCT lct = new LCT(N, A);

        for(int i = 0; i < N-1; i++){
            int x = in.nextInt();
            int y = in.nextInt();
            lct.link(x, y);
        }

        int root = in.nextInt();

        for(int i = 0; i < M; i++){
            int K = in.nextInt();
            if(K == 0){
                root = in.nextInt();
            } else if(K == 1){
                int x = in.nextInt();
                int y = in.nextInt();
                long z = in.nextLong();
                lct.pathModify(x, y, z);
            } else if(K == 2){
                int x = in.nextInt();
                int y = in.nextInt();
                long z = in.nextLong();
                lct.pathIncrement(x, y, z);
            } else if(K == 3){
                int x = in.nextInt();
                int y = in.nextInt();
                System.out.println(lct.pathMin(x, y));
            } else if(K == 4){
                int x = in.nextInt();
                int y = in.nextInt();
                System.out.println(lct.pathMax(x, y));
            } else if(K == 5){
                int x = in.nextInt();
                int y = in.nextInt();
                System.out.println(lct.pathSum(x, y));
            } else if(K == 6){
                int x = in.nextInt();
                int y = in.nextInt();
                lct.changeParent(root, x, y);
            } else if(K == 7){
                int x = in.nextInt();
                int y = in.nextInt();
                System.out.println(lct.LCA(root, x, y));
            }
        }
    }
}