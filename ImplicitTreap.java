import java.io.*;
import java.util.*;

/**
 * Implicit Treap with O(N) Build
 * Tested on https://dmoj.ca/problem/noi05p2/
 */
public class ImplicitTreap {
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

        public String read() throws IOException {
            byte[] ret = new byte[64];
            int idx = 0;
            byte c = Read();
            while (c <= ' ') {
                c = Read();
            }
            do {
                ret[idx++] = c;
                c = Read();
            } while (c != -1 && c != ' ' && c != '\n' && c != '\r');
            return new String(ret, 0, idx);
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
    }

    static int NEG_INF = -1001;
    static Random rand = new Random();

    public static int rand(){
        return rand.nextInt(9999999)+1;
    }

    static class Node {
        boolean flip;
        Node l, r;
        int v, prior, sz, sum, dpl, dpr, dp, assign;
        public Node(int val, int pri){
            sz = 1;
            flip = false;
            l = r = null;
            v = sum = dpl = dpr = dp = val;
            prior = pri;
            assign = NEG_INF;
        }
    }

    static class Pair {
        Node first, second;
        public Pair(Node first, Node second){
            this.first = first; this.second = second;
        }
    }

    static class Treap {
        Node root;
        public Treap(){
            root = null;
        }

        private int size(Node x){   return x == null ? 0 : x.sz;           }
        private int sum(Node x){    return x == null ? 0 : x.sum;          }
        private int dp(Node x){     return x == null ? NEG_INF : x.dp;     }
        private int dpl(Node x){    return x == null ? NEG_INF : x.dpl;    }
        private int dpr(Node x){    return x == null ? NEG_INF : x.dpr;    }

        private void assign(Node x, int val){
            x.assign = x.v = val;
            x.sum = val * x.sz;
            x.dpl = x.dpr = x.dp = Math.max(val, x.sum);
        }

        private void flip(Node x){
            Node tempNode = x.l;
            x.l = x.r;
            x.r = tempNode;

            int tempInt = x.dpl;
            x.dpl = x.dpr;
            x.dpr = tempInt;

            x.flip ^= true;
        }

        private void pullUp(Node x){
            if(x == null)   return;
            Node l = x.l, r = x.r;
            x.sz = size(l) + 1 + size(r);
            x.sum = sum(l) + x.v + sum(r);

            x.dpl = l != null ? Math.max(l.dpl, l.sum + x.v + Math.max(0, dpl(r))) : Math.max(x.v, x.v + Math.max(0, dpl(r)));
            x.dpr = r != null ? Math.max(r.dpr, r.sum + x.v + Math.max(0, dpr(l))) : Math.max(x.v, x.v + Math.max(0, dpr(l)));
            if(l != null && r != null)  x.dp = Math.max(0, l.dpr) + x.v + Math.max(0, r.dpl);
            else {
                if(l != null)       x.dp = Math.max(0, l.dpr) + x.v;
                else if(r != null)  x.dp = Math.max(0, r.dpl) + x.v;
                else                x.dp = x.v;
            }
            if(l != null) x.dp = Math.max(x.dp, l.dp);
            if(r != null) x.dp = Math.max(x.dp, r.dp);
            x.dp = Math.max(x.dp, x.sum);
        }

        private void pushDown(Node x){
            if(x == null) return;
            Node l = x.l, r = x.r;

            if(x.assign != NEG_INF){
                if(x.l != null)    assign(l, x.assign);
                if(x.r != null)    assign(r, x.assign);
                x.assign = NEG_INF;
            }

            if(x.flip){
                if(x.l != null)    flip(l);
                if(x.r != null)    flip(r);
                x.flip = false;
            }
        }

        private Node merge(Node x, Node y){
            if(x == null)   return y;
            if(y == null)   return x;

            pushDown(x); pushDown(y);
            if(x.prior < y.prior){
                x.r = merge(x.r, y);
                pullUp(x);
                return x;
            } else {
                y.l = merge(x, y.l);
                pullUp(y);
                return y;
            }
        }

