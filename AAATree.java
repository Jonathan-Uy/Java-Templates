import java.io.*;

/**
 * Tested on https://dmoj.ca/problem/ds5
 */
public class AAATree {
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

    static class Edge {
        int u, v;
        public Edge(int u, int v){
            this.u = u; this.v = v;
        }
    }

    static class Lazy {
        int mul, add;

        public Lazy(int mul, int add){
            this.mul = mul;
            this.add = add;
        }

        public boolean isEmpty(){
            return this.mul == 1 && this.add == 0;
        }

        public Lazy combine(Lazy other){
            return new Lazy(this.mul*other.mul, this.add*other.mul + other.add);
        }
    }

    static class Data {
        int max, min, sum, size;

        public Data(int max, int min, int sum, int size){
            this.max = max; this.min = min; this.sum = sum; this.size = size;
        }

        public Data combine(Lazy other){
            if(this.size > 0){
                int mxv = this.max*other.mul + other.add;
                int mnv = this.min*other.mul + other.add;
                int smv = this.sum*other.mul + other.add*this.size;
                return new Data(mxv, mnv, smv, this.size);
            }
            return this;
        }

        public Data combine(Data other){
            int mxv = this.max > other.max ? this.max : other.max;
            int mnv = this.min < other.min ? this.min : other.min;
            int smv = this.sum + other.sum;
            int szv = this.size + other.size;
            return new Data(mxv, mnv, smv, szv);
        }
    }

    static class AAAT {
        int N, root, fptr, bptr;
        boolean[] rev, isx;
        Lazy[] stag, xtag;
        Data[] sld, vir, all;
        int[] p, val, bin;
        int[][] c;

        public AAAT(int N, Edge[] E, int[] V){
            this.N = N;
            root = 1;
            fptr = bptr = 0;
            rev = new boolean[2*N+1];
            isx = new boolean[2*N+1];
            stag = new Lazy[2*N+1];
            xtag = new Lazy[2*N+1];
            sld = new Data[2*N+1];
            vir = new Data[2*N+1];
            all = new Data[2*N+1];
            p = new int[2*N+1];
            val = new int[2*N+1];
            bin = new int[2*N+1];
            c = new int[2*N+1][4];

            for(int i = 1; i <= N; i++){
                stag[i] = new Lazy(1, 0);
                xtag[i] = new Lazy(1, 0);
                sld[i] = new Data(0, 0, 0, 0);
                vir[i] = new Data(0, 0, 0, 0);
                all[i] = new Data(0, 0, 0, 0);
            }

            for(int i = 1; i <= N; i++){
                val[++bptr] = V[i];
                pullUp(bptr);
            }

            for(int i = 0; i < N-1; i++){
                makeRoot(E[i].u);
                makeRoot(E[i].v);
                link(E[i].u, E[i].v);
            }
        }

        private void pushrev(int x){
            rev[x] ^= true;
            int temp = c[x][0];
            c[x][0] = c[x][1];
            c[x][1] = temp;
        }

        private void tagsld(int x, Lazy tag){
            stag[x] = stag[x].combine(tag);
            sld[x] = sld[x].combine(tag);
            val[x] = val[x]*tag.mul + tag.add;
            all[x] = sld[x].combine(vir[x]);
        }

        private void tagvir(int x, Lazy tag, boolean fg){
            xtag[x] = xtag[x].combine(tag);
            all[x] = all[x].combine(tag);
            vir[x] = vir[x].combine(tag);
            if(fg)  tagsld(x, tag);
        }

        private void pullUp(int x){
            sld[x] = new Data(-(1<<30), 1<<30, 0, 0);
            vir[x] = new Data(-(1<<30), 1<<30, 0, 0);
            all[x] = new Data(-(1<<30), 1<<30, 0, 0);
            if(!isx[x]){
                all[x] = new Data(val[x], val[x], val[x], 1);
                sld[x] = new Data(val[x], val[x], val[x], 1);
            }
            for(int i = 0; i < 2; i++){
                if(c[x][i] != 0){
                    sld[x] = sld[x].combine(sld[c[x][i]]);
                    vir[x] = vir[x].combine(vir[c[x][i]]);
                }
            }
            for(int i = 0; i < 4; i++)
                if(c[x][i] != 0)
                    all[x] = all[x].combine(all[c[x][i]]);
            for(int i = 2; i < 4; i++)
                if(c[x][i] != 0)
                    vir[x] = vir[x].combine(all[c[x][i]]);
        }

