package db

import javax.inject.{Inject, Singleton}

import data.{DASUser, DASUserOps}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

trait DASUserModule extends SlickModule {

  import driver.api._

  val DASUsers = TableQuery[DASUserTable]

  class DASUserTable(tag: Tag) extends Table[DASUser](tag, "das_user") {

    def id = column[Long]("id", O.PrimaryKey)

    def name = column[String]("name")

    def password = column[String]("password")

    def * = (id, name, password) <>(DASUser.tupled, DASUser.unapply)
  }

}

@Singleton
class DASUserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext) extends DASUserModule with DASUserOps {

  import driver.api._

  override def validate(username: String, password: String): Future[Option[DASUser]] = db.run {
    DASUsers.filter(u => u.name === username && u.password === password).result.headOption
  }

  override def byId(id: Long): Future[Option[DASUser]] = db.run(DASUsers.filter(_.id === id).result.headOption)

  override def byName(s: String): Future[Option[DASUser]] = db.run(DASUsers.filter(u => u.name === s).result.headOption)
}