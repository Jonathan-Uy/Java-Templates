import java.io.*;

/**
 * Tested on https://dmoj.ca/problem/ds4/
 */
public class SizeBalancedTree {
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

    static class Node {
        int l, r, size, val;
        public Node(int val){
            this.l = -1;
            this.r = -1;
            this.size = 1;
            this.val = val;
        }
    }

    static class SBT {
        private int root, N;
        private Node[] tree;

        public SBT(int maxSize){
            root = -1; N = 0;
            tree = new Node[maxSize];
        }

        private int size(int x){
            return (x == -1 ? 0 : tree[x].size);
        }

        private void update(int x){
            tree[x].size = size(tree[x].l) + 1 + size(tree[x].r);
        }

        private int rightRotate(int x){
            int y = tree[x].l;
            tree[x].l = tree[y].r;
            tree[y].r = x;
            update(x);
            update(y);
            return y;
        }

        private int leftRotate(int x){
            int y = tree[x].r;
            tree[x].r = tree[y].l;
            tree[y].l = x;
            update(x);
            update(y);
            return y;
        }

        private int maintain(int x, boolean flag){
            if(flag){
                if(tree[x].r == -1) return x;
                else if(size(tree[x].l) < size(tree[tree[x].r].l)) { tree[x].r = rightRotate(tree[x].r); x = leftRotate(x); }
                else if(size(tree[x].l) < size(tree[tree[x].r].r)) { x = leftRotate(x); }
                else return x;
            } else {
                if(tree[x].l == -1) return x;
                else if(size(tree[x].r) < size(tree[tree[x].l].r)) { tree[x].l = leftRotate(tree[x].l); x = rightRotate(x); }
                else if(size(tree[x].r) < size(tree[tree[x].l].l)) { x = rightRotate(x); }
                else return x;
            }
            tree[x].l = maintain(tree[x].l, false);
            tree[x].r = maintain(tree[x].r, true);
            x = maintain(x, true);
            x = maintain(x, false);
            return x;
        }

        private boolean contains(int x, int val){
            if(x == -1) return false;
            else if(val < tree[x].val)  return contains(tree[x].l, val);
            else if(tree[x].val < val)  return contains(tree[x].r, val);
            else return true;
        }

        private int removeMin(int x){
            if(tree[x].l == -1) return tree[x].r;
            tree[x].l = removeMin(tree[x].l);
            update(x); return x;
        }

        private int getMin(int x) { return tree[x].l == -1 ? x : getMin(tree[x].l); }

        private int insert(int x, int val){
            if(x == -1) { tree[N++] = new Node(val); return N-1; }
            if(val < tree[x].val) { int l = insert(tree[x].l, val); tree[x].l = l; }
            else                  { int r = insert(tree[x].r, val); tree[x].r = r; }
            update(x); return maintain(x, tree[x].val <= val);
        }

        private int remove(int x, int val){
            if(x == -1) return -1;
            else if(val < tree[x].val)  tree[x].l = remove(tree[x].l, val);
            else if(tree[x].val < val)  tree[x].r = remove(tree[x].r, val);
            else {
                if(tree[x].l == -1)         return tree[x].r;
                else if(tree[x].r == -1)    return tree[x].l;
                else {
                    int y = x;
                    x = getMin(tree[y].r);
                    tree[x].r = removeMin(tree[y].r);
                    tree[x].l = tree[y].l;
                }
            }
            update(x); return x;
        }

        private int select(int x, int k){
            if(x == -1) return -1;
            int t = size(tree[x].l);
            if(t > k)       return select(tree[x].l, k);
            else if(t < k)  return select(tree[x].r, k - t - 1);
            else            return x;
        }

        private int rank(int x, int val){
            if(x == -1) return 0;
            if(val <= tree[x].val)  return rank(tree[x].l, val);
            else return 1 + size(tree[x].l) + rank(tree[x].r, val);
        }

        private void print(int x){
            if(x == -1) return;
            print(tree[x].l);
            System.out.print(tree[x].val + " ");
            print(tree[x].r);
        }

        public void insert(int val){
            root = insert(root, val);
        }

        public void remove(int val){
            if(contains(root, val))
                root = remove(root, val);
        }

        public int select(int index){
            return tree[select(root, index-1)].val;
        }

        public int rank(int val){
            if(!contains(root, val))    return -1;
            else return rank(root, val) + 1;
        }

        public void print(){
            print(root);
        }
    }

    public static void main(String[] args) throws IOException {
        Reader in = new Reader();
        int N = in.nextInt();
        int M = in.nextInt();

        SBT tree = new SBT(N+M);
        for(int i = 0; i < N; i++)
            tree.insert(in.nextInt());

        int lastAns = 0;
        for(int i = 0; i < M; i++){
            char c = in.read().charAt(0);
            int v = in.nextInt() ^ lastAns;

            if(c == 'I'){
                tree.insert(v);
            } else if(c == 'R'){
                tree.remove(v);
            } else if(c == 'S'){
                lastAns = tree.select(v);
                System.out.println(lastAns);
            } else if(c == 'L'){
                lastAns = tree.rank(v);
                System.out.println(lastAns);
            }
        }

        tree.print();
    }
}