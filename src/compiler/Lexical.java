package compiler;

import java.io.*;

public class Lexical {
    private String keyWord[] = {"const","var","procedure","begin","end","odd","if",
            "then","call","while","do","read","write"};
    private char ch;
    //判断是否是关键字
    boolean isKey(String str)
    {
        for(int i = 0;i < keyWord.length;i++)
        {
            if(keyWord[i].equals(str))
                return true;
        }
        return false;
    }
    //判断是否是字母
    boolean isLetter(char ch)
    {
        if(ch >= 'a' && ch<= 'z')
            return true;
        else
            return false;
    }
    //判断是否是数字
    boolean isNum(char num)
    {
        if(num >= '0' && num <= '9')
            return true;
        else
            return false;
    }
    //词法分析
    void getSym(char[] chars) throws IOException
    {
        File f = new File("src/input_sentence.txt");
        FileOutputStream fop = new FileOutputStream(f);
        // 构建FileOutputStream对象,文件不存在会自动新建

        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
        for(int i = 0;i< chars.length;i++) {
            String arr = "";
            ch = chars[i];
            while(ch == ' '||ch == '\t'||ch == '\n'||ch == '\r')//忽略空格、换行、回车和Tab
            {ch = chars[++i];}
            if(isLetter(ch)){//变量名以字母开头 其中可能包含数字
                while(isLetter(ch)||isNum(ch)){
                    arr += ch; //arr的拓展要放在前面
                    ch = chars[++i];
                }
                //注意此处回退一个字符 对应之前++i操作
                i--;
                if(isKey(arr)){
                    //关键字 置保留字的种别至sym
                    writer.append(arr+"\t"+3+"\t关键字"+"\n");
                }
                else{
                    //标识符
                    writer.append('v');
                }
            }
            else
            { 	 //无符号数的读取
                if(isNum(ch))
                {   double d=0;
                    double N=0;
                    double P=0;int e=1;
                    double t=0;//存储最终的结果
                    while(isNum(ch)) {
                        i++;
                        arr += ch;//存字符串
                        d=ch-48;//存实际的数值
                        N=10*N+d;
                        ch=chars[i];
                    }
                    if(ch=='.') {
                        arr += ch;
                        ch=chars[++i];
                        int j=0;
                        while(isNum(ch)) {
                            j++;arr += ch;
                            d=ch-48;
                            for(int i1=j;i1>0;i1--)
                                d*=0.1;
                            N=N+d;
                            ch=chars[++i];
                        }
                        if(ch=='e') {
                            arr += ch;
                            ch=chars[++i];
                            if(ch=='-') e=-1;
                            arr += ch;
                            ch=chars[++i];
                            while(isNum(ch)) {
                                arr += ch;
                                d=ch-48;
                                P=10*P+d;
                                ch=chars[++i];
                            }
                        }

                    }
                    t=N*Math.pow(10,P*e);
                    writer.append('c');
                    i--;//注意此处的回退 因为会向后判断一位
                }

                else switch(ch){
                    //运算符
                    case '+': writer.append(ch);break;
                    case '-': writer.append(ch);break;
                    case '*': writer.append(ch);break;
                    case '/': writer.append(ch);break;
                    case '#': writer.append(ch+"\t"+4+"\t运算符"+"\n");break;
                    //分界符
                    case '(': writer.append(ch);break;
                    case ')': writer.append(ch);break;
//                case '[': writer.append(ch+" "+5+"\t分界符"+"\n");break;
//                case ']': writer.append(ch+" "+5+"\t分界符"+"\n");break;
//                case ';': writer.append(ch+" "+5+"\t分界符"+"\n");break;
//                case '{': writer.append(ch+" "+5+"\t分界符"+"\n");break;
//                case '}': writer.append(ch+" "+5+"\t分界符"+"\n");break;
                    //运算符
                    case '=':{
                        ch = chars[++i];
                        if(ch == '=') writer.append("=="+"\t"+4+"\t运算符"+"\n");
                        else {
                            writer.append("=");
                            i--;
                        }
                    }break;
                    case ':':{
                        ch = chars[++i];
                        if(ch == '=') writer.append(":="+"\t"+4+"\t运算符"+"\n");
                        else {
                            writer.append(":"+"\t"+4+"\t运算符"+"\n");
                            i--;
                        }
                    }break;
                    case '>':{
                        ch = chars[++i];
                        if(ch == '=') writer.append(">=\t"+4+"\t运算符"+"\n");
                        else {
                            writer.append(">\t"+4+"\t运算符"+"\n");
                            i--;
                        }
                    }break;
                    case '<':{
                        ch = chars[++i];
                        if(ch == '=') writer.append("<="+"\t"+4+"\t运算符"+"\n");
                        else {
                            writer.append("<"+"\t"+4+"\t运算符"+"\n");
                            i--;
                        }
                    }break;
                    //无识别
                    // default:  writer.append(ch+"\t6"+"\t无识别符");
                }
            }
        }
        writer.close();
        fop.close();
    }
    public static void main(String[] args) throws IOException {

    }
}
