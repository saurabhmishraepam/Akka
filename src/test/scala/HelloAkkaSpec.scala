import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, Matchers }
import akka.actor.{ Actor, Props, ActorSystem }
import akka.testkit.{ ImplicitSender, TestKit, TestActorRef }
import scala.concurrent.duration._

class HelloAkkaSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("HelloAkkaSpec"))

  override def afterAll: Unit = {
  }

  "An HelloAkkaActor" should "be able to set a new greeting" in {
  }

  it should "be able to get a new greeting" in {
    val greeter = system.actorOf(Props[Greeter], "greeter")
    greeter ! WhoToGreet("testkit")
    greeter ! Greet
    expectMsgType[Greeting].message.toString should be("hello, testkit")
  }
}
