package com.razielez;

import com.razielez.codec.MessageType;
import java.util.Map;

public class Tst {

  public static void main(String[] args) {
    RecordClass recordClass = RecordClass.create(MessageType.HEARTBEAT, null, null);
  }

}

record RecordClass(
    Long id,
    MessageType type,
    Map<String, Object> metaData,
    byte[] data
) {


  public static RecordClass create(
      MessageType type,
      Map<String, Object> metaData,
      byte[] data
  ) {
    return new RecordClass(0L, type, metaData, data);
  }


}
