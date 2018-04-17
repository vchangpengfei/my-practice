package com.dang.practice.apitest.rpc.rmi;

import java.rmi.Naming;

public class Client {
     public static void main(String[] args){    
            try{    
                //调用远程对象，注意RMI路径与接口必须与服务器配置一致    
                UserService userService=(UserService) Naming.lookup("rmi://127.0.0.1:6600/userService");
                User user =userService.getUserById("1245");  
                 System.out.println(user.getName());  
            }catch(Exception ex){    
                ex.printStackTrace();    
            }    
        }    
}  