package com.dang.practice.apitest.rpc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;  
  

  
public interface UserService extends Remote {  
    public User getUserById(String id)throws RemoteException;  
}  