package org.apache.paimon.web.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/** log util */
public class LogUtil {
  private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

  public static String getError(Throwable e) {
    String error = null;
    try (StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw)) {
      e.printStackTrace(pw);
      error = sw.toString();
      logger.error(error);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      return error;
    }
  }

  public static String getError(String msg, Throwable e) {
    String error = null;
    try (StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw)) {
      e.printStackTrace(pw);
      LocalDateTime now = LocalDateTime.now();
      error = now + ": " + msg + " \nError message:\n " + sw.toString();
      logger.error(error);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      return error;
    }
  }
}
