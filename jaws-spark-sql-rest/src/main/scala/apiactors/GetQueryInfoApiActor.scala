package apiactors

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import messages.ErrorMessage
import messages.GetQueryInfoMessage
import com.xpatterns.jaws.data.contracts.DAL
import server.Configuration
import akka.actor.Actor
import com.google.common.base.Preconditions
import com.xpatterns.jaws.data.DTO.Query

import scala.util.Try

/**
 * Created by emaorhian
 */
class GetQueryInfoApiActor(dals: DAL) extends Actor {

  override def receive = {

    case message: GetQueryInfoMessage => {
      Configuration.log4j.info("[GetQueryInfoApiActor]: retrieving the query information for " + message.queryIDs)
      val currentSender = sender

      val getQueryInfoFuture = future {
        message.queryIDs map (queryID => new Query(dals.loggingDal.getState(queryID).toString,
          queryID, dals.loggingDal.getScriptDetails(queryID),
          dals.loggingDal.getMetaInfo(queryID)))

      }

      getQueryInfoFuture onComplete {
        case Success(result) => currentSender ! result
        case Failure(e) => currentSender ! ErrorMessage(s"GET query info failed with the following message: ${e.getMessage}")
      }

    }
  }
}
