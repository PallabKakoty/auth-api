
/**
  * @author pallab
  */

package globals

import javax.inject.Inject

import play.api.Play
import play.api.Logger
//import utils.Log

class Configurations @Inject()(configuration: play.api.Configuration) {
  //for logging
  private implicit val logAddress = "Configurations"

  object app {

    lazy val env = configuration.getString("env").getOrElse("local")

    lazy val hostname = configuration.getString("hostname").getOrElse("localhost:9000")

    lazy val node: Int = configuration.getInt("node").getOrElse {
      Logger.debug("Node is not defined in conf. Shutting down system")
      System.exit(1)
      0
    }
    val actorNodeSupervisorName = "Supervisor"
  }

}
