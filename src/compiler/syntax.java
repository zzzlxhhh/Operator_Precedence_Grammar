package compiler;

import java.io.*;
import java.util.*;

//简单优先文法
public class syntax {
    int vn_size=0;
    int vt_size=0;
    int Gnumber;
    Vector<Character>[] G=new Vector[30];
    Set<Character> vn=new HashSet<>();
    Set<Character> vt=new HashSet<>();
    Set<Character> v=new HashSet<>();
    String[] Gstring=new String[30];
    int[][] mtable=new int[30][30];
    Vector<Character> v_n=new Vector<Character>();//非终结
    Vector<Character> v_t=new Vector<Character>();
    Vector<Character> vv=new Vector<Character>();
    Set<Character>[] head_set=new Set[30];
    Set<Character>[] last_set=new Set[30];
    //将vn set转化为vector存储方便后续查找
    void set_to_vector(){
        Iterator iter = vn.iterator();
        while(iter.hasNext())
            v_n.add((Character) iter.next());
        Iterator iter1 = vt.iterator();
        while(iter1.hasNext())
            v_t.add((Character) iter1.next());
        Iterator iter2 = v.iterator();//之前的错误将vn改为v
        while(iter2.hasNext())
            vv.add((Character) iter2.next());

    }
    //构造方法
    syntax(){
        for(int i=0;i<30;i++)
        {
            G[i]=new Vector<Character>();
            head_set[i]=new HashSet<>();
            last_set[i]=new HashSet<>();
        }
        for(int i=0;i<30;i++)
            for(int j=0;j<30;j++)
                mtable[i][j]=-1; //后面通过i j查找vv对应的符号
        for(int i=0;i<30;i++)//#
        {mtable[i][29]=2;
            mtable[29][i]=1;
        }
        mtable[29][29]=0;
        for(int i=0;i<30;i++)//一定要初始化
            Gstring[i]="";
    }
    //该非终结符的在vector中的
    //获取head
    //通过set向vector的过渡避免重复，使用递归查找head集
    Set<Character> get_head(Character ch){
        Set<Character> temp =new HashSet<>();
        for(int j=0;j<Gnumber;j++)
            if(ch==G[j].get(0)) {
                if(vt.contains(G[j].get(1)))//终结符 直接加入
                    temp.add((Character) G[j].get(1));
                if (vn.contains((Character) G[j].get(1))) {//非终结符
                    if(G[j].get(1)!=ch) {
                        Iterator iter = get_head((Character) G[j].get(1)).iterator();
                        while (iter.hasNext())
                            temp.add((Character) iter.next());
                    }
                    if(G[j].size()>2&&vt.contains(G[j].get(2)))
                        temp.add(G[j].get(2));
                }
            }

        return temp;
    }
    //获取head集
    void get_headset(){
        char ch;//后续通过下标找对应的数符
        for(int i=0;i<v_n.size();i++)
        {
            ch=v_n.get(i);
            Iterator iter = get_head((Character)ch).iterator();
            while (iter.hasNext())
                head_set[i].add((Character) iter.next());
        }
    }
    //获取last
    Set<Character>  get_last(Character ch){
        Set<Character> temp = new HashSet<>();
        for(int j=0;j<Gnumber;j++)
            if(ch==G[j].get(0)) {//先将产生式的最后一个符 包括进去
                if(vt.contains(G[j].get(G[j].size()-1)))//如果是最后一个则包括入内
                    temp.add((Character) G[j].get(G[j].size()-1));
                if (vn.contains((Character) G[j].get(G[j].size()-1))) {//如果最后一个数符式非终结符
                    if(ch!=G[j].get(G[j].size()-1)){//避免重复循环
                        if(G[j].get(G[j].size()-1)!=ch) {
                            Iterator iter = get_last((Character) G[j].get(G[j].size() - 1)).iterator();
                            while (iter.hasNext())
                                temp.add((Character) iter.next());
                        }
                        if(G[j].size()>2&&vt.contains(G[j].get(G[j].size()-2)))
                            temp.add(G[j].get(G[j].size()-2));
                    }
                }
            }
        return temp;
    }
    //获取last集
    void get_lastset(){
        char ch;//后续通过下标找对应的数符
        for(int i=0;i<v_n.size();i++)
        {
            ch=v_n.get(i);//注意修改为终结符
            Iterator iter = get_last((Character)ch).iterator();
            while (iter.hasNext())
                last_set[i].add((Character) iter.next());
        }
    }
    int get_index(Character ch){
        if(ch=='#') return 29;
        return v_t.indexOf(ch);
    }
    /**
     0表示相等
     1<
     2>
     -1表示错误
     * */
    void get_mtable() {
        set_to_vector();
        //get_headset();
        //get_lastset();
        int p1, p2;
        int s = v.size();
        for (int i = 0; i < Gnumber; i++)//每次查看一个产生式
            for (int j = 1; j < G[i].size() - 1; j++)//每一行从下标为1处开始查看
            { //每次查看两个符号
                //相邻
                //注意右边产生式只有一个符号则不必往右看
                if (G[i].size() == 2) break;
                //第一个数是非终结
                if (!vt.contains(G[i].get(j))) {
                    if (vt.contains(G[i].get(j + 1))) {
                        p2 = get_index(G[i].get(j + 1));
                        Iterator iter = get_last((Character) G[i].get(j)).iterator();
                        while (iter.hasNext()) {
                            p1 = get_index((Character) iter.next());
                            mtable[p1][p2] = 2;
                        }
                    }
                }
                //第一和第二个数是终结
                else if (vt.contains(G[i].get(j + 1))) {
                    p1 = v_t.indexOf(G[i].get(j));
                    p2 = v_t.indexOf(G[i].get(j + 1));
                    mtable[p1][p2] = 0;
                }
                //第二个符号为非终结
                else {
                    //确保不越界
                    if ((j + 2) < G[i].size() && vt.contains(G[i].get(j + 2))) {
                        p1 = v_t.indexOf(G[i].get(j));
                        p2 = v_t.indexOf(G[i].get(j + 2));
                        mtable[p1][p2] = 0;
                    }
                        p1 = v_t.indexOf(G[i].get(j));
                        Iterator iter = get_head((Character) G[i].get(j + 1)).iterator();
                        while (iter.hasNext()) {
                            p2 = get_index((Character) iter.next());
                            mtable[p1][p2] = 1;
                        }

                }
            }

    }
    //读文法
    void get_g() throws IOException {
        Reader reader = new FileReader("src/input.txt");//定义一个file对象，用来初始化FileReader
        char temp;
        for(int i=0;i<Gnumber;i++){
            while(true) {
                temp= (char) reader.read();
                while(temp=='>')
                    temp=(char)reader.read();
                if(temp!='$')
                {
                    G[i].add(temp);
                    v.add(temp);//先记录所有的符号 最后去掉非终结符即得终结符
                    vt.add(temp);
                }
                else break;
            }
            vn.add(G[i].get(0));//非终结符
        }
        Iterator iter = vn.iterator();
        while(iter.hasNext())
            vt.remove(iter.next());
    }
    //产生式右边全变为string
    void G_to_string(){
        for(int i=0;i<Gnumber;i++)
            for(int j=1;j<G[i].size();j++)
                if(G[i].get(j)=='+'||G[i].get(j)=='-'||G[i].get(j)=='/'||G[i].get(j)=='*'||G[i].get(j)=='='||G[i].get(j)=='('||G[i].get(j)==')')
                Gstring[i]+=G[i].get(j);
                else Gstring[i]+='N';
    }
    //求得句柄的下标
    int equal_index(String str){
        for(int i=0;i<Gstring.length;i++)
            if(str.equals(Gstring[i]))
                return i;

        return -1;
    }
    //判断句子是否是该文法的语言
    boolean judge() throws IOException {

        File file = new File("src/input_sentence.txt");//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        int length = (int) file.length();
        //这里定义字符数组的时候需要多定义一个,因为词法分析器会遇到超前读取一个字符的时候，如果是最后一个
        //字符被读取，如果在读取下一个字符就会出现越界的异常
        char buf[] = new char[length];
        reader.read(buf);

        G_to_string();
        Stack<Character> an_stack=new Stack<>();
        Stack<Character> temp_stack=new Stack<>();
        Stack<Character> in_stack=new Stack<>();
        an_stack.push('#');
        in_stack.push('#');
        char an_temp;
        char in_temp;
        char temp;//在规约时用
        int x,y;//记录mtable中的位置
        int index1=0;//记录<站内位置
        int index2=0;//记录>时栈顶位置
        int an_size=an_stack.size();//记录栈的大小
        for(int i=buf.length-1;i>=0;i--)
        {
            in_stack.push(buf[i]);
        }
        if(in_stack.get(in_stack.size()-2)!='=') return false;
        while(true)
        {
            an_size=an_stack.size();
            int j=an_size;//记录栈的第一个终结符位置
            an_temp=an_stack.peek();
            in_temp=in_stack.peek();
            index2=an_size-1;//栈顶下标
            if(an_temp!='#'&&!vt.contains(an_temp))
            {
                j--;
                an_temp=an_stack.get(j-1);//找到分析栈中的非终结符
            }
            else an_temp=an_stack.get(j-1);

            x=get_index(an_temp);
            y=get_index(in_temp);

            if(mtable[x][y]==0||mtable[x][y]==1)
                an_stack.push(in_stack.pop());
            else if(mtable[x][y]==2)
            {


               while(mtable[x][y]!=1)
               {
                   temp=an_temp;
                   y=get_index(temp);
                   j--;//此处的j用来循环
                   an_temp=an_stack.get(j-1);
                  if(an_temp!='#'&&!vt.contains(an_temp))
                      j--;
                  an_temp=an_stack.get(j-1);
                  x=get_index(an_temp);
               }
                index1=j-1;//一定是j-1 不能getIndex
                index2=an_stack.size()-1;
                String str="";
                char ch_temp;
                for(int round=0;round<(index2-index1);round++)
                {

                    ch_temp=an_stack.pop();
                    if(ch_temp=='+'||ch_temp=='-'||ch_temp=='*'||ch_temp=='/'||ch_temp=='='||ch_temp=='('||ch_temp==')')
                    temp_stack.push(ch_temp);
                    else  temp_stack.push('N');
                //最左素短语
                }
                int time=temp_stack.size();
                for(int t=0;t<time;t++)
                    str+=temp_stack.pop();
                int index=equal_index(str);//求得产生式左部
                if(index<0)
                    return false;//求产生式左部
                an_stack.push('N');

            }
            else return false;
            //0=  1< 2>
            if(an_stack.peek()=='N'&&an_stack.size()==2&&in_stack.size()==1)
                return true;
        }

    }
    void set_gnumber(){
        Gnumber=10;
    }
    public static void main(String[] args) throws IOException {
        File file = new File("src/sentence.txt");//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        int length = (int) file.length();
        //这里定义字符数组的时候需要多定义一个,因为词法分析器会遇到超前读取一个字符的时候，如果是最后一个
        //字符被读取，如果在读取下一个字符就会出现越界的异常
        char buf[] = new char[length+1];
        reader.read(buf);
        reader.close();
        new Lexical().getSym(buf);
        //语法分析
        syntax s=new syntax();
        s.set_gnumber();
        s.get_g();
        s.get_mtable();

        if(s.judge())
            System.out.println("是该文法的句子");
        else System.out.println("不是该文法的句子");
        // write your code here
    }
}

