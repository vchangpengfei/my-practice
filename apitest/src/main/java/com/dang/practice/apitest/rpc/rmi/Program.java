package com.dang.practice.apitest.rpc.rmi;

import java.net.MalformedURLException;
import java.nio.channels.AlreadyBoundException;  
import java.rmi.Naming;    
import java.rmi.RemoteException;  
import java.rmi.registry.LocateRegistry;    
  

    
public class Program{    
    public static void main(String[] args) {    
        // TODO Auto-generated method stub    
        try {    
            UserService userService=new UserServiceImpl();    
            //注册通讯端口    
            LocateRegistry.createRegistry(6600);    
            //注册通讯路径    
            Naming.rebind("rmi://127.0.0.1:6600/userService", userService);    
            System.out.println("Service Start!");    
        }  catch (RemoteException e) {  
            System.out.println("创建远程对象发生异常！");  
            e.printStackTrace();  
        } catch (AlreadyBoundException e) {  
            System.out.println("发生重复绑定对象异常！");  
            e.printStackTrace();  
        } catch (MalformedURLException e) {  
            System.out.println("发生URL畸形异常！");  
            e.printStackTrace();  
        }   
    }    
}    