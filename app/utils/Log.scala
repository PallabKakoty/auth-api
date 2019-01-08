package utils

/**
 * @author pallab
 */
import org.joda.time.LocalDateTime
import play.api.Logger


object Log {
    
    private val loglevel = new scala.collection.mutable.HashMap[String, Boolean]
    /**
     * interface to set log level remotely
     * in a running application
     */
    def setLogLevel(source: String, level: Boolean ) = {
      loglevel.put(source, level)
    }
    private def getLogLevel(key: String) = {
      loglevel.get(key).getOrElse(true)
    }
    def debug(msg: String)(implicit logAddress: String) = if(getLogLevel("debug") && getLogLevel(logAddress)){
     // Logger.debug(LocalDateTime.now() + " : " +Configuration.app.node+" : "+ source + " : " + msg)
    }
    def error( msg: String)(implicit logAddress: String) = if(getLogLevel("error") && getLogLevel(logAddress)){
      Logger.error(LocalDateTime.now() + " : " + logAddress + " : " + msg)
    }
    def info(msg: String)(implicit logAddress: String) = if(getLogLevel("info") && getLogLevel(logAddress)){
      Logger.info(LocalDateTime.now() + " : " + logAddress + " : " + msg)
    }
    def warn(msg: String)(implicit logAddress: String) = if(getLogLevel("warn") && getLogLevel(logAddress)){
      Logger.warn(LocalDateTime.now() + " : " + logAddress + " : " + msg)
    }
  
}