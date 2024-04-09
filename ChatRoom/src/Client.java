import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public void init() {
        Scanner sc=new Scanner(System.in);

            while(true){
                try(Socket s=new Socket("localhost",9999)){//如何做好服务器和客户端间输入输出流的协调？
                    try(BufferedReader reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
                        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))){
                        String isConnected=reader.readLine();
                        if("聊天室已满，无法继续加入".equals(isConnected)){
                            System.out.println(isConnected);
                            return;
                        }
                        while(true) {
                            System.out.println("----------聊天室---------");
                            System.out.println("""
                            请作出您的选择：
                            1、登陆
                            2、注册
                            3、退出
                            """);
                            int i=sc.nextInt();
                            sc.nextLine();
                            int res=0;
                            boolean isQuit=false;
                            switch(i){
                                case 1:{
                                    try{
                                        res=loginIn(writer,reader);
                                        switch(res){
                                            case 1:System.out.println("登陆成功");break;
                                            case 0:System.out.println("账号不存在");break;
                                            case -1:System.out.println("密码错误");break;
                                            default:System.out.println("登陆错误");
                                        }
                                    }catch(IOException e){
                                        System.out.println(e.getMessage());
                                    }
                                    break;
                                }
                                case 2:
                                    switch(createAccount(reader,writer)){
                                        case 1:System.out.println("注册成功");break;
                                        case 0:System.out.println("账号已存在");break;
                                        default:System.out.println("注册错误");
                                    }
                                    break;
                                case 3:{
                                    writer.write("quit-0-0\n");
                                    isQuit=true;
                                    break;
                                }
                                default:{
                                    System.out.println("输入错误");
                                    continue;
                                }
                            }
                            if(res==1){
                                break;
                            }
                            if(isQuit){
                                return;
                            }
                        }


                        Thread hear=new Thread(new Hearing(reader));
                        hear.start();
                        System.out.println("欢迎来到聊天室，请畅所欲言");
                        while(true){
                            String msg=sc.nextLine();
                            System.out.println("repeat:"+msg);
                            if("why".equals(msg)) {
                                System.out.println(s.isClosed()+" "+s.isConnected());
                                continue;
                            }
                            writer.write(msg+"\n");
                            writer.flush();
                            if("quit".equals(msg)){
                                break;//为什么这里quit后其它客户和服务器间也不能正常通信？
                            }
                        }
                        hear.interrupt();
                        System.out.println("已经退出程序");
                        break;
                    }
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
//                System.out.println("----------聊天室---------");
//                System.out.println("""
//                请作出您的选择：
//                1、登陆
//                2、注册
//                3、退出
//                """);
//                int i=sc.nextInt();
//                sc.nextLine();
//                int res=0;
//                boolean isQuit=false;
//
//
//
////            switch(i){
////                case 1:{
////                    try(Reader reader=new FileReader("D:/Code/idea/ChatRoom/src/partners.txt", StandardCharsets.UTF_8);
////                        BufferedReader bf=new BufferedReader(reader)){
////                        root=loginIn(bf);
////                    }catch(IOException e){
////                        System.out.println(e.getMessage());
////                    }
////                    break;
////                }
////                case 2:createAccount();
////                    break;
////                case 3:{
////                    isQuit=true;
////                    break;
////                }
////
////                default:{
////                    System.out.println("输入错误");
////                    continue;
////                }
////            }
//
//            if(isQuit) break;
//
//            switch(root) {
//                case -1:
//                    System.out.println("密码错误");
//                    break;
//                case -2:
//                    System.out.println("账号不存在");
//                    break;
                //登陆成功
//                case 1: {
//                    try(Socket s=new Socket("localhost",9999)){//如何做好服务器和客户端间输入输出流的协调？
//                        try(BufferedReader reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
//                            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))){
//                            Thread hear=new Thread(new Hearing(reader));
//                            hear.start();
//                            System.out.println("欢迎来到聊天室，请畅所欲言");
//                            while(true){
//                                String msg=sc.nextLine();
//                                System.out.println("repeat:"+msg);
//                                if("why".equals(msg)) {
//                                    System.out.println(s.isClosed()+" "+s.isConnected());
//                                    continue;
//                                }
//                                writer.write(msg+"\n");
//                                writer.flush();
//                                if("quit".equals(msg)){
//                                    break;//为什么这里quit后其它客户和服务器间也不能正常通信？
//                                }
//                            }
//                            hear.interrupt();
//                            System.out.println("已经退出程序");
//                            break;
//                        }
//                    }catch(IOException e){
//                        System.out.println(e.getMessage());
//                    }

            }

        }



    private int loginIn(BufferedWriter bw,BufferedReader br) throws IOException {
        System.out.print("昵称：");
        Scanner sc=new Scanner(System.in);
        String name=sc.nextLine();

        System.out.print("密码：");
        String psw=sc.nextLine();

        bw.write("login-"+name+"-"+psw+"\n");
        bw.flush();
        String res=br.readLine();
        return Integer.parseInt(res);

//        for(String s= bf.readLine();s!=null;s=bf.readLine()){
//            String[] ss=s.split("-");
////            System.out.println(ss[0]+","+ss[1]);
//            if(ss.length==2){
//                if(ss[0].equals(account)){
//                    if(ss[1].equals(psw)){
//                        return 1;
//                    }else{
//                        return -1;
//                    }
//                }
//            }
//        }
//        return -2;
    }
    private int createAccount(BufferedReader br,BufferedWriter bw){

        try{
//            reader.mark(200);
            String name=null;
            String psw=null;
            Scanner sc=new Scanner(System.in);

            while(true){
                System.out.println("您想要创建的昵称是：");
                name=sc.nextLine();
                if(name.isEmpty() ||name.length()>9){
                    System.out.println("昵称输入错误");
                    continue;
                }
                break;
//                boolean isExist=false;
//                while(true){
//                    String s1=br.readLine();
//                    if(s1!=null){
////                        s=s1;
//                        if(s1.split("-")[0].equals(name)){
//                            isExist=true;
//                            break;
//                        }
//                    }else{
//                        break;
//                    }
//                }
//                if(isExist){//                    System.out.println("昵称已存在");
//                    reader.reset();
//                    continue;
//                }
//                break;
            }

            while(true){
                System.out.println("请设置密码：");
                psw=sc.nextLine();
                if(psw.length()<3){
                    System.out.println("密码长度过短，请重新设置");
                    continue;
                }
                break;
            }
            bw.write("create-"+name+"-"+psw+"\n");
            bw.flush();
            String res=br.readLine();
            return Integer.parseInt(res);
        }catch(IOException e){
            System.out.println(e.getMessage());
            return -1;
        }

    }

}

class Hearing implements Runnable{
    BufferedReader reader;
    Hearing(BufferedReader br){
        reader=br;
    }
//    Socket socket;
//    Hearing(Socket socket){
//        this.socket=socket;
//    }
    @Override
    public void run(){
        while(!Thread.currentThread().isInterrupted()){
//            try(BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            try{
                String s=reader.readLine();//这里为什么可能连接断开
                if(s!=null){ System.out.println(s);}
//                else{
//                    System.out.println("连接出错，已返回");
//                    return;
//                }
            } catch (IOException e) {
                System.out.println("io");//这里抛出异常可能是因为interrupt()
            }

        }
    }

}