        private void pushDown(int x){
            if(rev[x]){
                if(c[x][0] != 0)    pushrev(c[x][0]);
                if(c[x][1] != 0)    pushrev(c[x][1]);
                rev[x] = false;
            }
            if(!xtag[x].isEmpty()){
                for(int i = 0; i < 4; i++)
                    if(c[x][i] != 0)
                        tagvir(c[x][i], xtag[x], i >= 2);
                xtag[x] = new Lazy(1, 0);
            }
            if(!stag[x].isEmpty()){
                for(int i = 0; i < 2; i++)
                    if(c[x][i] != 0)
                        tagsld(c[x][i], stag[x]);
                stag[x] = new Lazy(1, 0);
            }
        }

        private int son(int x, int y){
            if(c[x][y] != 0)
                pushDown(c[x][y]);
            return c[x][y];
        }

        private int find(int x){
            for(int i = 0; i < 4; i++)
                if(c[p[x]][i] == x)
                    return i;
            return -1;
        }

        private void sets(int x, int w, int tp){
            if(w != 0)
                p[w] = x;
            c[x][tp] = w;
        }

        private boolean check(int x, int tp){
            if(tp == 0) return p[x] == 0 || c[p[x]][0] != x && c[p[x]][1] != x;
            else return p[x] == 0 || !isx[x] || !isx[p[x]];
        }

        private void rotate(int x, int tp){
            if(check(x, tp))    return;
            int y = p[x];
            if(p[y] != 0)   sets(p[y], x, find(y));
            else            p[x] = 0;
            p[y] = x;
            if(c[y][tp] == x){
                c[y][tp] = c[x][tp+1];
                c[x][tp+1] = y;
                if(c[y][tp] != 0)
                    p[c[y][tp]] = y;
            } else {
                c[y][tp+1] = c[x][tp];
                c[x][tp] = y;
                if(c[y][tp+1] != 0)
                    p[c[y][tp+1]] = y;
            }
            pullUp(y);
            pullUp(x);
        }

        private void update(int x){
            if(p[x] != 0)
                update(p[x]);
            pushDown(x);
        }

        private void split(int x, int tp){
            pullUp(x);
            while(!check(x, tp)){
                int y = p[x];
                if(check(y, tp))    rotate(x, tp);
                else {
                    if((c[p[y]][tp] == y)^(c[y][tp] == x))
                        rotate(x, tp);
                    else
                        rotate(y, tp);
                    rotate(x, tp);
                }
            }
        }

        private int newNode(){
            int x = (fptr == 0 ? ++bptr : bin[fptr--]);
            for(int i = 0; i < 4; i++)
                c[x][i] = 0;
            p[x] = 0;
            stag[x] = new Lazy(1, 0);
            xtag[x] = new Lazy(1, 0);
            sld[x] = new Data(-(1<<30), 1<<30, 0, 0);
            vir[x] = new Data(-(1<<30), 1<<30, 0, 0);
            all[x] = new Data(-(1<<30), 1<<30, 0, 0);
            isx[x] = true;
            rev[x] = false;
            val[x] = 0;
            return x;
        }

        private void recycle(int x){
            bin[++fptr] = x;
        }

        private void add(int x, int w){
            for(int i = 2; i < 4; i++){
                if(c[w][i] == 0){
                    sets(w, x, i);
                    return;
                }
            }
            int y = newNode(), u;
            for(u = w; isx[c[u][2]]; u = son(u, 2));
            sets(y, c[u][2], 2);
            sets(y, x, 3);
            sets(u, y, 2);
            split(y, 2);
        }

        private void del(int x){
            if(isx[p[x]]){
                sets(p[p[x]], c[p[x]][5-find(x)], find(p[x]));
                recycle(p[x]);
                split(p[p[x]], 2);
            } else {
                sets(p[x], 0, find(x));
            }
            p[x] = 0;
        }

        private void access(int x){
            int y;
            update(x);
            split(x, 0);
            if(c[x][1] != 0){
                y = c[x][1];
                c[x][1] = 0;
                add(y, x);
                pullUp(x);
            }
            while(p[x] != 0){
                for(y = p[x]; isx[y]; y = p[y]);
                split(y, 0);
                if(c[y][1] != 0){
                    sets(p[x], c[y][1], find(x));
                    split(p[x], 2);
                } else {
                    del(x);
                }
                sets(y, x, 1);
                pullUp(y);
                x = y;
            }
        }

        public void makeRoot(int x){
            access(x);
            split(x, 0);
            pushrev(x);
        }

        private int findRoot(int x){
            access(x);
            split(x, 0);
            x = son(x, 0);
            while(x != 0 && c[x][1] != 0)
                x = son(x, 1);
            return x;
        }

        private int getRoot(int x){
            while(p[x] != 0)
                x = p[x];
            return x;
        }

        public int cut(int x){
            int y = findRoot(x);
            if(y != 0){
                access(y);
                split(y, 0);
                del(x);
                pullUp(y);
            }
            return y;
        }

