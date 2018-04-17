package com.dang.practice.apitest.rpc.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;  
  

  
public class UserServiceImpl extends UnicastRemoteObject  implements UserService {  
  
    /** 
     *  
     */  
    private static final long serialVersionUID = 6222175854495075991L;  
  
    public UserServiceImpl() throws RemoteException {  
        super();  
        // TODO Auto-generated constructor stub  
    }  
  

    public User getUserById(String id) {  
        User user=new User();  
        user.setId(id);  
        user.setAge(100);  
        user.setName("测试");  
        return user;  
    }  
  
}  