package com.razielez.settings;

import lombok.Data;

@Data
public final class ClientSetting extends Setting {

  // == connect info
  private String serverHost;
  private String password;

  // == proxy info
  private String proxyHost = "127.0.0.1";
  private Integer proxyPort;

  // == server open info
  private Integer remotePort;


}