        public void link(int x, int w){
            int y = cut(x);
            if(getRoot(x) != getRoot(w))    y = w;
            if(y != 0){
                access(y);
                split(y, 0);
                add(x, y);
                pullUp(y);
            }
        }

        public void subSet(int x, int y){
            access(x);
            split(x, 0);
            Lazy tag = new Lazy(0, y);
            val[x] = y;
            for(int i = 2; i < 4; i++)
                if(c[x][i] != 0)
                    tagvir(c[x][i], tag, true);
            pullUp(x);
        }

        public void subAdd(int x, int y){
            access(x);
            split(x, 0);
            Lazy tag = new Lazy(1, y);
            val[x] = val[x] + y;
            for(int i = 2; i < 4; i++)
                if(c[x][i] != 0)
                    tagvir(c[x][i], tag, true);
            pullUp(x);
        }

        public void pathSet(int x, int y, int z, int rt){
            makeRoot(x);
            access(y);
            split(x, 0);
            tagsld(x, new Lazy(0, z));
            makeRoot(rt);
        }

        public void pathAdd(int x, int y, int z, int rt){
            makeRoot(x);
            access(y);
            split(x, 0);
            tagsld(x, new Lazy(1, z));
            makeRoot(rt);
        }

        public int subMin(int x){
            access(x);
            split(x, 0);
            int ans = val[x];
            for(int i = 2; i < 4; i++)
                if(c[x][i] != 0)
                    ans = ans < all[c[x][i]].min ? ans : all[c[x][i]].min;
            return ans;
        }

        public int subMax(int x){
            access(x);
            split(x, 0);
            int ans = val[x];
            for(int i = 2; i < 4; i++)
                if(c[x][i] != 0)
                    ans = ans > all[c[x][i]].max ? ans : all[c[x][i]].max;
            return ans;
        }

        public int subSum(int x){
            access(x);
            split(x, 0);
            int ans = val[x];
            for(int i = 2; i < 4; i++)
                if(c[x][i] != 0)
                    ans += all[c[x][i]].sum;
            return ans;
        }

        public int pathMin(int x, int y, int rt){
            makeRoot(x);
            access(y);
            split(x, 0);
            int ans = sld[x].min;
            makeRoot(rt);
            return ans;
        }

        public int pathMax(int x, int y, int rt){
            makeRoot(x);
            access(y);
            split(x, 0);
            int ans = sld[x].max;
            makeRoot(rt);
            return ans;
        }

        public int pathSum(int x, int y, int rt){
            makeRoot(x);
            access(y);
            split(x, 0);
            int ans = sld[x].sum;
            makeRoot(rt);
            return ans;
        }
    }

    public static void main(String[] args) throws IOException {
        PrintWriter out = new PrintWriter(System.out);
        Reader in = new Reader();
        int N = in.nextInt();
        int M = in.nextInt();

        Edge[] edges = new Edge[N-1];
        int[] vals = new int[N+1];
        for(int i = 0; i < N-1; i++){
            int u = in.nextInt();
            int v = in.nextInt();
            edges[i] = new Edge(u, v);
        }
        for(int i = 1; i <= N; i++)
            vals[i] = in.nextInt();

        AAAT tree = new AAAT(N, edges, vals);
        int root = in.nextInt();
        tree.makeRoot(root);

        for(int i = 0; i < M; i++){
            int type = in.nextInt();
            if(type == 0){
                int x = in.nextInt();
                int y = in.nextInt();
                tree.subSet(x, y);
            } else if(type == 1){
                root = in.nextInt();
                tree.makeRoot(root);
            } else if(type == 2){
                int x = in.nextInt();
                int y = in.nextInt();
                int z = in.nextInt();
                tree.pathSet(x, y, z, root);
            } else if(type == 3){
                int x = in.nextInt();
                out.println(tree.subMin(x));
            } else if(type == 4){
                int x = in.nextInt();
                out.println(tree.subMax(x));
            } else if(type == 5){
                int x = in.nextInt();
                int y = in.nextInt();
                tree.subAdd(x, y);
            } else if(type == 6){
                int x = in.nextInt();
                int y = in.nextInt();
                int z = in.nextInt();
                tree.pathAdd(x, y, z, root);
            } else if(type == 7){
                int x = in.nextInt();
                int y = in.nextInt();
                out.println(tree.pathMin(x, y, root));
            } else if(type == 8){
                int x = in.nextInt();
                int y = in.nextInt();
                out.println(tree.pathMax(x, y, root));
            } else if(type == 9){
                int x = in.nextInt();
                int y = in.nextInt();
                tree.link(x, y);
            } else if(type == 10){
                int x = in.nextInt();
                int y = in.nextInt();
                out.println(tree.pathSum(x, y, root));
            } else if(type == 11){
                int x = in.nextInt();
                out.println(tree.subSum(x));
            }
        }

        in.close();
        out.close();
    }
}