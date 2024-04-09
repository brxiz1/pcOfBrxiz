import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    ServerSocket ss;
    private static Socket[] socketList;
    int maxSize=5;
    private static Account[] accountList;
    public Server() throws IOException {
        ss=new ServerSocket(9999);
        socketList=new Socket[maxSize];
        accountList=new Account[100];
    }

    private static ReadWriteLock rwLockSocket=new ReentrantReadWriteLock();
    private static Lock rlockSocket= rwLockSocket.readLock();
    private static Lock wlockSocket= rwLockSocket.writeLock();

    private static ReadWriteLock rwLockAccount=new ReentrantReadWriteLock();
    private static Lock rlockAccount= rwLockAccount.readLock();
    private static Lock wlockAccount= rwLockAccount.writeLock();
    public static Socket getSocketList(int i) {
        rlockSocket.lock();
        try{
            return socketList[i];
        }finally{
            rlockSocket.unlock();
        }
    }

    public static void setSocketList(int i,Socket socket) {
        wlockSocket.lock();
        try{
            socketList[i]=socket;
        }finally{
            wlockSocket.unlock();
        }
    }

    public static Account getAccountList(int i) {
        rlockAccount.lock();
        try{
            return accountList[i];
        }finally{
            rlockAccount.unlock();
        }
    }
    public static int isExistAccountList(String name,String psw) {
        rlockAccount.lock();
        try{
            boolean isExist=false;
            for(Account ac:accountList){
                if(ac!=null){
                    if(ac.getName().equals(name)){
                        isExist=true;
                        if(ac.getPassword().equals(psw)) return 1;//登录成功
                        break;
                    }
                }
            }
            if(isExist){
                return -1;//密码错误
            }else return 0;//账号不存在
        }finally{
            rlockAccount.unlock();
        }
    }
    public static boolean setAccountList(Account account) {
        wlockAccount.lock();
        try{
            for(int j=0;j<100;j++){
                if(accountList[j]==null){
                    accountList[j]=account;
                    try(BufferedWriter bw=new BufferedWriter(new FileWriter("./ChatRoom/src/partners.txt",true))){
                        bw.write(account.getName()+"-"+account.getPassword()+"\n");
                        bw.flush();
                        return true;
                    }catch(IOException e){
                        accountList[j]=null;
                        return false;
                    }
                }
            }
            return false;

        }finally{
            wlockAccount.unlock();
        }
    }

    public void createAccountList(){
        wlockAccount.lock();
        try(BufferedReader br=new BufferedReader(new FileReader("./ChatRoom/src/partners.txt"))){
            String msg;
            int j=0;
            while((msg=br.readLine())!=null){
                String[] msgs=msg.split("-");
                for(;j<100;j++){
                    if(accountList[j]==null){
                        accountList[j]=new Account(msgs[0],msgs[1]);
                        break;
                    }
                }
            }
            for(Account a :accountList){
                if(a!=null){
                    System.out.println(a.getName()+" "+a.getPassword());
                }else{
                    System.out.println("所有账号输出完毕");
                    break;
                }
            }
        }catch(IOException e){
            System.out.println(System.getProperty("user.dir"));
            System.out.println("create accountList error");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally{
            wlockAccount.unlock();
        }
    }

    public void init() throws IOException {
        System.out.println("服务器已启动");
        createAccountList();
        while(true){
            Socket s=ss.accept();
            Thread t;
            int i=0;
            rlockSocket.lock();
            for(i=0;i<5;){
                if(socketList[i]==null){
                    socketList[i]=s;
                    break;
                }
                i++;
            }
            rlockSocket.unlock();
            if(i<5){

                t=new Thread(new SocketThread(i));
                t.start();
                System.out.println("欢迎新同学加入聊天室");
            }else{
                System.out.println("聊天室已满，无法继续加入");
                try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))){
                    bw.write("聊天室已满，无法继续加入\n");
                    bw.flush();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

//    public void init(){
//        Scanner sc=new Scanner(System.in);
//        while(true){
//            System.out.println("----------聊天室---------");
//            System.out.println("""
//                请作出您的选择：
//                1、登陆
//                2、注册
//                3、退出
//                """);
//            int i=sc.nextInt();
//            int root=0;
//            boolean isQuit=false;
//
//            switch(i){
//                case 1:{
//                    try(Reader reader=new FileReader("./partners.txt",StandardCharsets.UTF_8);
//                        BufferedReader bf=new BufferedReader(reader)){
//                        root=loginIn(bf);
//                    }catch(IOException e){
//                        System.out.println(e.getMessage());
//                    }
//                    break;
//                }
//                case 2:createAccount();
//                case 3:{
//                    isQuit=true;
//                    break;
//                }
//
//                default:{
//                    System.out.println("输入错误");
//                    continue;
//                }
//            }
//
//            if(isQuit) break;
//            if(root!=0){
//                switch(root){
//                    case -1:
//                        System.out.println("密码错误");
//                        break;
//                    case -2:
//                        System.out.println("账号不存在");
//                        break;
//                    //登陆成功，注册
//                    default:{
//                        while(true){
//
//                        }
//                    }
//
//                }
//            }
//
//        }
//
//    }
//
//    private int loginIn(BufferedReader bf) throws IOException {
//        System.out.print("昵称：");
//        Scanner sc=new Scanner(System.in);
//        String account=sc.nextLine();
//
//        System.out.print("密码：");
//        String psw=sc.nextLine();
//
//        for(String s= bf.readLine();s!=null;s=bf.readLine()){
//            String[] ss=s.split("-");
//            if(ss.length==3){
//                if(ss[0].equals(account)){
//                    if(ss[1].equals(psw)){
//                        return Integer.parseInt(ss[2]);
//                    }else{
//                        return -1;
//                    }
//                }
//            }
//        }
//        return -2;
//    }
//    private void createAccount(){
//
//        try(Reader reader=new FileReader("./partners.txt",StandardCharsets.UTF_8);
//            BufferedReader br=new BufferedReader(reader);
//            Writer writer=new FileWriter("./partners.txt",true);
//            BufferedWriter bw=new BufferedWriter(writer)
//        ){
//            reader.mark(200);
//            int nextRoot=0;
//            String name=null;
//            Scanner sc=new Scanner(System.in);
//
//            while(true){
//                System.out.println("您想要创建的昵称是：");
//
//                name=sc.nextLine();
//                String s=null;
//
//                if(name.isEmpty() ||name.length()>9){
//                    System.out.println("昵称输入错误");
//                    continue;
//                }
//                boolean isExist=false;
//                while(true){
//                    String s1=br.readLine();
//                    if(s1!=null){
//                        s=s1;
//                        if(s1.split("-")[0].equals(name)){
//                            isExist=true;
//                            break;
//                        }
//                    }else{
//                        break;
//                    }
//                }
//
//                if(!isExist){
//                    System.out.println("昵称已存在");
//                    reader.reset();
//                    continue;
//                }
//
//                if(s!=null){
//                    String[] ss=s.split("-");
//                    nextRoot=Integer.parseInt(ss[2])+1;
//                    break;
//
//                }else{
//                    System.out.println("创建失败");
//                    return;
//                }
//            }
//
//            if(nextRoot!=0){
//                while(true){
//                    System.out.println("请设置密码：");
//                    String psw=sc.nextLine();
//                    if(psw.length()<3){
//                        System.out.println("密码长度过短，请重新设置");
//                        continue;
//                    }else{
//                        String s=name+"-"+psw+"-"+nextRoot;
//                        bw.write(s);
//                        bw.newLine();
//                        System.out.println("注册成功");
//                        return;
//                    }
//                }
//
//
//            }
//
//
//        }catch(IOException e){
//            System.out.println(e.getMessage());
//            return;
//        }
//
//    }
}

class SocketThread implements Runnable {

    int i;
    SocketThread(int i){
        this.i=i;
    }
    @Override
    public void run(){
        System.out.println("server:get connect from "+Server.getSocketList(i).getPort());
        try(BufferedReader reader=new BufferedReader(
                new InputStreamReader(Server.getSocketList(i).getInputStream(),StandardCharsets.UTF_8))) {
            BufferedWriter[] writers=new BufferedWriter[5];
            writers[i]=new BufferedWriter(new OutputStreamWriter(Server.getSocketList(i).getOutputStream()));
            writers[i].write("connect success\n");
            writers[i].flush();
//            for(int j=0;j<sl.length;j++){
//                if(sl[j]!=null) {
//                    writers[j] = new BufferedWriter(new OutputStreamWriter(sl[j].getOutputStream()));
//                }
//            }

            while(true){
                String msg= reader.readLine();
                //login,creat,quit0
                boolean isLogin=false;
                boolean isQuit=false;
                if(msg!=null){
                    String[] msgs=msg.split("-");
                    switch(msgs[0]){
                        case "login":
                            int reslog=Server.isExistAccountList(msgs[1],msgs[2]);
                            if(reslog==1) {
                                isLogin = true;
                            }
                            writers[i].write(reslog + "\n");
                            writers[i].flush();
                            break;
                        case "create":
                            if(Server.isExistAccountList(msgs[1],null)==0){//账号不存在
                                boolean isCreate=Server.setAccountList(new Account(msgs[1],msgs[2]));
                                if(isCreate){
                                    writers[i].write("1\n");
                                    writers[i].flush();
                                }else{
                                    writers[i].write("-1\n");
                                    writers[i].flush();
                                }

                            }else{
                                writers[i].write("0\n");
                                writers[i].flush();
                            }
                            break;
                        case "quit":
                            isQuit=true;
                            Server.setSocketList(i,null);
                            break;
                        default:
                            writers[i].write("指令发送错误\n");
                            writers[i].flush();
                    }
                    if(isQuit)return;
                    if(isLogin)break;
                }else{
                    System.out.println(Server.getSocketList(i).getPort()+"异常退出");
                    Server.setSocketList(i,null);
                    return;
                }

            }
            while(true) {
                String msg=null;
                msg= reader.readLine();
//                try{
//                    msg= reader.readLine();
//                }catch(IOException e){
//                    System.out.println(i+"run "+e.getMessage());
//                    e.printStackTrace();
//                    System.out.println(sl[i].isConnected()+" "+sl[i].isClosed());
//                    break;
//                }

                if("quit".equals(msg)){
                    Server.setSocketList(i,null);
                    writers[i]=null;
//                    for(int j=0;j<5;j++){
//                        if(sl[j]!=null){
//                            System.out.println("quit01:"+j+" "+sl[j].isClosed());
//                        }
//
//                    }
                    break;
                }
                msg=Server.getSocketList(i).getPort()+":"+msg;
                System.out.println(msg);
                for(int j=0;j<5;j++){
                    if(j==i){
                        continue;
                    }
                    if(Server.getSocketList(i)!=null){

                        try{
                            if(writers[j]==null){
                                writers[j] = new BufferedWriter(new OutputStreamWriter(Server.getSocketList(j).getOutputStream()));
                            }
                            writers[j].write(msg+"\n");
                            writers[j].flush();
                        }catch(IOException e){
                            System.out.println("sl[]"+j+" "+(Server.getSocketList(i)==null)+Server.getSocketList(i).isConnected()+Server.getSocketList(i).isClosed());
                            System.out.println(Server.getSocketList(i).getPort()+e.getMessage());
                        }
//                        try(BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(sl[j].getOutputStream()))){
//                            writer.write(msg+"\n");
//                            writer.flush();
//                        }
                    }
                }
            }
//            for(int j=0;j<5;j++){
//                if(Server.getSocketList(j)!=null){
//                    System.out.println("quit02:"+j+" "+Server.getSocketList(j).isClosed());
//                }
//            }
//            for(int j=0;j<sl.length;j++){
//                if(sl[j]!=null) {
//                    sl[j].shutdownOutput();//执行这里的close方法会导致writer对应的Socket在服务器上被关闭
//                }
//            }
//            for(int j=0;j<5;j++){
//                if(sl[j]!=null){
//                    System.out.println("quit03:"+j+" "+sl[j].isClosed());
//                }
//            }
        }catch(IOException e){//服务器抛出IOException大概是因为访问了已关闭的Socket的IO
            System.out.println(i+"run "+e.getMessage());
            e.printStackTrace();
        }
//        while(true){ new InputStreamReader(sl[i].getInputStream(),StandardCharsets.UTF_8))){
//            try(BufferedReader reader=new BufferedReader(
//                    new InputStreamReader(sl[i].getInputStream(),StandardCharsets.UTF_8))){
//                String msg= reader.readLine();
//                if("quit".equals(msg)){
//                    return;
//                }
//                msg=sl[i].getLocalPort()+":"+msg;
//                System.out.println(msg);
//                for(int j=0;j<sl.length;j++){
//                    if(j==i){
//                        continue;
//                    }
//                    if(sl[j]!=null){
//                        try(BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(sl[j].getOutputStream()))){
//                            writer.write(msg+"\n");
//                            writer.flush();
//                        }
//                    }
//                }
//
//            }catch(IOException e){
//                System.out.println("run "+e.getMessage());
//                return;
//            }

    }


}


