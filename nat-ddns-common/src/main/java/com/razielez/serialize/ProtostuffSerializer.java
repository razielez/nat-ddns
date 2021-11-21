package com.razielez.serialize;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ProtostuffSerializer implements Serializer {

  public static final ProtostuffSerializer INSTANCE = new ProtostuffSerializer();

  private static final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
  private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  private static <T> Schema<T> getSchema(Class<T> clazz) {
    Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
    if (schema == null) {
      schema = RuntimeSchema.getSchema(clazz);
      if (schema == null) {
        schemaCache.put(clazz, schema);
      }
    }
    return schema;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> byte[] serialize(T obj) {
    Class<T> clazz = (Class<T>) obj.getClass();
    Schema<T> schema = getSchema(clazz);
    byte[] data;
    try {
      data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    } finally {
      buffer.clear();
    }
    return data;
  }

  @Override
  public <T> Object deserialize(byte[] input, Class<T> clz) {
    Schema<T> schema = getSchema(clz);
    T obj = schema.newMessage();
    ProtostuffIOUtil.mergeFrom(input, obj, schema);
    return obj;
  }
}
