import java.io.*;
import java.util.*;

/**
 * Tested on https://dmoj.ca/problem/ds2/
 */
public class DisjointSetUnion {
    static int[] ds;

    public static int find(int u){
        if(ds[u] < 0)   return u;
        ds[u] = find(ds[u]);
        return ds[u];
    }

    public static boolean merge(int u, int v){
        u = find(u); v = find(v);
        if(u == v)  return false;
        if(ds[u] < ds[v]){
            ds[u] += ds[v];
            ds[v] = u;
        } else {
            ds[v] += ds[u];
            ds[u] = v;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        ds = new int[N+1];
        for(int i = 1; i <= N; i++)
            ds[i] = -1;

        ArrayList<Integer> MST = new ArrayList<>();
        for(int i = 1; i <= M; i++){
            st = new StringTokenizer(br.readLine());
            int u = Integer.parseInt(st.nextToken());
            int v = Integer.parseInt(st.nextToken());
            if(merge(u, v)) MST.add(i);
        }

        if(MST.size() < N-1)    System.out.println("Disconnected Graph");
        else
            for(int e : MST)
                System.out.println(e);
    }
}