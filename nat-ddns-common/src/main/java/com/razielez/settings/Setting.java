package com.razielez.settings;

import lombok.Data;

@Data
abstract public class Setting {

  private Integer serverPort = 9090;
  private String password;

}
