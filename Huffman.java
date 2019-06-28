package huffman;

import com.sun.xml.internal.fastinfoset.EncodingConstants;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;

public class Huffman {

    public static void main(String[] args) throws IOException {
        int[] freq = new int[256];
        FileInputStream in = null, in2 = null;
        System.out.println("Enter file name:");
        Scanner sc = new Scanner(System.in);
        String inputFile = sc.nextLine();
        System.out.println("1. Compress file");
        System.out.println("2. Decompress file");
        int type = sc.nextInt();
        String cinput = "D:\\College\\Programming\\Algorithms\\Assignment\\" + inputFile;
        String coutput = "D:\\College\\Programming\\Algorithms\\Assignment\\Co"+ inputFile;
        String dinput = "D:\\College\\Programming\\Algorithms\\Assignment\\"+ inputFile;
        String doutput = "D:\\College\\Programming\\Algorithms\\Assignment\\De"+inputFile;
        long startTime;
        long endTime;
        Double time;
        double inlength;
        double outlength;
        double ratio;
        int totalNomOfCharacters=0;
        int effChars=0;
        if (type == 1) {
            startTime= System.nanoTime();
//            FileWriter outHeader = new FileWriter(coutput);
            BitOutputStream out = new BitOutputStream(coutput);
            File infile =new File(cinput);
            File outfile =new File(coutput);
            try {
                in = new FileInputStream(cinput);
                int c;
                while ((c = in.read()) != -1) {
                    //System.out.print(c);
                    freq[c]++;
                    totalNomOfCharacters++;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            Node root = buildTrie(freq);
            String[] st = new String[256];
            buildCode(st, root, "");
            writeTree(root);
//            outHeader.write("Header");
//            outHeader.write(System.getProperty("line.separator"));
            out.writeBy(Integer.toString(totalNomOfCharacters, 10));
            out.writeBy(System.getProperty("line.separator"));
            out.writeBy("\n");
            for (int i = 0; i < 256; i++) {
                if (st[i] != null) {
                    String x=Integer.toBinaryString(i);
                    out.writeBy(x+" ");
                    out.writeBy(st[i]);
                    out.writeBy(System.getProperty("line.separator"));
//                    System.out.print(Character.toString((char) i) + " : ");
//                    System.out.print(i + " : ");
//                    System.out.print(Integer.toBinaryString(i) + " : ");
//                    System.out.println(st[i]);
                }
            }
            out.writeBy("e");
//            outHeader.write(System.getProperty("line.separator"));
            try {
                in2 = new FileInputStream(cinput);
                int c;
                while ((c = in2.read()) != -1) {
                    for (int i = 0; i < st[c].length(); i++) {
                        int x = Integer.parseInt(Character.toString(st[c].charAt(i)));
                        out.writeBit(x);
                    }
                }
            } finally {
                if (in2 != null) {
                    in2.close();
                }

                out.close();
//                    outHeader.close();
            }
            endTime= System.nanoTime();
            inlength=infile.length();
            System.out.println("Original Length "+inlength/1024+" KB");
            outlength=outfile.length();
            System.out.println("Compressed Size "+outlength/1024+" KB");
            ratio=inlength/outlength;
            System.out.println("Compression Factor "+ratio);
            System.out.println("Compression Ratio "+1.0/ratio);
            time = (endTime - startTime)/1000000.0;
            System.out.println("");
            System.out.println("Time to Compress = "+time+" ms");
            if(ratio<1){
                outfile.delete();
                JOptionPane.showMessageDialog(null,"File can't be compressed :(","Warning",JOptionPane.WARNING_MESSAGE);
            }
        } else if (type == 2) {
            startTime= System.nanoTime();
//            FileInputStream inHeader = new FileInputStream(dinput);
            FileOutputStream out = new FileOutputStream(doutput);
            BitInputStream decompress = new BitInputStream(dinput);
           try {
//                int datatime=0;
//                String check="";
//                int counter=0;
                int c;
                int ascii=0;
                int count=0;
                int[] index=new int[256];
                String old = "";
                String[] st = new String[256];
                String length="";
                while((c=decompress.readBit(0))!=13)
                {   
                    length=length+Character.toString((char)c);
                }
                totalNomOfCharacters=Integer.parseInt(length, 10);
                while((c=(char)decompress.readBit(0))!='e')
                {
                    String x = Character.toString((char) c);
                    if (x.equals("1") || x.equals("0")) {
                        old += x;
                    }
                    if (x.equals(" ")) {
//                        System.out.println(old);
                        ascii=Integer.parseInt(old,2);
                        index[count]=ascii;
                        count++;
                        old = "";
                    }
                    else if (c == 13) {
//                        System.out.println(old);
                        st[ascii]=old;
                        old = "";
                    }
                }
                System.out.println("HASH"); 
                for(int i = 0; i<count;i++){
                    System.out.println((char)index[i] +" " + st[index[i]]);
                }
                while ((c = decompress.readBit(1)) != -1 && effChars<totalNomOfCharacters) {
                    //if(datatime==0){
                        //check+=Integer.toBinaryString(c);
                        //while(counter<8)
                        //{
                            //check+=Integer.toBinaryString(decompress.readBit());
                            //counter++;
                        //}
                        //System.out.println(check);
                        //if(check.equals("01100101")){
                            //datatime=1;
                            //continue;
                        //}
                        
                        //check=check.substring(1)+Integer.toBinaryString(c);
                    //}
                    String x = Integer.toBinaryString(c);
                    old += x;
                    for(int i = 0; i<count;i++){
                        if(old.equals(st[index[i]])){
                            out.write(index[i]); 
                            old = "";
                            effChars++;
                            break;
                        }
                    }
                }
            } finally {
                out.close();
//                inHeader.close();
            }
            endTime= System.nanoTime();
            time = (endTime - startTime)/1000000.0;
            System.out.println("");
            System.out.println("Time to Decompress = "+time+" ms");
//            Node root = readTrie();
//            // number of bytes to write
//            int length = BinaryStdIn.readInt();
//            // decode using the Huffman trie
//            for (int i = 0; i < length; i++) {
//                Node x = root;
//                while (!x.isLeaf()) {
//                    boolean bit = BinaryStdIn.readBoolean();
//                    if (bit) {
//                        x = x.right;
//                    } else {
//                        x = x.left;
//                    }
//                }
//            }
        }
        

    }
    
    // build the Huffman trie given frequencies
    private static Node buildTrie(int[] freq) {

        // initialze priority queue with singleton trees
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        for (char i = 0; i < 256; i++) {
            if (freq[i] > 0) {
                pq.add(new Node(i, freq[i], null, null));
            }
        }

        // special case in case there is only one character with a nonzero frequency
        if (pq.size() == 1) {
            if (freq['\0'] == 0) {
                pq.add(new Node('\0', 0, null, null));
            } else {
                pq.add(new Node('\1', 0, null, null));
            }
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.add(parent);
        }
        return pq.poll();
    }

    // make a lookup table from symbols and their encodings
    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left, s + '0');
            buildCode(st, x.right, s + '1');
        } else {
            st[x.ch] = s;
        }
    }

    // write bitstring-encoded trie to standard output
    private static void writeTree(Node x) {
        if (x.isLeaf()) {
            return;
        }
        writeTree(x.left);
        writeTree(x.right);
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }
}

//    private static Node readTrie() {
//        boolean isLeaf = BinaryStdIn.readBoolean();
//        if (isLeaf) {
//            return new Node(BinaryStdIn.readChar(), -1, null, null);
//        } else {
//            return new Node('\0', -1, readTrie(), readTrie());
//        }
//    }