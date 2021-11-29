package com.razielez.settings;

import com.razielez.constants.Constants;
import com.razielez.utils.JsonTools;
import com.razielez.utils.Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.SneakyThrows;

public abstract class LoadSetting {

  @SneakyThrows
  public ClientSetting loadClientSetting() {
    String config = readConfig();
    return JsonTools.fromJson(config, ClientSetting.class);
  }

  @SneakyThrows
  public ServerSetting loadServerSetting() {
    String config = readConfig();
    return JsonTools.fromJson(config, ServerSetting.class);
  }

  @SneakyThrows
  public void genDefaultConfig() {
    final String serverIp = Utils.getRemoteIp();
    final String password = UUID.randomUUID().toString();
    final Integer port = 9090;
    ClientSetting clientSetting = defaultClientSetting(serverIp, password, port);
    writeConfig(clientSetting);
  }

  private void writeConfig(Setting setting) {
  }

  public ClientSetting defaultClientSetting(
      final String serverIp,
      final String password,
      final Integer proxyPort
  ) {

    ClientSetting clientSetting = new ClientSetting();
    clientSetting.setServerHost(serverIp);
    clientSetting.setPassword(password);
    clientSetting.setProxyPort(proxyPort);
    clientSetting.setRemotePort(8080);
    clientSetting.setServerPort(proxyPort);
    return clientSetting;
  }


  private String readConfig() throws IOException {
    String path = System.getenv().get(Constants.CONFIG_VARIABLE);
    return new String(Files.readAllBytes(Paths.get(path)));
  }
}

