import akka.actor._
import akka.dispatch.{Await, Future}
import akka.util.Timeout
import akka.util.duration._
import collection.mutable.ListBuffer

object Main extends App {
  implicit val system = ActorSystem("test")

  case class TestActors()
  case class Work()
  case class Done()

  class SlugActor(val slugID: Int) extends Actor with ActorLogging {
    def receive = {
      case Work => {
        implicit val futureTimeout = Timeout(15 seconds)

        val future = Future {
          log.info("%d. start working" format slugID)
          Thread.sleep(10000)

          true
        }

        try {
          val result = Await.result(future, futureTimeout.duration)

          log.info("%d. stop working with result: %s" format(slugID, result))
        } catch {
          case e: java.util.concurrent.TimeoutException => log.error("%d. timeout" format slugID)
        }

        sender ! Done
      }
    }
  }

  val hypervisor = system.actorOf(Props(new Actor {
    val slugActors = new ListBuffer[ActorRef]()

    var doneCounter = 0

    override def preStart() {
      for (id <- 1 to 32) {
        slugActors += context.actorOf(Props(new SlugActor(id)).withDispatcher("slug-actors-dispatcher"))
      }
    }

    def receive = {
      case TestActors => slugActors.foreach(_ ! Work)
      case Done =>
        doneCounter += 1

        if (doneCounter == 32) {
          system.shutdown()
        }
    }
  }))

  hypervisor ! TestActors
}