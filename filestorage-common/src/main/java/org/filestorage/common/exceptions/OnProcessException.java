package org.filestorage.common.exceptions;

import org.filestorage.common.Constants;

public class OnProcessException extends Exception {
  private static final long serialVersionUID = -290443469638741939L;

  public OnProcessException(String action, byte serverResponse) {
    super(String.format("Client action: %s. Server response code: %d (%s).",
        action, serverResponse, Constants.getConstantName(serverResponse)));
  }
  
  public OnProcessException(String msg) {
    super(msg);
  }
    
  public OnProcessException() { 
    super();
  }
}
