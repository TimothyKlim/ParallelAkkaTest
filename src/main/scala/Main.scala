import akka.actor._
import akka.util.Timeout
import concurrent.{Promise, Future, Await}
import concurrent.util.Duration
import concurrent.util.duration._
import collection.mutable.ListBuffer
import akka.pattern.after
import concurrent.ExecutionContext
import java.util.concurrent.{Executor, Executors}
import sys.process._

object Main extends App {
  implicit val system = ActorSystem("test")

  case class TestActors()
  case class Work()
  case class Done()

  class SlugActor(val slugID: Int) extends Actor with ActorLogging {
    val executorThreadPool = Executors.newCachedThreadPool()

    implicit val executor: Executor with ExecutionContext = ExecutionContext.fromExecutor(executorThreadPool)

    override def postStop() {
      executorThreadPool.shutdown()
    }

    def receive = {
      case Work => {
        val senderRef = sender

        val future = Future {
          log.info("%d. start working" format slugID)

          Thread.sleep(100000)

          true
        }

//        Await.result(future, 1 second)
        after(1 second, using = system.scheduler)(future) onComplete {
          case Right(result) =>
            log.info("%d. stop working with result: %s" format(slugID, result))
            senderRef ! Done
          case Left(e) =>
            log.error("%d. timeout" format slugID)
            senderRef ! Done
        }
//
//        println("received")
      }
    }
  }

  val actorsCount: Int = 32

  val hypervisor = system.actorOf(Props(new Actor {
    val slugActors = new ListBuffer[ActorRef]()

    var doneCounter = 0

    override def preStart() {
      for (id <- 1 to actorsCount) {
        slugActors += context.actorOf(Props(new SlugActor(id)))
      }
    }

    def receive = {
      case TestActors => slugActors.foreach(_ ! Work)
      case Done =>
        doneCounter += 1

        println("doneCounter: %d" format doneCounter)

        if (doneCounter == actorsCount) {
          println("SHUTDOWN!")
          system.shutdown()
        }
    }
  }))

  hypervisor ! TestActors
}