        private Pair split(Node x, int k){
            if(x == null)   return new Pair(null, null);

            Pair y = new Pair(null, null);
            pushDown(x);
            if(k <= size(x.l)){
                y = split(x.l, k);
                x.l = y.second;
                pullUp(x);
                y.second = x;
            } else {
                y = split(x.r, k-size(x.l)-1);
                x.r = y.first;
                pullUp(x);
                y.first = x;
            }

            return y;
        }

        private void heapify(Node x){
            if(x == null) return;

            Node max = x;
            if(x.l != null && x.l.prior > max.prior)    max = x.l;
            if(x.r != null && x.r.prior > max.prior)    max = x.r;
            if(max != x){
                int temp = x.prior;
                x.prior = max.prior;
                max.prior = temp;
                heapify(max);
            }
        }

        private Node build(int x, int k, int[] val){
            if(k == 0)  return null;

            int mid = k/2;
            Node t = new Node(val[x+mid], rand());
            t.l = build(x, mid, val);
            t.r = build(x+mid+1, k-mid-1, val);
            heapify(t);
            pullUp(t);
            return t;
        }

        public void insert(int x, int k, int[] val){
            Pair y = split(root, x);
            Node z = build(0, k, val);
            root = merge(y.first, merge(z, y.second));
        }

        public void delete(int x, int k){
            Pair y, z;
            y = split(root, x-1);
            z = split(y.second, k);
            root = merge(y.first, z.second);
        }

        public void assign(int x, int k, int c){
            Pair y, z;
            y = split(root, x-1);
            z = split(y.second, k);
            assign(z.first, c);
            y.second = merge(z.first, z.second);
            root = merge(y.first, y.second);
        }

        public void reverse(int x, int k){
            Pair y, z;
            y = split(root, x-1);
            z = split(y.second, k);
            flip(z.first);
            y.second = merge(z.first, z.second);
            root = merge(y.first, y.second);
        }

        public int getSum(int x, int k){
            if(k == 0)  return 0;

            Pair y, z;
            y = split(root, x-1);
            z = split(y.second, k);
            int ans = z.first.sum;
            y.second = merge(z.first, z.second);
            root = merge(y.first, y.second);
            return ans;
        }

        public int maxSum(){
            return root.dp;
        }

        private void print(Node x){
            if(x == null) return;
            print(x.l);
            System.out.print(x.v + " ");
            print(x.r);
        }

        public void print(){
            print(root);
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        Reader in = new Reader();
        int N = in.nextInt();
        int Q = in.nextInt();

        Treap treap = new Treap();

        int[] inserts = new int[500000];
        for(int i = 0; i < N; i++)
            inserts[i] = in.nextInt();
        treap.insert(0, N, inserts);

        PrintWriter pr = new PrintWriter(new OutputStreamWriter(System.out));

        for(int q = 0; q < Q; q++){
            String command = in.read();
            if(command.equals("INSERT")){
                int x = in.nextInt();
                int k = in.nextInt();
                for(int i = 0; i < k; i++)
                    inserts[i] = in.nextInt();
                treap.insert(x, k, inserts);
            } else if(command.equals("DELETE")){
                int x = in.nextInt();
                int k = in.nextInt();
                treap.delete(x, k);
            } else if(command.equals("MAKE-SAME")){
                int x = in.nextInt();
                int k = in.nextInt();
                int c = in.nextInt();
                treap.assign(x, k, c);
            } else if(command.equals("REVERSE")){
                int x = in.nextInt();
                int k = in.nextInt();
                treap.reverse(x, k);
            } else if(command.equals("GET-SUM")){
                int x = in.nextInt();
                int k = in.nextInt();
                pr.println(treap.getSum(x, k));
            } else if(command.equals("MAX-SUM")){
                pr.println(treap.maxSum());
            }
        }

        pr.close();
    }
}