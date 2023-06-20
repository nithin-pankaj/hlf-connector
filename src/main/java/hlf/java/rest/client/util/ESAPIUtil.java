package hlf.java.rest.client.util;

import org.apache.commons.io.IOUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ESAPIUtil {

  public static Object stripXSSForObject(Object object) {

    if(Objects.isNull(object)) {
      return object;
    }

    if (object instanceof String) {
      String value = (String) object;
      return stripXSS(value);
    }
    else if (object instanceof MultipartFile) {
      MultipartFile file = (MultipartFile) object;
      try {
        InputStream inputStream = file.getInputStream();
        String sanitizedStringContent = stripXSS(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        return new SanitizedMultipartFile(file, sanitizedStringContent.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else {
      Class<?> objectClass = object.getClass();
      Field[] fields = objectClass.getDeclaredFields();
      for (Field field : fields) {
        if (field.getType() == String.class) {
          String fieldName = field.getName();
          String capitalizedFieldName = capitalize(fieldName);
          try {
            Method getter = objectClass.getMethod("get" + capitalizedFieldName);
            Method setter = objectClass.getMethod("set" + capitalizedFieldName, String.class);

            String fieldValue = (String) getter.invoke(object);
            if (fieldValue != null) {
              String sanitizedValue = stripXSS(fieldValue);
              setter.invoke(object, sanitizedValue);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      return object;
    }

    return object;
  }

  private static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
  }

  public static String stripXSS(String value) {
    return ESAPI.encoder().encodeForHTML(value);
  }

  private static class SanitizedMultipartFile implements MultipartFile {
    private final MultipartFile file;
    private final byte[] sanitizedBytes;

    public SanitizedMultipartFile(MultipartFile file, byte[] sanitizedBytes) {
      this.file = file;
      this.sanitizedBytes = sanitizedBytes;
    }

    @Override
    public String getName() {
      return file.getName();
    }

    @Override
    public String getOriginalFilename() {
      return file.getOriginalFilename();
    }

    @Override
    public String getContentType() {
      return file.getContentType();
    }

    @Override
    public boolean isEmpty() {
      return file.isEmpty();
    }

    @Override
    public long getSize() {
      return sanitizedBytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
      return sanitizedBytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return file.getInputStream();
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
      file.transferTo(dest);
    }
  }
}
