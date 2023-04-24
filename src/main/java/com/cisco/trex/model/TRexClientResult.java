package com.cisco.trex.model;

public class TRexClientResult<T> {
  private String error;

  private T resultObj;

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public T get() {
    return resultObj;
  }

  public void set(T resultObj) {
    this.resultObj = resultObj;
  }

  public boolean isFailed() {
    return error != null;
  }
}
